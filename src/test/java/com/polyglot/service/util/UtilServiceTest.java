package com.polyglot.service.util;

import com.polyglot.PolyglotApplication;
import com.polyglot.model.Language;
import com.polyglot.repository.LanguageRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = PolyglotApplication.class)
class UtilServiceTest {

    @Autowired
    private UtilService utilService;

    @MockBean
    private LanguageRepository languageRepository;

    private static final Language LANGUAGE1 = new Language(1L, "l1", "LANG1");
    private static final Language LANGUAGE2 = new Language(1L, "l2", "LANG2");
    private static final Language LANGUAGE3 = new Language(1L, "l3", "LANG3");
    private static final Language LANGUAGE4 = new Language(1L, "l4", "LANG4");

    @Test
    void getAllLanguages_existingLanguages() {
        Mockito.when(languageRepository.findAll()).thenReturn(List.of(LANGUAGE1, LANGUAGE2,
                LANGUAGE3, LANGUAGE4));

        List<String> expectedLanguageNames = List.of("l1", "l2", "l3", "l4");

        List<String> actualLanguageNames = utilService.getAllLanguages();

        Assertions.assertIterableEquals(expectedLanguageNames, actualLanguageNames);
    }

    @Test
    void getAllLanguages_noExistingLanguages() {
        Mockito.when(languageRepository.findAll()).thenReturn(List.of());

        List<String> expectedLanguageNames = new ArrayList<>();

        List<String> actualLanguageNames = utilService.getAllLanguages();

        Assertions.assertIterableEquals(expectedLanguageNames, actualLanguageNames);
    }
}