package com.polyglot.repository;

import com.polyglot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository responsible for User-related CRUD operations in the database.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * @param username the name of the searched user.
     * @return the user with the given username, if any, otherwise an empty Optional.
     */
    Optional<User> findByUserName(String username);
}
