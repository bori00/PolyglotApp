package com.polyglot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "course_enrollment")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CourseEnrollment {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name = "student_id", referencedColumnName = "Id")
    // eager loading by default
    private Student student;

    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name = "course_id", referencedColumnName = "Id")
    // eager loading by default
    private Course course;

    public CourseEnrollment(Student student) {
        this.student = student;
    }

    public CourseEnrollment(Student student, Course course) {
        this.student = student;
        this.course = course;
    }
}
