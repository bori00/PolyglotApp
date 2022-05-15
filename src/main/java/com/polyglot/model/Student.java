package com.polyglot.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Set;

/**
 * Class representing a customer-type user.
 */
@Entity
@Table(name = "Student")
@ToString
@Setter
@Getter
@NoArgsConstructor
public class Student extends User {

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private Set<CourseEnrollment> enrollments;

    /**
     * @param userName is the name (identifier) of the customer.
     * @param password is the password of the user used for authentication.
     * @param emailAddress is the email address of the user.
     */
    public Student(String userName, String password, String emailAddress, Language nativeLanguage) {
        super(userName, password, emailAddress, nativeLanguage);
    }
}
