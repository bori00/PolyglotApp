package com.polyglot.repository;

import com.polyglot.model.CourseEnrollment;
import com.polyglot.model.Lesson;
import com.polyglot.model.WordToLearn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WordToLearnRepository extends JpaRepository<WordToLearn, Long> {
    List<WordToLearn> findByLessonAndCourseEnrollment(Lesson lesson,
                                                      CourseEnrollment courseEnrollment);
}
