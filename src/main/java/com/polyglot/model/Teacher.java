package com.polyglot.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Ad Admin-type user.
 */
@Entity(name = "Admin")
@ToString
@Setter
@NoArgsConstructor
@Getter
public class Teacher extends User {

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL)
    private Set<SupervisedCourse> taughtCourses;

    public Teacher(String userName, String password, String emailAddress) {
        super(userName, password, emailAddress);
        this.taughtCourses = new HashSet<>();
    }
}
