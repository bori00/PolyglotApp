package com.polyglot.service.authentication;

import com.polyglot.model.Student;
import com.polyglot.model.Teacher;
import com.polyglot.model.User;
import com.polyglot.repository.StudentRepository;
import com.polyglot.repository.TeacherRepository;
import com.polyglot.repository.UserRepository;
import com.polyglot.service.authentication.exceptions.AccessRestrictedToStudentsException;
import com.polyglot.service.authentication.exceptions.AccessRestrictedToTeachersException;
import com.polyglot.service.authentication.exceptions.AuthenticationRequiredException;
import com.polyglot.service.authentication.jwt.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service that implements functionalities related to user authentication: it provides data about
 * the currently logged in user.
 */
@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    /**
     * Returns the currently logged in user.
     *
     * @return the currently logged in user.
     * @throws AuthenticationRequiredException if no authenticated user exists.
     */
    public User getCurrentUser() throws AuthenticationRequiredException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optUser =
                userRepository.findByUserName(((UserDetailsServiceImpl.UserDetailsImpl) auth.getPrincipal()).getUsername());
        if (optUser.isEmpty()) {
            logger.warn("User tried to access resource without being authenticated");
            throw new AuthenticationRequiredException();
        }
        return optUser.get();
    }

    /**
     * Returns the currently logged in Teacher.
     *
     * @return the currently logged in Teacher.
     * @throws AccessRestrictedToTeachersException, if no authenticated user exists, or the
     *                                              logged-in user is not an Teacher.
     */
    public Teacher getCurrentTeacher() throws AccessRestrictedToTeachersException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Teacher> optUser =
                teacherRepository.findByUserName(((UserDetailsServiceImpl.UserDetailsImpl) auth.getPrincipal()).getUsername());
        if (optUser.isEmpty()) {
            logger.warn("Non-Teacher User tried to access Teacher-Only resources.");
            throw new AccessRestrictedToTeachersException();
        }
        return optUser.get();
    }

    /**
     * Returns the currently logged in Student.
     *
     * @return the currently logged in Student.
     * @throws AccessRestrictedToStudentsException, if no authenticated user exists, or the
     *                                              logged-in user is not a Student.
     */
    public Student getCurrentStudent() throws AccessRestrictedToStudentsException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Student> optUser =
                studentRepository.findByUserName(((UserDetailsServiceImpl.UserDetailsImpl) auth.getPrincipal()).getUsername());
        if (optUser.isEmpty()) {
            logger.warn("Non-Student User tried to access Student-Only resources.");
            throw new AccessRestrictedToStudentsException();
        }
        return optUser.get();
    }
}
