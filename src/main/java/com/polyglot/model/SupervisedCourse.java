package com.polyglot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "supervised_course")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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

    @Override
    public User getSupervisor() {
        return teacher;
    }

    @Override
    public List<Lesson> getLessons() {
        return new ArrayList<>(lessons);
    }
}
