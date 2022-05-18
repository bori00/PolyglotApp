package com.polyglot.controller;

import com.polyglot.model.DTO.WordLearningExerciseDTO;
import com.polyglot.model.DTO.WordLearningExerciseEvaluationDTO;
import com.polyglot.model.DTO.WordQuestionAnswerDTO;
import com.polyglot.service.authentication.exceptions.AccessRestrictedToStudentsException;
import com.polyglot.service.lesson_practice.LessonPracticeService;
import com.polyglot.service.lesson_practice.exceptions.LessonNotFoundException;
import com.polyglot.service.lesson_practice.exceptions.NoWordsToLearnException;
import com.polyglot.service.lesson_practice.exceptions.WordToLearnNotFoundException;
import com.polyglot.service.student_course_lesson_management.exceptions.InvalidCourseAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class LessonPracticeController {

    @Autowired
    private LessonPracticeService lessonPracticeService;

    private static final Logger logger = LoggerFactory.getLogger(LessonPracticeController.class);

    @GetMapping(value = "/get_word_question")
    @PreAuthorize("hasAuthority('STUDENT')")
    public WordLearningExerciseDTO getWordQuestion(@RequestParam Long lessonId) throws InvalidCourseAccessException, AccessRestrictedToStudentsException, LessonNotFoundException, NoWordsToLearnException {
        logger.info("REQUEST - /get_word_question for lesson {}", lessonId);

        return lessonPracticeService.getWordQuestion(lessonId);
    }

    @PostMapping(value = "/answer_word_question")
    @PreAuthorize("hasAuthority('STUDENT')")
    public WordLearningExerciseEvaluationDTO answerWordQuestion(@RequestBody WordQuestionAnswerDTO answerDTO) throws AccessRestrictedToStudentsException, WordToLearnNotFoundException, InvalidCourseAccessException {
        logger.info("REQUEST - /answer_word_question for wordToLearn {}", answerDTO.getWordToLearnId());

        return lessonPracticeService.answerWordQuestion(
                answerDTO.getWordToLearnId(),
                answerDTO.getSubmittedTranslation(),
                answerDTO.isForeignToNative());
    }
}
