package com.polyglot.service.util;

import com.polyglot.model.Language;
import com.polyglot.repository.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsible for utility requests.
 */
@Service
public class UtilService {

    @Autowired
    private LanguageRepository languageRepository;

    /**
     * Finds and returns all languages supported by the application.
     *
     * @return the list of the names of the languages supported by the application.
     */
    public List<String> getAllLanguages() {
        return languageRepository.findAll().stream().map(Language::getName).collect(Collectors.toList());
    }
}


