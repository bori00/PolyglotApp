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

    @OneToOne
    @JoinColumn(name = "native_language_id", nullable = false)
    private Language nativeLanguage;

    /**
     * @param userName is the name (identifier) of the customer.
     * @param password is the password of the user used for authentication.
     * @param emailAddress is the email address of the user.
     */
    public Student(String userName, String password, String emailAddress) {
        super(userName, password, emailAddress);
    }
}
