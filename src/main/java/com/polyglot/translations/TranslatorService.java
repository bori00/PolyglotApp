package com.polyglot.translations;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.testing.RemoteTranslateHelper;
import com.polyglot.model.Language;
import com.polyglot.service.student_course_management.StudentCourseManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Service responsible for generating and verifying translations.
 */
@Service
public class TranslatorService {

    private static final Logger logger = LoggerFactory.getLogger(TranslatorService.class);

    /**
     * Translates a word.
     * @param word is the word to translate.
     * @param sourceLanguage is the language of the word.
     * @param targetLanguage is the language to translate to.
     * @return the translated word.
     */
    public String getTranslation(String word, Language sourceLanguage, Language targetLanguage) {
        if (sourceLanguage.equals(targetLanguage)) {
            return word;
        }

        RemoteTranslateHelper helper = RemoteTranslateHelper.create();
        Translate translate = helper.getOptions().getService();

        String translation = translate.translate(word,
                    Translate.TranslateOption.sourceLanguage(sourceLanguage.getAPI_ID()),
                    Translate.TranslateOption.targetLanguage(targetLanguage.getAPI_ID())).getTranslatedText().toLowerCase(Locale.ROOT);

        logger.info("EVENT - Translated {} in {} to {} in {}", word, sourceLanguage.getName(),
                translation, targetLanguage.getName());

        return translation;
    }

    /**
     * Verifies that the translation from word to translatedWord is correct.
     * @param word is the source word.
     * @param translatedWord is the target word.
     * @param sourceLanguage is the language of word.
     * @param targetLanguage is the language of translatedWord.
     * @return true if and only if translatedWord in targetLanguage means the same as word in
     * sourceLanguage.
     */
    public boolean isCorrectTranslation(String word,
                                        String translatedWord,
                                        Language sourceLanguage,
                                        Language targetLanguage) {

        boolean correct =
                getTranslation(word, sourceLanguage, targetLanguage).equals(translatedWord.toLowerCase(Locale.ROOT)) ||
                getTranslation(translatedWord, targetLanguage, sourceLanguage).equals(word.toLowerCase(Locale.ROOT));

        if (correct) {
            logger.info("EVENT - Accepted translation {} in {} to {} in {}", word,
                    sourceLanguage.getName(),
                    translatedWord, targetLanguage.getName());
        } else {
            logger.info("EVENT - Declined translation {} in {} to {} in {}", word,
                    sourceLanguage.getName(),
                    translatedWord, targetLanguage.getName());
        }

        return correct;
    }
}
