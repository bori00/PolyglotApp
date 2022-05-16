package com.polyglot.model.DTO;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.io.IOException;
import java.io.StringWriter;
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
