package com.polyglot.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "self_taught_course")
public class SelfTaughtCourse extends Course {
    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name = "creator_id", referencedColumnName = "Id")
    // eager loading by default
    private Student creator;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    @OrderBy("indexInsideCourse")
    // lazy loading by default
    private Set<SelfTaughtLesson> lessons;


}
