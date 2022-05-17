package com.polyglot.repository;

import com.polyglot.model.WordToLearn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordToLearnRepository extends JpaRepository<WordToLearn, Long> {
}
