package com.polyglot.controller;

import com.polyglot.model.DTO.CourseStatisticsDTO;
import com.polyglot.model.DTO.LessonStatisticsDTO;
import com.polyglot.model.DTO.TaughtCourseDTO;
import com.polyglot.service.authentication.exceptions.AccessRestrictedToTeachersException;
import com.polyglot.service.course_lesson_statistics.TeacherStatisticsService;
import com.polyglot.service.lesson_practice.exceptions.LessonNotFoundException;
import com.polyglot.service.student_course_lesson_management.exceptions.CourseNotFoundException;
import com.polyglot.service.student_course_lesson_management.exceptions.InvalidCourseAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("hasAuthority('TEACHER')")
public class TeacherCourseStatisticsController {

    @Autowired
    private TeacherStatisticsService teacherStatisticsService;

    private static final Logger logger =
            LoggerFactory.getLogger(StudentCourseManagementController.class);

    @GetMapping("/get_course_statistics")
    public CourseStatisticsDTO getCourseStatistics(@RequestParam Long courseId) throws CourseNotFoundException, InvalidCourseAccessException, AccessRestrictedToTeachersException {
        logger.info("REQUEST - /get_course_statistics for course {}", courseId);
        return teacherStatisticsService.getCourseStatistics(courseId);
    }

    @GetMapping("/get_lesson_statistics")
    public LessonStatisticsDTO getLessonStatistics(@RequestParam Long lessonId) throws CourseNotFoundException,
            InvalidCourseAccessException, AccessRestrictedToTeachersException, LessonNotFoundException {
        logger.info("REQUEST - /get_course_statistics for lesson {}", lessonId);
        return teacherStatisticsService.getLessonStatistics(lessonId);
    }
}
