package com.polyglot.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "word_to_learn")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class WordToLearn {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false)
    private String originalWord;

    @Column(nullable = false)
    private String translation;

    @Column(nullable = false)
    private Integer collectedPoints;
    // min 0, max 100. Above a certain threshold, specified for each Course, a word is considered
    // to be learnt. Each correct answers add +1 point, each wrong answer adds -2 points.

    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name = "course_enrollment_id", referencedColumnName = "Id")
    // eager loading by default
    private CourseEnrollment courseEnrollment;

    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name = "lesson_id", referencedColumnName = "Id")
    // eager loading by default
    private Lesson lesson;

    public WordToLearn(String originalWord, String translation, Integer collectedPoints, CourseEnrollment courseEnrollment, Lesson lesson) {
        this.originalWord = originalWord;
        this.translation = translation;
        this.collectedPoints = collectedPoints;
        this.courseEnrollment = courseEnrollment;
        this.lesson = lesson;
    }
}
