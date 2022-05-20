package com.polyglot.model.DTO;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ToString
public class CourseStatisticsDTO {
    int noEnrolledStudents;
    List<String> lessonTitles;
    List<Double> avgNrOfUnknownWordsPerLesson;
}
