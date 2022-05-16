package com.polyglot.service.student_course_management;

import com.polyglot.model.*;
import com.polyglot.model.DTO.EnrolledCourseDTO;
import com.polyglot.model.DTO.SelfTaughtCourseDTO;
import com.polyglot.repository.CourseEnrollmentRepository;
import com.polyglot.repository.LanguageRepository;
import com.polyglot.repository.SelfTaughtCourseRepository;
import com.polyglot.repository.SelfTaughtLessonRepository;
import com.polyglot.service.authentication.AuthenticationService;
import com.polyglot.service.authentication.exceptions.AccessRestrictedToStudentsException;
import com.polyglot.service.file_storage.FileStorageService;
import com.polyglot.service.file_storage.exceptions.FileStorageException;
import com.polyglot.service.student_course_management.exceptions.CourseNotFoundException;
import com.polyglot.service.student_course_management.exceptions.LanguageNotFoundException;
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
public class StudentCourseManagementService {

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
    private FileStorageService fileStorageService;

    private static final Logger logger = LoggerFactory.getLogger(StudentCourseManagementService.class);


    /**
     * Creates and saves in the database a new self-taught course.
     * @param selfTaughtCourseDTO holds the data of the new course.
     * @return the saved course.
     * @throws AccessRestrictedToStudentsException if the current user is not a student.
     * @throws LanguageNotFoundException if the requested language is not supported.
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
     *
     * @param courseId
     * @param title
     * @param file
     * @return
     * @throws AccessRestrictedToStudentsException
     * @throws CourseNotFoundException
     * @throws FileStorageException
     */
    public SelfTaughtLesson saveNewSelfTaughtLesson(Long courseId, String title, MultipartFile file) throws AccessRestrictedToStudentsException, CourseNotFoundException, FileStorageException {
        Student student = authenticationService.getCurrentStudent();

        Optional<SelfTaughtCourse> course = selfTaughtCourseRepository.findById(courseId);

        if (course.isEmpty()) {
            logger.warn("INVALID UPDATE = attempt to add a lesson to a course {} that does not " +
                    "exist", courseId);
            throw new CourseNotFoundException();
        }

        SelfTaughtLesson selfTaughtLesson = new SelfTaughtLesson(title,
                course.get().getLessons().size()+1, course.get());

        SelfTaughtLesson savedLesson = selfTaughtLessonRepository.save(selfTaughtLesson);

        fileStorageService.storeLesson(file, savedLesson.getId());

        return savedLesson;
    }
}
