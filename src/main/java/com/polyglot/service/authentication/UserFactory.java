package com.polyglot.service.authentication;

import com.polyglot.model.DTO.UserDTO;
import com.polyglot.model.User;
import com.polyglot.repository.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Factory class that builds a user from a given UserDTO.
 */
public class UserFactory {

    /**
     * Builds a User.
     * @param userDTO is the DTO based on which the new user is constructed.
     * @param passwordEncoder is the encoder to be applied on the user's password before
     *                        returning it.
     * @return the new user,
     */
    public static User buildUser(UserDTO userDTO,
                                 PasswordEncoder passwordEncoder,
                                 LanguageRepository languageRepository) {
       userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
       return userDTO.getUserType().buildUser(userDTO,
               languageRepository.findByName(userDTO.getNativeLanguage()).get());
    }
}
