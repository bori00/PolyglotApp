package com.polyglot.service.authentication;

import com.polyglot.model.DTO.UserDTO;
import com.polyglot.model.User;
import com.polyglot.repository.LanguageRepository;
import com.polyglot.repository.UserRepository;
import com.polyglot.service.authentication.exceptions.DuplicateUsernameException;
import com.polyglot.service.authentication.jwt.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service that implements functionalities related to user authentication: registration.
 */
@Service
public class LoginRegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private LanguageRepository languageRepository;

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    /**
     * Registers a user with the data specified in the userDTO.
     *
     * @param userDTO holds the registration data.
     * @throws DuplicateUsernameException if the requested username is already taken.
     */
    public void register(UserDTO userDTO) throws DuplicateUsernameException {
        System.out.println(userDTO);

        // check that username is not taken
        Optional<User> userWithSameName = userRepository.findByUserName(userDTO.getName());
        if (userWithSameName.isPresent()) {
            logger.info(String.format("INVALID UPDATE - Failed registration: %s name already " +
                    "taken.", userDTO.getName()));
            throw new DuplicateUsernameException();
        }

        // save new user
        User user = UserFactory.buildUser(userDTO, passwordEncoder, languageRepository);

        logger.info(String.format("UPDATE - new user registered with name %s.",
                user.getUserName()));
        userRepository.save(user);
    }
}
