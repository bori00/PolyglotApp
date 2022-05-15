package com.polyglot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.units.qual.C;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "course")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Course {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer minPointsPerWord;

    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name = "language_id", referencedColumnName = "Id")
    private Language language;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    // lazy loading by default
    private Set<CourseEnrollment> enrollments;

    public Course(String title, Integer minPointsPerWord, Language language) {
        this.title = title;
        this.minPointsPerWord = minPointsPerWord;
        this.language = language;
        this.enrollments = new HashSet<>();
    }

    @Override
    public String toString() {
        return "Course{" +
                "Id=" + Id +
                ", title='" + title + '\'' +
                ", minPointsPerWord=" + minPointsPerWord +
                ", language=" + language +
                ", enrollments=" + enrollments +
                '}';
    }

    public abstract User getSupervisor();

    public void addCourseEnrollment(Student student) {
        CourseEnrollment enrollment = new CourseEnrollment(student);
        this.enrollments.add(enrollment);
        enrollment.setCourse(this);
    }
}
