package com.polyglot.repository;

import com.polyglot.model.Student;
import com.polyglot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    /**
     * @param username the name of the searched user.
     * @return the user with the given username, if any, otherwise an empty Optional.
     */
    Optional<Student> findByUserName(String username);
}
