package com.polyglot.service.student_course_lesson_management;

import com.polyglot.model.*;
import com.polyglot.model.DTO.EnrolledCourseDTO;
import com.polyglot.model.DTO.ExtendedEnrolledCourseDTO;
import com.polyglot.model.DTO.SelfTaughtCourseDTO;
import com.polyglot.repository.*;
import com.polyglot.service.authentication.AuthenticationService;
import com.polyglot.service.authentication.exceptions.AccessRestrictedToStudentsException;
import com.polyglot.service.lesson_storage.LessonStorageService;
import com.polyglot.service.lesson_storage.exceptions.FileStorageException;
import com.polyglot.service.right_restrictions.RightVerifier;
import com.polyglot.service.student_course_lesson_management.exceptions.CourseNotFoundException;
import com.polyglot.service.student_course_lesson_management.exceptions.InvalidCourseAccessException;
import com.polyglot.service.student_course_lesson_management.exceptions.LanguageNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class responsible for actions related to the management of self-taught courses.
 */
@Service
public class StudentCourseLessonManagementService {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private SelfTaughtCourseRepository selfTaughtCourseRepository;

    @Autowired
    private SelfTaughtLessonRepository selfTaughtLessonRepository;

    @Autowired
    private CourseEnrollmentRepository courseEnrollmentRepository;

    @Autowired
    private SupervisedCourseRepository supervisedCourseRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LessonStorageService lessonStorageService;

    private static final Logger logger = LoggerFactory.getLogger(StudentCourseLessonManagementService.class);

    private final RightVerifier rightVerifier = new RightVerifier();


    /**
     * Creates and saves in the database a new self-taught course.
     *
     * @param selfTaughtCourseDTO holds the data of the new course.
     * @return the saved course.
     * @throws AccessRestrictedToStudentsException if the current user is not a student.
     * @throws LanguageNotFoundException           if the requested language is not supported.
     */
    public SelfTaughtCourse createSelfTaughtCourse(SelfTaughtCourseDTO selfTaughtCourseDTO) throws AccessRestrictedToStudentsException, LanguageNotFoundException {
        Student student = authenticationService.getCurrentStudent();

        Optional<Language> optLanguage =
                languageRepository.findByName(selfTaughtCourseDTO.getLanguage());

        if (optLanguage.isEmpty()) {
            logger.warn(String.format("INVALID UPDATE - user %s attempted to create a course for " +
                            "not supported language %s",
                    student.getUserName(), selfTaughtCourseDTO.getLanguage()));
            throw new LanguageNotFoundException();
        }

        SelfTaughtCourse selfTaughtCourse = new SelfTaughtCourse(selfTaughtCourseDTO.getTitle(),
                selfTaughtCourseDTO.getMinPointsPerWord(),
                optLanguage.get(),
                student);
        selfTaughtCourse.addCourseEnrollment(student);

        SelfTaughtCourse savedCourse = selfTaughtCourseRepository.save(selfTaughtCourse);

        logger.info("UPDATE - created new course {}", savedCourse.toString());

        return savedCourse;
    }


    /**
     * Finds and returns all courses, self-taught or supervised, in which the current student is
     * enrolled.
     *
     * @return the courses in which the current student is enrolled.
     * @throws AccessRestrictedToStudentsException if the active user is not a student.
     */
    public List<EnrolledCourseDTO> getAllEnrolledCourses() throws AccessRestrictedToStudentsException {
        Student student = authenticationService.getCurrentStudent();

        return courseEnrollmentRepository.findByStudent(student)
                .stream()
                .map(CourseEnrollment::getCourse)
                .map(course -> {
                    if (course.getSupervisor().getUserName().equals(student.getUserName())) {
                        return new EnrolledCourseDTO(course.getId(), course.getTitle(),
                                course.getLanguage().getName(), null);
                    } else {
                        return new EnrolledCourseDTO(course.getId(), course.getTitle(),
                                course.getLanguage().getName(),
                                course.getSupervisor().getUserName());
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Saves a new self-taught lesson.
     *
     * @param courseId is the identifier of the course to which the lesson belongs.
     * @param title    is the title of the new lesson.
     * @param file     is the file with the lesson's content.
     * @return the newly saved lesson.
     * @throws AccessRestrictedToStudentsException if the active user is not a student.
     * @throws CourseNotFoundException             if no course with the requested id exists in the database.
     * @throws FileStorageException                if saving the file fails.
     * @throws InvalidCourseAccessException if the active user is not the owner of the course
     * with courseId.
     */
    public SelfTaughtLesson saveNewSelfTaughtLesson(Long courseId, String title, MultipartFile file) throws AccessRestrictedToStudentsException, CourseNotFoundException, FileStorageException, InvalidCourseAccessException {
        Student student = authenticationService.getCurrentStudent();

        Optional<SelfTaughtCourse> optCourse = selfTaughtCourseRepository.findById(courseId);

        if (optCourse.isEmpty()) {
            logger.warn("INVALID UPDATE = attempt to add a lesson to a course {} that does not " +
                    "exist", courseId);
            throw new CourseNotFoundException();
        }

        SelfTaughtCourse course = optCourse.get();
        if (!rightVerifier.hasRightToModifyTheDataOf(student, course)) {
            logger.warn("INVALID ACCESS = attempt add a lesson to course {} by student {}, who " +
                            "is not the creator of the course",
                    courseId, student);
            throw new InvalidCourseAccessException();
        }

        SelfTaughtLesson selfTaughtLesson = new SelfTaughtLesson(title,
                course.getLessons().size() + 1, course);

        SelfTaughtLesson savedLesson = selfTaughtLessonRepository.save(selfTaughtLesson);

        logger.info("UPDATE - saved new lesson {} with title {}", savedLesson.getId(),
                savedLesson.getTitle());

        lessonStorageService.storeLesson(file, savedLesson.getId());

        return savedLesson;
    }

    /**
     * Returns the data of a course in which the active user, a student, is enrolled.
     *
     * @param courseId is the id of the course whose data is requested and returned.
     * @return the data of the requested course.
     * @throws AccessRestrictedToStudentsException if the active user is not a student.
     * @throws InvalidCourseAccessException        if the student does not have access to the requested
     *                                             course.
     */
    public ExtendedEnrolledCourseDTO getEnrolledCourseData(Long courseId) throws AccessRestrictedToStudentsException, InvalidCourseAccessException, CourseNotFoundException {
        Student student = authenticationService.getCurrentStudent();

        Optional<Course> optCourse = courseRepository.findById(courseId);

        if (optCourse.isEmpty()) {
            logger.warn("INVALID REQUEST = attempt to access data of course {} that does not " +
                    "exist", courseId);
            throw new CourseNotFoundException();
        }

        Course course = optCourse.get();
        if (!rightVerifier.hasAccessToTheDataOf(student, course)) {
            logger.warn("INVALID ACCESS = attempt to access data of course {} by student {}, who " +
                            "is not enrolled",
                    courseId, student);
            throw new InvalidCourseAccessException();
        }

        String teacherName = null;
        if (!course.getSupervisor().getUserName().equals(student.getUserName())) {
            teacherName = course.getSupervisor().getUserName();
        }

        return new ExtendedEnrolledCourseDTO(course.getId(), course.getTitle(),
                course.getLanguage().getName(), teacherName,
                course.getLessons().stream().collect(Collectors.toMap(Lesson::getId,
                        Lesson::getTitle)));
    }

    /**
     * Enrolls the active student in the csupervised course with the given joining code.
     * @param joiningCode is the code of the course joined by the active student.
     * @throws CourseNotFoundException if no course with the given id exists.
     * @throws AccessRestrictedToStudentsException if the active user is not a student.
     */
    public void joinSupervisedCourse(Integer joiningCode) throws CourseNotFoundException,
            AccessRestrictedToStudentsException {
        Student student = authenticationService.getCurrentStudent();

        Optional<SupervisedCourse> optCourse = supervisedCourseRepository.findByJoiningCode(joiningCode);

        if (optCourse.isEmpty()) {
            logger.warn("INVALID UPDATE = attempt to join course with code {} that does not " +
                    "exist", joiningCode);
            throw new CourseNotFoundException();
        }

        SupervisedCourse course = optCourse.get();

        course.addCourseEnrollment(student);

        supervisedCourseRepository.save(course);

        logger.warn("UPDATE = student {} enrolled in course {}", student, course.getId());
    }
}
