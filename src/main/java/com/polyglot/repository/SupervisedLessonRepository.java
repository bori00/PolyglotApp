package com.polyglot.repository;

import com.polyglot.model.SupervisedLesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupervisedLessonRepository extends JpaRepository<SupervisedLesson, Long> {
}
