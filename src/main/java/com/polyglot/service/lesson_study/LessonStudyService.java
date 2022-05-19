package com.polyglot.service.lesson_study;

import com.polyglot.model.CourseEnrollment;
import com.polyglot.model.Lesson;
import com.polyglot.model.Student;
import com.polyglot.model.WordToLearn;
import com.polyglot.repository.CourseEnrollmentRepository;
import com.polyglot.repository.LessonRepository;
import com.polyglot.repository.WordToLearnRepository;
import com.polyglot.service.authentication.AuthenticationService;
import com.polyglot.service.authentication.exceptions.AccessRestrictedToStudentsException;
import com.polyglot.service.right_restrictions.RightVerifier;
import com.polyglot.service.student_course_lesson_management.exceptions.CourseNotFoundException;
import com.polyglot.service.student_course_lesson_management.exceptions.InvalidCourseAccessException;
import com.polyglot.translations.TranslatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    private final RightVerifier rightVerifier = new RightVerifier();

    /**
     * Saves a new word to learn for the active user, to the given lesson, with the translation
     * to the users native language.
     *
     * @param lessonId is the lesson to which the unknown word belongs.
     * @param word     is the word to learn.
     * @throws AccessRestrictedToStudentsException if the active user is not a student.
     * @throws InvalidCourseAccessException        if the active user is nt enrolled in the course to
     *                                             which the lesson belongs.
     */
    public void saveUnknownWord(Long lessonId, String word) throws AccessRestrictedToStudentsException, InvalidCourseAccessException {
        Student student = authenticationService.getCurrentStudent();

        Lesson lesson = lessonRepository.getById(lessonId);

        if (!rightVerifier.hasAccessToTheDataOf(student, lesson)) {
            logger.warn("INVALID UPDATE = attempt to add an unknown word to a lesson {} that the " +
                    "user {} doesn't have access to", lesson, student);
            throw new InvalidCourseAccessException();
        }

        Optional<CourseEnrollment> courseEnrollment =
                courseEnrollmentRepository.findByCourseAndStudent(lesson.getCourse(), student);
        // guaranteed to be present due to the previous check

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
