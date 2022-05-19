package com.polyglot.controller;

import com.polyglot.model.DTO.*;
import com.polyglot.service.authentication.exceptions.AccessRestrictedToStudentsException;
import com.polyglot.service.authentication.exceptions.AccessRestrictedToTeachersException;
import com.polyglot.service.lesson_storage.exceptions.FileStorageException;
import com.polyglot.service.student_course_lesson_management.exceptions.CourseNotFoundException;
import com.polyglot.service.student_course_lesson_management.exceptions.InvalidCourseAccessException;
import com.polyglot.service.student_course_lesson_management.exceptions.LanguageNotFoundException;
import com.polyglot.service.teacher_course_lesson_management.TeacherCourseLessonManagementService;
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
@PreAuthorize("hasAuthority('TEACHER')")
public class TeacherCourseManagementController {

    @Autowired
    private TeacherCourseLessonManagementService teacherCourseLessonManagementService;

    private static final Logger logger =
            LoggerFactory.getLogger(StudentCourseManagementController.class);

    @PostMapping("/create_supervised_course")
    public ExtendedTaughtCourseDTO createSelfTaughtCourse(@Valid @RequestBody SupervisedCourseDTO supervisedCourseDTO) throws LanguageNotFoundException, AccessRestrictedToTeachersException {
        logger.info("REQUEST - /create_supervised_course with DTO {}", supervisedCourseDTO);
        return teacherCourseLessonManagementService.createSupervisedCourse(supervisedCourseDTO);
    }

    @PostMapping(value = "/add_new_supervised_lesson", consumes =
            MediaType.MULTIPART_FORM_DATA_VALUE)
    public void addNewSelfTaughtLesson(@RequestParam("file") MultipartFile file, @RequestParam(
            "title") String title, @RequestParam Long courseId) throws FileStorageException, CourseNotFoundException, InvalidCourseAccessException, AccessRestrictedToTeachersException {
        logger.info("REQUEST - /add_new_supervised_lesson for course {} and title {}", courseId,
                title);

        teacherCourseLessonManagementService.saveNewSupervisedLesson(courseId, title, file);
    }

    @GetMapping("/get_all_taught_courses")
    public List<TaughtCourseDTO> getAllTaughtCourses() throws AccessRestrictedToTeachersException {
        logger.info("REQUEST - /get_all_taught_courses");
        return teacherCourseLessonManagementService.getAllTaughtCourses();
    }

    @GetMapping("/get_taught_course_data")
    public ExtendedTaughtCourseDTO getTaughtCourseData(Long courseId) throws InvalidCourseAccessException, CourseNotFoundException, AccessRestrictedToTeachersException {
        logger.info("REQUEST - /get_taught_course_data for course {}", courseId);
        return teacherCourseLessonManagementService.getTaughtCourseData(courseId);
    }
}
