package com.polyglot.controller;

import com.polyglot.service.authentication.exceptions.AccessRestrictedToStudentsException;
import com.polyglot.service.lesson_study.LessonStudyService;
import com.polyglot.service.student_course_lesson_management.exceptions.InvalidCourseAccessException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LessonStudyController {

    @Autowired
    private LessonStudyService lessonStudyService;

    private static final Logger logger =
            LoggerFactory.getLogger(LessonStudyController.class);

    @PostMapping("/add_unknown_word")
    @PreAuthorize("hasAuthority('STUDENT')")
    public void saveUnknownWord(@RequestBody UnknownWordDTO unknownWordDTO) throws AccessRestrictedToStudentsException, InvalidCourseAccessException {
        logger.info("REQUEST - /add_unknown_word");
        lessonStudyService.saveUnknownWord(unknownWordDTO.getLessonId(), unknownWordDTO.getWord());
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
