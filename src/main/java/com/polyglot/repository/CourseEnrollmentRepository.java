package com.polyglot.repository;

import com.polyglot.model.CourseEnrollment;
import com.polyglot.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {

    public List<CourseEnrollment> findByStudent(Student student);
}
