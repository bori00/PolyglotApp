package com.polyglot.service.authentication;

import com.polyglot.model.Student;
import com.polyglot.model.Teacher;
import com.polyglot.model.User;
import com.polyglot.repository.StudentRepository;
import com.polyglot.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * A Service which allows SPring Security to find a user by their username.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    /**
     * Finds a user by their username and returns the corresonding UserDetails for Spring Security.
     * @param username is the name of the user to be found.
     * @return the UserDetails of the user with the given name.
     * @throws UsernameNotFoundException if no user with the given username exists.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Teacher> optTeacher = teacherRepository.findByUserName(username);
        if (optTeacher.isPresent()) {
            return new UserDetailsImpl(optTeacher.get());
        }
        Optional<Student> optStudent = studentRepository.findByUserName(username);
        if (optStudent.isPresent()) {
            return new UserDetailsImpl(optStudent.get());
        }
        throw new UsernameNotFoundException("User Not Found with username: " + username);
    }

    /**
     * A class holding the details of a user in the format requested by SPring Security.
     */
    public static class UserDetailsImpl implements UserDetails {

        private final User user;
        private final Collection<GrantedAuthority> authorities;

        public enum Authorities {
            TEACHER,
            STUDENT
        }

        public UserDetailsImpl(Teacher admin) {
            this.user = admin;
            this.authorities = List.of(new SimpleGrantedAuthority(Authorities.TEACHER.toString()));
        }

        public UserDetailsImpl(Student Student) {
            this.user = Student;
            this.authorities = List.of(new SimpleGrantedAuthority(Authorities.STUDENT.toString()));
        }

        /** {@inheritDoc} */
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }

        /** {@inheritDoc} */
        @Override
        public String getPassword() {
            return user.getPassword();
        }

        /** {@inheritDoc} */
        @Override
        public String getUsername() {
            return user.getUserName();
        }

        /** {@inheritDoc} */
        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        /** {@inheritDoc} */
        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        /** {@inheritDoc} */
        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        /** {@inheritDoc} */
        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}
