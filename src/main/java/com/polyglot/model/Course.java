package com.polyglot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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
}
