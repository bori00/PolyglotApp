package com.polyglot.controller;

import com.polyglot.model.DTO.LessonDTO;
import com.polyglot.service.lesson_management.LessonManagementService;
import com.polyglot.service.lesson_practice.exceptions.LessonNotFoundException;
import com.polyglot.service.student_course_lesson_management.exceptions.InvalidCourseAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class LessonManagementController {

    @Autowired
    private LessonManagementService lessonManagementService;

    private static final Logger logger = LoggerFactory.getLogger(LessonManagementController.class);

    @GetMapping(value = "/get_lesson_file", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getLessonFile(@RequestParam Long lessonId) throws IOException, LessonNotFoundException, InvalidCourseAccessException {
        logger.info("REQUEST - /get_lesson_file for lesson {}", lessonId);

        byte[] file = lessonManagementService.getLessonsFile(lessonId);

        logger.info("EVENT - found pdf for lesson {}", lessonId);

        var headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=lesson.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }

    @GetMapping("/get_lesson_data")
    public LessonDTO saveUnknownWord(@RequestParam Long lessonId) throws LessonNotFoundException, InvalidCourseAccessException {
        logger.info("REQUEST - /get_lesson_data for lesson {}", lessonId);
        return lessonManagementService.getLessonData(lessonId);
    }
}
