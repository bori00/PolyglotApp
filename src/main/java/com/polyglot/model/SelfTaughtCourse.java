package com.polyglot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "self_taught_course")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SelfTaughtCourse extends Course {
    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name = "creator_id", referencedColumnName = "Id")
    // eager loading by default
    private Student creator;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    @OrderBy("indexInsideCourse")
    // lazy loading by default
    private Set<SelfTaughtLesson> lessons;

    public SelfTaughtCourse(String title, Integer minPointsPerWord, Language language, Student creator) {
        super(title, minPointsPerWord, language);
        this.creator = creator;
    }

    @Override
    public String toString() {
        return "SelfTaughtCourse{" +
                super.toString() +
                "creator=" + creator +
                '}';
    }

    @Override
    public User getSupervisor() {
        return creator;
    }


}
