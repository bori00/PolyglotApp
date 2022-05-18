package com.polyglot.repository;

import com.polyglot.model.Course;
import com.polyglot.model.CourseEnrollment;
import com.polyglot.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {

    List<CourseEnrollment> findByStudent(Student student);

    Optional<CourseEnrollment> findByCourseAndStudent(Course course, Student student);
}
