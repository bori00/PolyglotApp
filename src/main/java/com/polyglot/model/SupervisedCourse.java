package com.polyglot.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "supervised_course")
public class SupervisedCourse extends Course {
    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name = "teacher_id", referencedColumnName = "Id")
    // eager loading by default
    private Teacher teacher;

    @Column(nullable = false)
    private Integer joiningCode;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    @OrderBy("indexInsideCourse")
    // lazy loading by default
    private Set<SupervisedLesson> lessons;
}
