package com.polyglot.service.lesson_practice;

import com.polyglot.model.*;
import com.polyglot.model.DTO.WordLearningExerciseDTO;
import com.polyglot.model.DTO.WordLearningExerciseEvaluationDTO;
import com.polyglot.repository.CourseEnrollmentRepository;
import com.polyglot.repository.LessonRepository;
import com.polyglot.repository.WordToLearnRepository;
import com.polyglot.service.authentication.AuthenticationService;
import com.polyglot.service.authentication.exceptions.AccessRestrictedToStudentsException;
import com.polyglot.service.lesson_practice.exceptions.LessonNotFoundException;
import com.polyglot.service.lesson_practice.exceptions.NoWordsToLearnException;
import com.polyglot.service.lesson_practice.exceptions.WordToLearnNotFoundException;
import com.polyglot.service.student_course_management.exceptions.InvalidCourseAccessException;
import com.polyglot.translations.TranslatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Service responsible for creating and evaluating practice exercises for the lessons, based on
 * the unknown words of each user.
 */
@Service
public class LessonPracticeService {

    /**
     * The value of a good answer.
     */
    public static final int GOOD_ANSWER_POINTS = 1;
    /**
     * The penalty for a wrong answer.
     */
    public static final int BAD_ANSWER_POINTS = -2;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private TranslatorService translatorService;

    @Autowired
    private WordToLearnRepository wordToLearnRepository;

    @Autowired
    private CourseEnrollmentRepository courseEnrollmentRepository;

    @Autowired
    private LessonRepository lessonRepository;

    private static final Logger logger = LoggerFactory.getLogger(LessonPracticeService.class);

    /**
     * Generates a "word exercise", i.e. a question containing an unknown word belonging to the
     * requested lesson, or its translation, that asks the user to translate the word to their
     * native language or the studied foreign language, respectively. The unknown word
     * and the direction of the translation are randomly selected.
     *
     * @param lessonId is the lesson whose words are
     * @return the generated exercise.
     * @throws AccessRestrictedToStudentsException if the active user is not a student.
     * @throws LessonNotFoundException             if the requested lesson does not exist in the database.
     * @throws InvalidCourseAccessException        if the active user does not have access to the course
     *                                             of the requested lesson.
     * @throws NoWordsToLearnException             if there are no unknown (i.e. marked as unknown, with less
     *                                             than the target points) words for the requested lesson.
     */
    public WordLearningExerciseDTO getWordQuestion(Long lessonId) throws AccessRestrictedToStudentsException, LessonNotFoundException, InvalidCourseAccessException, NoWordsToLearnException {
        Student student = authenticationService.getCurrentStudent();

        Optional<Lesson> lesson = lessonRepository.findById(lessonId);
        if (lesson.isEmpty()) {
            logger.warn("INVALID REQUEST - no lesson with id {} found", lessonId);
            throw new LessonNotFoundException();
        }
        Course course = lesson.get().getCourse();
        Optional<CourseEnrollment> courseEnrollment =
                courseEnrollmentRepository.findByCourseAndStudent(course, student);
        if (courseEnrollment.isEmpty()) {
            logger.warn("INVALID REQUEST - user {} does not have access to the course of lesson {}",
                    student.getUserName(),
                    lessonId);
            throw new InvalidCourseAccessException();
        }

        // find the unknown words
        List<WordToLearn> wordsToLearn = wordToLearnRepository.findByLessonAndCourseEnrollmentAndCollectedPointsLessThan(
                lesson.get(),
                courseEnrollment.get(),
                course.getMinPointsPerWord());

        if (wordsToLearn.isEmpty()) {
            logger.info("EVENT - no question generated for lesson {}, because there are no " +
                    "unknown words", lessonId);
            throw new NoWordsToLearnException();
        }
        WordToLearn wordToLearn = wordsToLearn.get(new Random().nextInt(wordsToLearn.size()));

        if (randomTranslateForwards()) {
            // translate unknown word to native language
            logger.info("EVENT - question generated for lesson {}, from foreign word {} to native" +
                    " language", lessonId, wordToLearn.getOriginalWord());
            return new WordLearningExerciseDTO(
                    wordToLearn.getId(),
                    lessonId,
                    wordToLearn.getOriginalWord(),
                    wordToLearn.getCollectedPoints(),
                    course.getMinPointsPerWord(),
                    wordToLearn.getLesson().getCourse().getLanguage().getName(),
                    student.getNativeLanguage().getName(),
                    true);
        } else {
            // translate from native language to foreign language
            logger.info("EVENT - question generated for lesson {}, from native language word {} " +
                    "to foreign language", lessonId, wordToLearn.getTranslation());
            return new WordLearningExerciseDTO(
                    wordToLearn.getId(),
                    lessonId,
                    wordToLearn.getTranslation(),
                    wordToLearn.getCollectedPoints(),
                    course.getMinPointsPerWord(),
                    student.getNativeLanguage().getName(),
                    wordToLearn.getLesson().getCourse().getLanguage().getName(),
                    false);
        }
    }

    /**
     * Evaluates the answer to a word question.
     *
     * @param wordToLearnId        is the id of the WordToLearn to which the question belonged.
     * @param submittedTranslation is the answer submitted by the user.
     * @param foreignToNative      shows the direction of the translation requested from the user in
     *                             the question.
     * @return the evaluation of the user's answer.
     * @throws AccessRestrictedToStudentsException is the active user is not a student.
     * @throws WordToLearnNotFoundException        if the WordToLearn was not found in the database.
     */
    public WordLearningExerciseEvaluationDTO answerWordQuestion(Long wordToLearnId,
                                                                String submittedTranslation,
                                                                boolean foreignToNative) throws AccessRestrictedToStudentsException, WordToLearnNotFoundException {
        Student student = authenticationService.getCurrentStudent();

        Optional<WordToLearn> optWordToLearn = wordToLearnRepository.findById(wordToLearnId);

        if (optWordToLearn.isEmpty()) {
            logger.warn("INVALID REQUEST - WordToLearn with {} not found", wordToLearnId);
            throw new WordToLearnNotFoundException();
        }

        WordToLearn wordToLearn = optWordToLearn.get();

        Language sourceLanguage = foreignToNative ?
                wordToLearn.getLesson().getCourse().getLanguage() :
                student.getNativeLanguage();
        Language targetLanguage = foreignToNative ?
                student.getNativeLanguage() :
                wordToLearn.getLesson().getCourse().getLanguage();

        String word = foreignToNative ? wordToLearn.getOriginalWord() :
                wordToLearn.getTranslation();
        String officialTranslation = foreignToNative ? wordToLearn.getTranslation() :
                wordToLearn.getOriginalWord();

        boolean isCorrect = translatorService.isCorrectTranslation(word, submittedTranslation,
                sourceLanguage, targetLanguage);

        if (isCorrect) {
            wordToLearn.setCollectedPoints(wordToLearn.getCollectedPoints() + GOOD_ANSWER_POINTS);
        } else {
            wordToLearn.setCollectedPoints(wordToLearn.getCollectedPoints() + BAD_ANSWER_POINTS);
        }

        wordToLearnRepository.save(wordToLearn);

        logger.info("UPDATE - set collected points of WordToLearn nr {} to {}, after evaluating " +
                        "the user's answer", wordToLearnId,
                wordToLearn.getCollectedPoints());

        return new WordLearningExerciseEvaluationDTO(
                wordToLearnId,
                wordToLearn.getLesson().getId(),
                word,
                wordToLearn.getCollectedPoints(),
                wordToLearn.getLesson().getCourse().getMinPointsPerWord(),
                officialTranslation,
                submittedTranslation,
                isCorrect,
                sourceLanguage.getName(),
                targetLanguage.getName(),
                foreignToNative);
    }

    private boolean randomTranslateForwards() {
        return new Random().nextBoolean();
    }
}
