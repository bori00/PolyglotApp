package com.polyglot.model;

import javax.persistence.*;

@Entity
@Table(name = "self_taught_lesson")
public class SelfTaughtLesson extends Lesson {
    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name = "course_id", referencedColumnName = "Id")
    private SelfTaughtCourse course;
}
