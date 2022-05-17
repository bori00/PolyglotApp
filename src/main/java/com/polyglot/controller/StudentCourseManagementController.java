package com.polyglot.controller;

import com.polyglot.model.DTO.EnrolledCourseDTO;
import com.polyglot.model.DTO.ExtendedEnrolledCourseDTO;
import com.polyglot.model.DTO.SelfTaughtCourseDTO;
import com.polyglot.model.SelfTaughtCourse;
import com.polyglot.service.authentication.exceptions.AccessRestrictedToStudentsException;
import com.polyglot.service.file_storage.FileStorageService;
import com.polyglot.service.file_storage.exceptions.FileStorageException;
import com.polyglot.service.student_course_management.StudentCourseManagementService;
import com.polyglot.service.student_course_management.exceptions.CourseNotFoundException;
import com.polyglot.service.student_course_management.exceptions.InvalidCourseAccessException;
import com.polyglot.service.student_course_management.exceptions.LanguageNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(value = "/add_new_self_taught_lesson", consumes =
            MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('STUDENT')")
    public void addNewSelfTaughtLesson(@RequestParam("file") MultipartFile file, @RequestParam(
            "title") String title, @RequestParam Long courseId) throws FileStorageException, AccessRestrictedToStudentsException, CourseNotFoundException {
        logger.info("REQUEST - /add_new_self_taught_lesson for course {} and title {}", courseId,
                title);

        studentCourseManagementService.saveNewSelfTaughtLesson(courseId, title, file);
    }

    @GetMapping("/get_all_enrolled_courses")
    @PreAuthorize("hasAuthority('STUDENT')")
    public List<EnrolledCourseDTO> getAllEnrolledCourses() throws AccessRestrictedToStudentsException {
        logger.info("REQUEST - /get_all_enrolled_courses");
        return studentCourseManagementService.getAllEnrolledCourses();
    }

    @GetMapping("/get_enrolled_course_data")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ExtendedEnrolledCourseDTO getEnrolledCourseData(Long courseId) throws AccessRestrictedToStudentsException, InvalidCourseAccessException {
        logger.info("REQUEST - /get_enrolled_course_data");
        return studentCourseManagementService.getEnrolledCourseData(courseId);
    }

    @PostMapping("/add_unknown_word")
    @PreAuthorize("hasAuthority('STUDENT')")
    public void saveUnknownWord(@RequestBody UnknownWordDTO unknownWordDTO) throws AccessRestrictedToStudentsException, InvalidCourseAccessException {
        logger.info("REQUEST - /add_unknown_word");
        studentCourseManagementService.saveUnknownWord(unknownWordDTO.getLessonId(), unknownWordDTO.getWord());
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
