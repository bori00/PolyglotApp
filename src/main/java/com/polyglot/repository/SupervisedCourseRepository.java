package com.polyglot.repository;

import com.polyglot.model.SupervisedCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupervisedCourseRepository extends JpaRepository<SupervisedCourse, Long> {

    Optional<SupervisedCourse> findByJoiningCode(Integer joiningCode);
}
