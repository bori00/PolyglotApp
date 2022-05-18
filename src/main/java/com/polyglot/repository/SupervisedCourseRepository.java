package com.polyglot.repository;

import com.polyglot.model.SupervisedCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupervisedCourseRepository extends JpaRepository<SupervisedCourse, Long> {
}
