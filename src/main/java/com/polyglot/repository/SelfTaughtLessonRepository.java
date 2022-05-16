package com.polyglot.repository;

import com.polyglot.model.SelfTaughtLesson;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.*;

public interface SelfTaughtLessonRepository extends JpaRepository<SelfTaughtLesson, Long> {
}
