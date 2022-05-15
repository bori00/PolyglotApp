package com.polyglot.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "supervised_lesson")
public class SupervisedLesson extends Lesson {
    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name = "course_id", referencedColumnName = "Id")
    private SupervisedCourse course;
}
