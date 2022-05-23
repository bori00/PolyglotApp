package com.polyglot.service.teacher_course_lesson_management;

import com.polyglot.model.*;
import com.polyglot.model.DTO.*;
import com.polyglot.repository.*;
import com.polyglot.service.authentication.AuthenticationService;
import com.polyglot.service.authentication.exceptions.AccessRestrictedToStudentsException;
import com.polyglot.service.authentication.exceptions.AccessRestrictedToTeachersException;
import com.polyglot.service.lesson_storage.LessonStorageService;
import com.polyglot.service.lesson_storage.exceptions.FileStorageException;
import com.polyglot.service.right_restrictions.RightVerifier;
import com.polyglot.service.student_course_lesson_management.StudentCourseLessonManagementService;
import com.polyglot.service.student_course_lesson_management.exceptions.CourseNotFoundException;
import com.polyglot.service.student_course_lesson_management.exceptions.InvalidCourseAccessException;
import com.polyglot.service.student_course_lesson_management.exceptions.LanguageNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class responsible for actions related to the management of supervised courses.
 */
@Service
public class TeacherCourseLessonManagementService {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private SupervisedCourseRepository supervisedCourseRepository;

    @Autowired
    private SupervisedLessonRepository supervisedLessonRepository;

    @Autowired
    private LessonStorageService lessonStorageService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(TeacherCourseLessonManagementService.class);

    private final RightVerifier rightVerifier = new RightVerifier();


    /**
     * Creates and saves in the database a new supervised course.
     *
     * @param supervisedCourseDTO holds the data of the new course.
     * @return the saved course.
     * @throws AccessRestrictedToTeachersException if the current user is not a teacher.
     * @throws LanguageNotFoundException           if the requested language is not supported.
     */
    public ExtendedTaughtCourseDTO createSupervisedCourse(SupervisedCourseDTO supervisedCourseDTO) throws LanguageNotFoundException, AccessRestrictedToTeachersException {
        Teacher teacher = authenticationService.getCurrentTeacher();

        Optional<Language> optLanguage =
                languageRepository.findByName(supervisedCourseDTO.getLanguage());

        if (optLanguage.isEmpty()) {
            logger.warn(String.format("INVALID UPDATE - user %s attempted to create a course for " +
                            "not supported language %s",
                    teacher.getUserName(), supervisedCourseDTO.getLanguage()));
            throw new LanguageNotFoundException();
        }

        SupervisedCourse supervisedCourse = new SupervisedCourse(supervisedCourseDTO.getTitle(),
                supervisedCourseDTO.getMinPointsPerWord(),
                optLanguage.get(),
                teacher,
                -1);

        SupervisedCourse savedCourse = supervisedCourseRepository.save(supervisedCourse);

        savedCourse.setJoiningCode(Math.toIntExact(savedCourse.getId()));

        savedCourse = supervisedCourseRepository.save(supervisedCourse);

        logger.info("UPDATE - created new course {}", savedCourse.toString());

        return new ExtendedTaughtCourseDTO(
                savedCourse.getId(),
                savedCourse.getTitle(),
                savedCourse.getLanguage().getName(),
                savedCourse.getEnrollments().size(),
                savedCourse.getLessons().stream().collect(Collectors.toMap(Lesson::getId,
                        Lesson::getTitle)),
                savedCourse.getJoiningCode()
        );
    }


    /**
     * Finds and returns all course taught by the active teacher.
     *
     * @return the courses taught by the active teacher.
     * @throws AccessRestrictedToTeachersException if the active user is not a teacher.
     */
    public List<TaughtCourseDTO> getAllTaughtCourses() throws  AccessRestrictedToTeachersException {
        Teacher teacher = authenticationService.getCurrentTeacher();

        return teacher.getTaughtCourses()
                    .stream()
                    .map(course -> new TaughtCourseDTO(course.getId(), course.getTitle(),
                            course.getLanguage().getName()))
                    .collect(Collectors.toList());
    }

    /**
     * Saves a new supervised lesson.
     *
     * @param courseId is the identifier of the course to which the lesson belongs.
     * @param title    is the title of the new lesson.
     * @param file     is the file with the lesson's content.
     * @return the newly saved lesson.
     * @throws AccessRestrictedToTeachersException if the active user is not a teacher.
     * @throws CourseNotFoundException             if no course with the requested id exists in the database.
     * @throws FileStorageException                if saving the file fails.
     * @throws InvalidCourseAccessException if the active user is not the owner of the course
     * with courseId.
     */
    public SupervisedLesson saveNewSupervisedLesson(Long courseId, String title,
                                                  MultipartFile file) throws CourseNotFoundException, FileStorageException, InvalidCourseAccessException, AccessRestrictedToTeachersException {
        Teacher teacher = authenticationService.getCurrentTeacher();

        Optional<SupervisedCourse> optCourse = supervisedCourseRepository.findById(courseId);

        if (optCourse.isEmpty()) {
            logger.warn("INVALID UPDATE = attempt to add a lesson to a course {} that does not " +
                    "exist", courseId);
            throw new CourseNotFoundException();
        }

        SupervisedCourse course = optCourse.get();
        if (!rightVerifier.hasRightToModifyTheDataOf(teacher, course)) {
            logger.warn("INVALID ACCESS = attempt add a lesson to course {} by teacher {}, who " +
                            "is not the creator of the course",
                    courseId, teacher);
            throw new InvalidCourseAccessException();
        }

        SupervisedLesson supervisedLesson = new SupervisedLesson(title,
                course.getLessons().size() + 1, course);

        SupervisedLesson savedLesson = supervisedLessonRepository.save(supervisedLesson);

        logger.info("UPDATE - saved new lesson {} with title {}", savedLesson.getId(),
                savedLesson.getTitle());

        lessonStorageService.storeLesson(file, savedLesson.getId());

        return savedLesson;
    }

    /**
     * Returns the data of a course supervised by the active user.
     *
     * @param courseId is the id of the course whose data is requested and returned.
     * @return the data of the requested course.
     * @throws AccessRestrictedToTeachersException if the active user is not a teacher.
     * @throws InvalidCourseAccessException        if the teacher does not have access to the
     * requested course.
     */
    public ExtendedTaughtCourseDTO getTaughtCourseData(Long courseId) throws InvalidCourseAccessException, CourseNotFoundException, AccessRestrictedToTeachersException {
        Teacher teacher = authenticationService.getCurrentTeacher();

        Optional<SupervisedCourse> optCourse = supervisedCourseRepository.findById(courseId);

        if (optCourse.isEmpty()) {
            logger.warn("INVALID UPDATE = attempt to add a lesson to a course {} that does not " +
                    "exist", courseId);
            throw new CourseNotFoundException();
        }

        SupervisedCourse course = optCourse.get();
        if (!rightVerifier.hasAccessToTheDataOf(teacher, course)) {
            logger.warn("INVALID ACCESS = attempt to access data of course {} by teacher {}, who " +
                            "is not the supervisor of the course",
                    courseId, teacher);
            throw new InvalidCourseAccessException();
        }

        return new ExtendedTaughtCourseDTO(
                course.getId(),
                course.getTitle(),
                course.getLanguage().getName(),
                course.getEnrollments().size(),
                course.getLessons().stream().collect(Collectors.toMap(Lesson::getId,
                        Lesson::getTitle)),
                course.getJoiningCode());
    }
}
