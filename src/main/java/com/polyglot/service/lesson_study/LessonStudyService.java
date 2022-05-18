package com.polyglot.service.lesson_study;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.testing.RemoteTranslateHelper;
import com.polyglot.model.CourseEnrollment;
import com.polyglot.model.Lesson;
import com.polyglot.model.Student;
import com.polyglot.model.WordToLearn;
import com.polyglot.repository.*;
import com.polyglot.service.authentication.AuthenticationService;
import com.polyglot.service.authentication.exceptions.AccessRestrictedToStudentsException;
import com.polyglot.service.student_course_management.StudentCourseManagementService;
import com.polyglot.service.student_course_management.exceptions.InvalidCourseAccessException;
import com.polyglot.translations.TranslatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

/**
 * Service responsible for any actions related to studying a lesson.
 */
@Service
public class LessonStudyService {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private TranslatorService translatorService;

    @Autowired
    private CourseEnrollmentRepository courseEnrollmentRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private WordToLearnRepository wordToLearnRepository;

    private static final Logger logger = LoggerFactory.getLogger(LessonStudyService.class);

    /**
     * Saves a new word to learn for the active user, to the given lesson, with the translation
     * to the users native language.
     * @param lessonId is the lesson to which the unknown word belongs.
     * @param word is the word to learn.
     * @throws AccessRestrictedToStudentsException if the active user is not a student.
     * @throws InvalidCourseAccessException if the active user is nt enrolled in the course to
     * which the lesson belongs.
     */
    public void saveUnknownWord(Long lessonId, String word) throws AccessRestrictedToStudentsException, InvalidCourseAccessException {
        Student student = authenticationService.getCurrentStudent();

        Lesson lesson = lessonRepository.getById(lessonId);

        Optional<CourseEnrollment> courseEnrollment =
                courseEnrollmentRepository.findByCourseAndStudent(lesson.getCourse(), student);

        if (courseEnrollment.isEmpty()) {
            throw new InvalidCourseAccessException();
        }

        String translatedWord = translatorService.getTranslation(word,
                lesson.getCourse().getLanguage(), student.getNativeLanguage());

        WordToLearn wordToLearn = new WordToLearn(word, translatedWord, 0, courseEnrollment.get()
                , lesson);

        wordToLearnRepository.save(wordToLearn);

        logger.info("UPDATE - saved new word to learn for user {}: {} in {} translated to {} in {}",
                student.getUserName(),
                word,
                lesson.getCourse().getLanguage(),
                translatedWord, student.getNativeLanguage());
    }
}
