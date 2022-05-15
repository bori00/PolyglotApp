package com.polyglot.controller;

import com.polyglot.model.DTO.EnrolledCourseDTO;
import com.polyglot.model.DTO.SelfTaughtCourseDTO;
import com.polyglot.model.SelfTaughtCourse;
import com.polyglot.service.authentication.exceptions.AccessRestrictedToStudentsException;
import com.polyglot.service.student_course_management.StudentCourseManagementService;
import com.polyglot.service.student_course_management.exceptions.LanguageNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class StudentCourseManagementController {

    @Autowired
    private StudentCourseManagementService studentCourseManagementService;

    private static final Logger logger =
            LoggerFactory.getLogger(StudentCourseManagementController.class);

    @PostMapping("/create_self_taught_course")
    @PreAuthorize("hasAuthority('STUDENT')")
    public void createSelfTaughtCourse(@Valid @RequestBody SelfTaughtCourseDTO selfTaughtCourseDTO) throws AccessRestrictedToStudentsException, LanguageNotFoundException {
        logger.info("REQUEST - /create_self_taught_course with DTO {}", selfTaughtCourseDTO);
        studentCourseManagementService.createSelfTaughtCourse(selfTaughtCourseDTO);
    }

    @GetMapping("/get_all_enrolled_courses")
    @PreAuthorize("hasAuthority('STUDENT')")
    public List<EnrolledCourseDTO> getAllEnrolledCourses() throws AccessRestrictedToStudentsException {
        logger.info("REQUEST - /get_all_enrolled_courses");
        return studentCourseManagementService.getAllEnrolledCourses();
    }

//    @GetMapping("/get_lessons_of_enrolled_course")
//    @PreAuthorize("hasAuthority('STUDENT')")
//    public List<>

}
