package com.polyglot.model.DTO;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ToString
public class WordLearningExerciseEvaluationDTO {
    private Long wordToLearnId;
    private Long lessonId;
    private String word;
    private int currentPoints;
    private int targetPoints;
    private String officialTranslation;
    private String submittedTranslation;
    private boolean accepted;
    private String sourceLanguage;
    private String targetLanguage;
    private boolean foreignToNative;
}
