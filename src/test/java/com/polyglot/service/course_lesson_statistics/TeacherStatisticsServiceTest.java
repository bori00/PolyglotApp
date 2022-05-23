package com.polyglot.service.course_lesson_statistics;

import com.polyglot.PolyglotApplication;
import com.polyglot.model.*;
import com.polyglot.model.DTO.CourseStatisticsDTO;
import com.polyglot.model.DTO.LessonStatisticsDTO;
import com.polyglot.repository.SupervisedCourseRepository;
import com.polyglot.repository.SupervisedLessonRepository;
import com.polyglot.repository.WordToLearnRepository;
import com.polyglot.service.authentication.AuthenticationService;
import com.polyglot.service.authentication.exceptions.AccessRestrictedToTeachersException;
import com.polyglot.service.lesson_practice.exceptions.LessonNotFoundException;
import com.polyglot.service.student_course_lesson_management.exceptions.CourseNotFoundException;
import com.polyglot.service.student_course_lesson_management.exceptions.InvalidCourseAccessException;
import org.apache.commons.codec.language.bm.Lang;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = PolyglotApplication.class)
class TeacherStatisticsServiceTest {

    @Autowired
    private TeacherStatisticsService teacherStatisticsService;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private SupervisedCourseRepository supervisedCourseRepository;

    @MockBean
    private WordToLearnRepository wordToLearnRepository;

    @MockBean
    private SupervisedLessonRepository supervisedLessonRepository;

    private static final Language LANGUAGE = new Language(1L, "l", "LANG");
    private static final Teacher TEACHER = new Teacher("teacher", "pass", "teacher@gmail.com",
            LANGUAGE);

    private static SupervisedCourse SUPERVISED_COURSE = new SupervisedCourse("c1", 5,
            LANGUAGE, TEACHER,
            1);
    private static SupervisedCourse UNSUPERVISED_COURSE = new SupervisedCourse("c1", 5,
            LANGUAGE, new Teacher("t2", "pass", "@", LANGUAGE),
            1);

    private static final Student STUDENT1 = new Student("st1", "pass", "student1@gmail.com",
            LANGUAGE);
    private static final Student STUDENT2 = new Student("st2", "pass", "student2@gmail.com",
            LANGUAGE);
    private static final Student STUDENT3= new Student("st3", "pass", "student3@gmail.com",
            LANGUAGE);

    private static final SupervisedLesson LESSON1 = new SupervisedLesson("L1", 1,
            SUPERVISED_COURSE);
    private static final SupervisedLesson LESSON2 = new SupervisedLesson("L2", 2,
            SUPERVISED_COURSE);
    private static final SupervisedLesson LESSON3 = new SupervisedLesson("L3", 3,
            SUPERVISED_COURSE);
    private static final SupervisedLesson LESSON5 = new SupervisedLesson("L5", 1,
            UNSUPERVISED_COURSE);

    private static final CourseEnrollment COURSE_ENROLLMENT1 = new CourseEnrollment(STUDENT1,
            SUPERVISED_COURSE);
    private static final CourseEnrollment COURSE_ENROLLMENT2 = new CourseEnrollment(STUDENT2,
            SUPERVISED_COURSE);
    private static final CourseEnrollment COURSE_ENROLLMENT3 = new CourseEnrollment(STUDENT3,
            SUPERVISED_COURSE);

    // --- words for lesson 1
    // student 1
    private static final WordToLearn WORD_TO_LEARN1 = new WordToLearn("w1", "t1", 1,
            COURSE_ENROLLMENT1, LESSON1);
    private static final WordToLearn WORD_TO_LEARN2 = new WordToLearn("w2", "t2", 1,
            COURSE_ENROLLMENT1, LESSON1);
    private static final WordToLearn WORD_TO_LEARN3 = new WordToLearn("w1", "t1", 1,
            COURSE_ENROLLMENT1, LESSON1);
    private static final WordToLearn WORD_TO_LEARN9 = new WordToLearn("w1", "t1", 1,
            COURSE_ENROLLMENT1, LESSON1);
    // student 2
    private static final WordToLearn WORD_TO_LEARN4 = new WordToLearn("w4", "t4", 1,
            COURSE_ENROLLMENT2, LESSON1);
    // student 3
    private static final WordToLearn WORD_TO_LEARN5 = new WordToLearn("w5", "t5", 1,
            COURSE_ENROLLMENT3, LESSON1);
    private static final WordToLearn WORD_TO_LEARN6 = new WordToLearn("w5", "t6", 1,
            COURSE_ENROLLMENT3, LESSON1);
    // --- words for lesson 2
    // student 2
    private static final WordToLearn WORD_TO_LEARN7 = new WordToLearn("w7", "t1", 1,
            COURSE_ENROLLMENT2, LESSON1);
    private static final WordToLearn WORD_TO_LEARN8 = new WordToLearn("w2", "t2", 1,
            COURSE_ENROLLMENT2, LESSON1);

    @BeforeEach
    void setUp() throws AccessRestrictedToTeachersException {
        SUPERVISED_COURSE.getEnrollments().add(COURSE_ENROLLMENT1);
        SUPERVISED_COURSE.getEnrollments().add(COURSE_ENROLLMENT2);
        SUPERVISED_COURSE.getEnrollments().add(COURSE_ENROLLMENT3);

        SUPERVISED_COURSE.addLesson(LESSON1);
        SUPERVISED_COURSE.addLesson(LESSON2);
        SUPERVISED_COURSE.addLesson(LESSON3);

        Mockito.when(authenticationService.getCurrentTeacher()).thenReturn(
                TEACHER);

        Mockito.when(supervisedCourseRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(SUPERVISED_COURSE));

        Mockito.when(supervisedCourseRepository.findById(2L)).thenReturn(Optional.empty());

        Mockito.when(supervisedCourseRepository.findById(3L)).thenReturn(Optional.of(UNSUPERVISED_COURSE));

        Mockito.when(wordToLearnRepository.findByLesson(LESSON1)).thenReturn(
                List.of(WORD_TO_LEARN1,
                        WORD_TO_LEARN2,
                        WORD_TO_LEARN3,
                        WORD_TO_LEARN9,
                        WORD_TO_LEARN4,
                        WORD_TO_LEARN5,
                        WORD_TO_LEARN6));

        Mockito.when(wordToLearnRepository.findByLesson(LESSON2)).thenReturn(
                List.of(WORD_TO_LEARN7,
                        WORD_TO_LEARN8));

        Mockito.when(supervisedLessonRepository.findById(1L)).thenReturn(Optional.of(LESSON1));

        Mockito.when(supervisedLessonRepository.findById(2L)).thenReturn(Optional.of(LESSON2));

        Mockito.when(supervisedLessonRepository.findById(4L)).thenReturn(Optional.empty());

        Mockito.when(supervisedLessonRepository.findById(5L)).thenReturn(Optional.of(LESSON5));
    }

    @Test
    void getCourseStatistics_validRequest() throws CourseNotFoundException, InvalidCourseAccessException, AccessRestrictedToTeachersException {
        CourseStatisticsDTO actualCoruseStatistics =
                teacherStatisticsService.getCourseStatistics(1L);

        CourseStatisticsDTO expectedCourseSatistics = new CourseStatisticsDTO(3, List.of("L1",
                "L2", "L3"), List.of((double) 7 / 3, (double) 2 / 3, (double) 0));

        assertThat(actualCoruseStatistics).usingRecursiveComparison().isEqualTo(expectedCourseSatistics);
    }

    @Test
    void getCourseStatistics_courseNotFoundException() {
        assertThrows(CourseNotFoundException.class, () -> {
            teacherStatisticsService.getCourseStatistics(2L);
        });
    }

    @Test
    void getCourseStatistics_invalidCourseAccessException() {
        assertThrows(InvalidCourseAccessException.class, () -> {
            teacherStatisticsService.getCourseStatistics(3L);
        });
    }

    @Test
    void getLessonStatistics_validRequest() throws InvalidCourseAccessException, AccessRestrictedToTeachersException, LessonNotFoundException {
        LessonStatisticsDTO actualLessonStatistics =
                teacherStatisticsService.getLessonStatistics(1L);

        LessonStatisticsDTO expectedLessonStatisticsDTO = new LessonStatisticsDTO("L1", 1, "c1",
                Map.of("w1", 3L, "w2", 1L, "w4", 1L, "w5", 2L));

        assertThat(actualLessonStatistics).usingRecursiveComparison().isEqualTo(expectedLessonStatisticsDTO);
    }

    @Test
    void getLessonStatistics_validRequest2() throws InvalidCourseAccessException,
            AccessRestrictedToTeachersException, LessonNotFoundException {
        LessonStatisticsDTO actualLessonStatistics =
                teacherStatisticsService.getLessonStatistics(2L);

        LessonStatisticsDTO expectedLessonStatisticsDTO = new LessonStatisticsDTO("L2", 2, "c1",
                Map.of("w2", 1L, "w7", 1L));

        assertThat(actualLessonStatistics).usingRecursiveComparison().isEqualTo(expectedLessonStatisticsDTO);
    }

    @Test
    void getLessonStatistics_invalidCourseAccessException() {
        assertThrows(InvalidCourseAccessException.class, () -> {
            teacherStatisticsService.getLessonStatistics(5L);
        });
    }

    @Test
    void getLessonStatistics_lessonNotFoundException() {
        assertThrows(LessonNotFoundException.class, () -> {
            teacherStatisticsService.getLessonStatistics(4L);
        });
    }
}