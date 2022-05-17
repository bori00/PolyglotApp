package com.polyglot.controller;

import com.polyglot.service.file_storage.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@RestController
public class LessonManagementController {

    @Autowired
    private FileStorageService fileStorageService;

    private static final Logger logger = LoggerFactory.getLogger(LessonManagementController.class);

    @GetMapping(value="/get_lesson_file", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getLessonFile(@RequestParam Long lessonId) throws IOException {
        logger.info("REQUEST - /get_lesson_file for lesson {}", lessonId);

        // todo: verify that user has access to the given file

        byte[] file = fileStorageService.getLessonFile(lessonId);

        logger.info("EVENT - found pdf for lesson {}", lessonId);

        var headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=lesson.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }
}
