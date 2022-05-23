package com.polyglot.controller;

import com.polyglot.service.authentication.exceptions.AccessRestrictedToStudentsException;
import com.polyglot.service.lesson_practice.exceptions.LessonNotFoundException;
import com.polyglot.service.lesson_study.LessonStudyService;
import com.polyglot.service.lesson_study.exceptions.DuplicateWordToLearnException;
import com.polyglot.service.student_course_lesson_management.exceptions.InvalidCourseAccessException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

@RestController
@PreAuthorize("hasAuthority('STUDENT')")
public class LessonStudyController {

    @Autowired
    private LessonStudyService lessonStudyService;

    private static final Logger logger =
            LoggerFactory.getLogger(LessonStudyController.class);

    @PostMapping("/add_unknown_word")
    public void saveUnknownWord(@RequestBody UnknownWordDTO unknownWordDTO) throws AccessRestrictedToStudentsException, InvalidCourseAccessException, DuplicateWordToLearnException, LessonNotFoundException {
        logger.info("REQUEST - /add_unknown_word");
        lessonStudyService.saveUnknownWord(unknownWordDTO.getLessonId(), unknownWordDTO.getWord());
    }

    @GetMapping(value="/get_lesson_vocabulary_in_pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> getLessonVocabularyInPdf(@RequestParam Long lessonId) throws InvalidCourseAccessException, AccessRestrictedToStudentsException, LessonNotFoundException {
        logger.info("REQUEST - /get_lesson_vocabulary_in_pdf for lesson {}", lessonId);

        ByteArrayInputStream vocabularyPDF = lessonStudyService.getLessonsVocabularyInPdf(lessonId);

        logger.info("EVENT - generated PDF for lesson {}'s vocabulary", lessonId);

        var headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=vocabulary.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(vocabularyPDF));
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    private static class UnknownWordDTO {
        private String word;
        private Long lessonId;
    }
}
