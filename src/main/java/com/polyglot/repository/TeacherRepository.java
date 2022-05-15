package com.polyglot.repository;

import com.polyglot.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository  extends JpaRepository<Teacher, Long> {
    /**
     * @param username the name of the searched user.
     * @return the user with the given username, if any, otherwise an empty Optional.
     */
    Optional<Teacher> findByUserName(String username);
}