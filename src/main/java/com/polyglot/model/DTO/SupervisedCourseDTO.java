package com.polyglot.model.DTO;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * DTO used for client-server communication, representing a supervised course.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ToString
public class SupervisedCourseDTO {
    @NotBlank(message = "The title cannot be blank.")
    @Size(min = 3, max = 30, message = "The title should have a length between 3 and " +
            "30")
    private String title;

    @NotBlank(message = "The language cannot be blank.")
    private String language;

    @Min(value = 1, message = "The Word Target must be at least 1")
    @Max(value = 25, message = "The Word Target should be at most 25")
    private int minPointsPerWord;
}