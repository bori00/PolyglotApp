package com.polyglot.model.DTO;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ToString
public class LessonStatisticsDTO {
     String title;
     Integer indexInsideCourse;
     String courseTitle;
     Map<String, Long> unknownWordsToFrequency;
}
