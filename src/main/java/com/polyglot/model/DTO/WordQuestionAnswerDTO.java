package com.polyglot.model.DTO;

import lombok.*;
import org.springframework.web.bind.annotation.RequestParam;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ToString
public class WordQuestionAnswerDTO {
    private Long wordToLearnId;
    private String submittedTranslation;
    private boolean foreignToNative;
}
