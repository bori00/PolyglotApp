package com.polyglot.model.DTO;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ToString
public class LessonDTO {
    private String title;
    private Long courseId;
    private Integer indexInsideCourse;
    private String courseTitle;
}
