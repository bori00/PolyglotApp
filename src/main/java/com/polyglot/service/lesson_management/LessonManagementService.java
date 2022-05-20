package com.polyglot.service.lesson_management;

import com.polyglot.model.DTO.LessonDTO;
import com.polyglot.model.Lesson;
import com.polyglot.model.User;
import com.polyglot.repository.LessonRepository;
import com.polyglot.service.authentication.AuthenticationService;
import com.polyglot.service.authentication.exceptions.AccessRestrictedToStudentsException;
import com.polyglot.service.authentication.exceptions.AccessRestrictedToTeachersException;
import com.polyglot.service.lesson_storage.LessonStorageService;
import com.polyglot.service.lesson_practice.exceptions.LessonNotFoundException;
import com.polyglot.service.right_restrictions.RightVerifier;
import com.polyglot.service.student_course_lesson_management.StudentCourseLessonManagementService;
import com.polyglot.service.student_course_lesson_management.exceptions.InvalidCourseAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

/**
 * Service reponsible for accesisng and updating lesson data.
 */
@Service
public class LessonManagementService {

    @Autowired
    private LessonStorageService lessonStorageService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private LessonRepository lessonRepository;

    private static final Logger logger = LoggerFactory.getLogger(StudentCourseLessonManagementService.class);

    private final RightVerifier rightVerifier = new RightVerifier();

    /**
     * Returns the content (the pdf file) of a lesson.
     * @param lessonId is the identifier of the lesson whose content is returned.
     * @return the file, in a byte array linked to the requested lesson.
     * @throws LessonNotFoundException if no lesson with the given id exists.
     * @throws InvalidCourseAccessException if the active user does not have access to the course
     * which contains the given lesson.
     * @throws IOException if the file could not be read.
     */
    public byte[] getLessonsFile(Long lessonId) throws LessonNotFoundException,
            InvalidCourseAccessException, IOException {
        User user;

        try {
            user = authenticationService.getCurrentStudent();
        } catch (AccessRestrictedToStudentsException e) {
            try {
                user = authenticationService.getCurrentTeacher();
            } catch (AccessRestrictedToTeachersException accessRestrictedToTeachersException) {
                throw new IllegalStateException("Unknown user type");
            }
        }

        Optional<Lesson> lesson = lessonRepository.findById(lessonId);
        if (lesson.isEmpty()) {
            logger.warn("INVALID REQUEST - file of lesson {] could not be retrieved, because " +
                    "lesson does not exist.");
            throw new LessonNotFoundException();
        }

        if (!rightVerifier.hasAccessToTheDataOf(user, lesson.get())) {
            logger.warn("INVALID ACCESS - user {} attempted to access the file content of lesson " +
                    "{}, but does not have access to it", user, lessonId);
            throw new InvalidCourseAccessException();
        }

        return lessonStorageService.getLessonFile(lessonId);
    }

    /**
     * Returns the general data of a lesson.
     * @param lessonId is the identifier of the lesson whose data is requested.
     * @return the lesson's data.
     * @throws InvalidCourseAccessException if the active user does not have access to the data
     * of the course which this lesson is part of.
     * @throws LessonNotFoundException if no lesson with the requested id exists.
     */
    public LessonDTO getLessonData(Long lessonId) throws InvalidCourseAccessException,
            LessonNotFoundException {
        User user;

        try {
            user = authenticationService.getCurrentStudent();
        } catch (AccessRestrictedToStudentsException e) {
            try {
                user = authenticationService.getCurrentTeacher();
            } catch (AccessRestrictedToTeachersException accessRestrictedToTeachersException) {
                throw new IllegalStateException("Unknown user type");
            }
        }

        Optional<Lesson> lesson = lessonRepository.findById(lessonId);
        if (lesson.isEmpty()) {
            logger.warn("INVALID REQUEST - file of lesson {] could not be retrieved, because " +
                    "lesson does not exist.");
            throw new LessonNotFoundException();
        }

        if (!rightVerifier.hasAccessToTheDataOf(user, lesson.get())) {
            logger.warn("INVALID ACCESS - user {} attempted to access the file content of lesson " +
                    "{}, but does not have access to it", user, lessonId);
            throw new InvalidCourseAccessException();
        }

        return new LessonDTO(
                lesson.get().getTitle(),
                lesson.get().getCourse().getId(),
                lesson.get().getIndexInsideCourse(),
                lesson.get().getCourse().getTitle());
    }
}
