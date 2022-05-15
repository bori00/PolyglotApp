package com.polyglot.repository;

import com.polyglot.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, Long> {

    Language findByName(String name);
}
