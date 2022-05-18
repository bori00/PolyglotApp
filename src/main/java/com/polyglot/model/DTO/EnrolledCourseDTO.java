package com.polyglot.model.DTO;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ToString
public class EnrolledCourseDTO {
    public Long id;
    public String title;
    public String language;
    public String teacher;
}
