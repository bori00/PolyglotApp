package com.polyglot.service.util;

import com.polyglot.model.Language;
import com.polyglot.repository.LanguageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UtilService {

    @Autowired
    private LanguageRepository languageRepository;

    public List<String> getAllLanguages() {
        return languageRepository.findAll().stream().map(Language::getName).collect(Collectors.toList());
    }
}


