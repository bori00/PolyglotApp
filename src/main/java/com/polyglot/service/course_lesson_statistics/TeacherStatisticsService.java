package com.polyglot.service.course_lesson_statistics;

import com.polyglot.model.*;
import com.polyglot.model.DTO.CourseStatisticsDTO;
import com.polyglot.model.DTO.ExtendedTaughtCourseDTO;
import com.polyglot.model.DTO.LessonStatisticsDTO;
import com.polyglot.repository.SupervisedCourseRepository;
import com.polyglot.repository.SupervisedLessonRepository;
import com.polyglot.repository.WordToLearnRepository;
import com.polyglot.service.authentication.AuthenticationService;
import com.polyglot.service.authentication.exceptions.AccessRestrictedToTeachersException;
import com.polyglot.service.lesson_practice.exceptions.LessonNotFoundException;
import com.polyglot.service.right_restrictions.RightVerifier;
import com.polyglot.service.student_course_lesson_management.exceptions.CourseNotFoundException;
import com.polyglot.service.student_course_lesson_management.exceptions.InvalidCourseAccessException;
import com.polyglot.service.teacher_course_lesson_management.TeacherCourseLessonManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service responsible for generating statistics for teachers about their courses.
 */
@Service
public class TeacherStatisticsService {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private SupervisedCourseRepository supervisedCourseRepository;

    @Autowired
    private WordToLearnRepository wordToLearnRepository;

    @Autowired
    private SupervisedLessonRepository supervisedLessonRepository;

    private static final Logger logger = LoggerFactory.getLogger(TeacherStatisticsService.class);

    private final RightVerifier rightVerifier = new RightVerifier();

    /**
     * Generates high-level statistics about a course supervised by the active teacher.
     * @param courseId is the id of the course whose statistics is generated.
     * @return the statistics.
     * @throws AccessRestrictedToTeachersException if the active user is not a teacher.
     * @throws CourseNotFoundException if no course with the requested id exists.
     * @throws InvalidCourseAccessException if the active teacher is not the supervisor of the
     * course.
     */
    public CourseStatisticsDTO getCourseStatistics(Long courseId) throws AccessRestrictedToTeachersException, CourseNotFoundException, InvalidCourseAccessException {
        Teacher teacher = authenticationService.getCurrentTeacher();

        Optional<SupervisedCourse> optCourse = supervisedCourseRepository.findById(courseId);

        if (optCourse.isEmpty()) {
            logger.warn("INVALID REQUEST = attempt to access statistics of course {} that does " +
                    "not" +
                    " exist", courseId);
            throw new CourseNotFoundException();
        }

        SupervisedCourse course = optCourse.get();
        if (!rightVerifier.hasAccessToTheDataOf(teacher, course)) {
            logger.warn("INVALID ACCESS = attempt to access data of course {} by teacher {}, who " +
                            "is not the supervisor of the course",
                    courseId, teacher);
            throw new InvalidCourseAccessException();
        }

        int nrStudents = course.getEnrollments().size();

        List<String> lessonTitles =
                course.getLessons().stream().map(Lesson::getTitle).collect(Collectors.toList());

        List<Double> avgNrOfUnknownWordsPerLesson = new ArrayList<>();

        for (Lesson lesson : course.getLessons()) {
            avgNrOfUnknownWordsPerLesson.add((wordToLearnRepository.findByLesson(lesson).size() / ((double) nrStudents)));
        }

        return new CourseStatisticsDTO(nrStudents, lessonTitles, avgNrOfUnknownWordsPerLesson);
    }

    /**
     * Generates statistics about the results of the students on a certain lesson.
     * @param lessonId is the id of the lesson whose statistics are generated.
     * @return the statistics.
     * @throws AccessRestrictedToTeachersException ifthe actice user is not a teacher.
     * @throws LessonNotFoundException if no lesson with the requested id exists.
     * @throws InvalidCourseAccessException if the active teacher is not the supervisor of the
     * course, which contains the requested lesson.
     */
    public LessonStatisticsDTO getLessonStatistics(Long lessonId) throws AccessRestrictedToTeachersException, LessonNotFoundException, InvalidCourseAccessException {
        Teacher teacher = authenticationService.getCurrentTeacher();

        Optional<SupervisedLesson> optLesson = supervisedLessonRepository.findById(lessonId);
        if (optLesson.isEmpty()) {
            logger.warn("INVALID REQUEST = attempt to access data of lesson {} by teacher {}, but" +
                            " the lesson does not exist",
                    lessonId, teacher);
            throw new LessonNotFoundException();
        }
        SupervisedLesson lesson = optLesson.get();

        if (!rightVerifier.hasAccessToTheDataOf(teacher, lesson)) {
            logger.warn("INVALID ACCESS = attempt to access data of lesson {} by teacher {}, who " +
                            "is not the supervisor of the lesson's course",
                    lessonId, teacher);
            throw new InvalidCourseAccessException();
        }

        Map<String, Long> unknownWordToFrequency = wordToLearnRepository.findByLesson(lesson)
                .stream()
                .collect(Collectors.groupingBy(WordToLearn::getOriginalWord,
                        Collectors.counting()));

        return new LessonStatisticsDTO(lesson.getTitle(), lesson.getIndexInsideCourse(),
                lesson.getCourse().getTitle(),
                unknownWordToFrequency);
    }
}
