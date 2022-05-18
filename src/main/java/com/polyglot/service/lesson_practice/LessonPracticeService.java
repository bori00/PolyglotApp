package com.polyglot.service.lesson_practice;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.testing.RemoteTranslateHelper;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service responsible for creating and evaluating practice exercises for the lessons, based on
 * the unknown words of each user.
 */
@Service
public class LessonPracticeService {

    public static final int GOOD_ANSWER_POINTS = 1;
    public static final int BAD_ANSWER_POINTS = -2;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private WordToLearnRepository wordToLearnRepository;

    @Autowired
    private CourseEnrollmentRepository courseEnrollmentRepository;

    @Autowired
    private LessonRepository lessonRepository;

    private static final Logger logger = LoggerFactory.getLogger(LessonPracticeService.class);

    public WordLearningExerciseDTO getWordQuestion(Long lessonId) throws AccessRestrictedToStudentsException, LessonNotFoundException, InvalidCourseAccessException, NoWordsToLearnException {
        Student student = authenticationService.getCurrentStudent();

        Optional<Lesson> lesson = lessonRepository.findById(lessonId);
        if (lesson.isEmpty()) {
            throw new LessonNotFoundException();
        }

        Course course = lesson.get().getCourse();
        Optional<CourseEnrollment> courseEnrollment =
                courseEnrollmentRepository.findByCourseAndStudent(course, student);
        if (courseEnrollment.isEmpty()) {
            throw new InvalidCourseAccessException();
        }

        List<WordToLearn> wordsToLearn = wordToLearnRepository.findByLessonAndCourseEnrollment(
                lesson.get(),
                courseEnrollment.get());

        if (wordsToLearn.isEmpty()) {
            throw new NoWordsToLearnException();
        }
        WordToLearn wordToLearn = wordsToLearn.get(new Random().nextInt(wordsToLearn.size()));

        if (randomTranslateForwards()) {
            // translate unknown word to native language
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

    public WordLearningExerciseEvaluationDTO answerWordQuestion(Long wordToLearnId,
                                                                String submittedTranslation,
                                                                boolean foreignToNative) throws AccessRestrictedToStudentsException, WordToLearnNotFoundException {
        Student student = authenticationService.getCurrentStudent();

        Optional<WordToLearn> optWordToLearn = wordToLearnRepository.findById(wordToLearnId);

        if (optWordToLearn.isEmpty()) {
            throw new WordToLearnNotFoundException();
        }

        WordToLearn wordToLearn = optWordToLearn.get();

        boolean isCorrect = isCorrectAnswer(wordToLearn, submittedTranslation,
                student.getNativeLanguage(), foreignToNative);

        if (isCorrect) {
            wordToLearn.setCollectedPoints(wordToLearn.getCollectedPoints() + GOOD_ANSWER_POINTS);
        } else {
            wordToLearn.setCollectedPoints(wordToLearn.getCollectedPoints() + BAD_ANSWER_POINTS);
        }

        wordToLearnRepository.save(wordToLearn);

        Language sourceLanguage = foreignToNative ?
                wordToLearn.getLesson().getCourse().getLanguage() :
                student.getNativeLanguage();
        Language targetLanguage = !foreignToNative ?
                wordToLearn.getLesson().getCourse().getLanguage() :
                student.getNativeLanguage();

        String word = foreignToNative ? wordToLearn.getOriginalWord() :
                wordToLearn.getTranslation();
        String officialTranslation = foreignToNative ? wordToLearn.getTranslation() :
                wordToLearn.getOriginalWord();

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

    private boolean isCorrectAnswer(WordToLearn wordToLearn, String submittedTranslation,
                                    Language nativeLanguage, boolean foreignToNative) {
        String sourceWord;
        String translation;

        Language sourceLanguage;
        Language targetLanguage;

        if (foreignToNative) {
           sourceWord = wordToLearn.getOriginalWord();
           translation = wordToLearn.getTranslation();
           sourceLanguage = wordToLearn.getLesson().getCourse().getLanguage();
           targetLanguage = nativeLanguage;
        } else {
            sourceWord = wordToLearn.getTranslation();
            translation = wordToLearn.getOriginalWord();
            sourceLanguage = nativeLanguage;
            targetLanguage = wordToLearn.getLesson().getCourse().getLanguage();
        }

        if (submittedTranslation.equals(translation)) {
            return true;
        }

        String backTranslatedWord = submittedTranslation;
        if (!wordToLearn.getLesson().getCourse().getLanguage().equals(nativeLanguage)) {
            RemoteTranslateHelper helper = RemoteTranslateHelper.create();
            Translate translate = helper.getOptions().getService();

            backTranslatedWord = translate.translate(submittedTranslation,
                    Translate.TranslateOption.sourceLanguage(targetLanguage.getAPI_ID()),
                    Translate.TranslateOption.targetLanguage(sourceLanguage.getAPI_ID())).getTranslatedText().toLowerCase(Locale.ROOT);
        }

        return (backTranslatedWord.equals(sourceWord.toLowerCase(Locale.ROOT)));
    }

    private boolean randomTranslateForwards() {
        return new Random().nextBoolean();
    }
}
