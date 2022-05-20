package com.polyglot.service.course_lesson_statistics;

import com.polyglot.model.DTO.CourseStatisticsDTO;
import com.polyglot.model.DTO.ExtendedTaughtCourseDTO;
import com.polyglot.model.Lesson;
import com.polyglot.model.SupervisedCourse;
import com.polyglot.model.Teacher;
import com.polyglot.repository.SupervisedCourseRepository;
import com.polyglot.repository.WordToLearnRepository;
import com.polyglot.service.authentication.AuthenticationService;
import com.polyglot.service.authentication.exceptions.AccessRestrictedToTeachersException;
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

@Service
public class TeacherStatisticsService {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private SupervisedCourseRepository supervisedCourseRepository;

    @Autowired
    private WordToLearnRepository wordToLearnRepository;

    private static final Logger logger = LoggerFactory.getLogger(TeacherStatisticsService.class);

    private final RightVerifier rightVerifier = new RightVerifier();

    public CourseStatisticsDTO getCourseStatistics(Long courseId) throws AccessRestrictedToTeachersException, CourseNotFoundException, InvalidCourseAccessException {
        Teacher teacher = authenticationService.getCurrentTeacher();

        Optional<SupervisedCourse> optCourse = supervisedCourseRepository.findById(courseId);

        if (optCourse.isEmpty()) {
            logger.warn("INVALID ACCESS = attempt to access statistics of course {} that does not" +
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
}
