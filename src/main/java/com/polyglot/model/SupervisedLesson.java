package com.polyglot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "supervised_lesson")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupervisedLesson extends Lesson {
    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name = "course_id", referencedColumnName = "Id")
    private SupervisedCourse course;

    @Override
    public Course getCourse() {
        return course;
    }
}
