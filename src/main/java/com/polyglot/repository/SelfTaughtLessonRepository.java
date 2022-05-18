package com.polyglot.repository;

import com.polyglot.model.SelfTaughtLesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SelfTaughtLessonRepository extends JpaRepository<SelfTaughtLesson, Long> {
}
