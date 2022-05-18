package com.polyglot.model.DTO;

import lombok.*;

import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ToString
public class ExtendedTaughtCourseDTO {
    public Long id;
    public String title;
    public String language;
    public int nrOfStudents;
    private Map<Long, String> lessonIdsToTitle;
}
