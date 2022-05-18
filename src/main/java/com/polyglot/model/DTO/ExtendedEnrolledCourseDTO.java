package com.polyglot.model.DTO;

import lombok.*;

import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ToString
public class ExtendedEnrolledCourseDTO {
    public Long id;
    public String title;
    public String language;
    public String teacher;
    private Map<Long, String> lessonIdsToTitle;
}
