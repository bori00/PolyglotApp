package com.polyglot.controller;

import com.polyglot.model.DTO.LoginJwtDTO;
import com.polyglot.model.DTO.UserDTO;
import com.polyglot.service.authentication.LoginRegistrationService;
import com.polyglot.service.authentication.UserDetailsServiceImpl;
import com.polyglot.service.authentication.exceptions.DuplicateUsernameException;
import com.polyglot.service.authentication.jwt.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AuthenticationController {

    @Autowired
    private LoginRegistrationService loginRegistrationService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    void register(@Valid @RequestBody UserDTO userDTO) throws DuplicateUsernameException {
        logger.info(String.format("REQUEST - /register, for username %s",
                userDTO.getName()));
        loginRegistrationService.register(userDTO);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    /** Returns RESTAURANT_ADMIN or CUSTOMER as a string. */
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
        logger.info(String.format("REQUEST - /login, for username %s",
                userDTO.getName()));
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDTO.getName(),
                        userDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsServiceImpl.UserDetailsImpl userDetails = (UserDetailsServiceImpl.UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new LoginJwtDTO(
                userDetails.getUsername(),
                roles.get(0),
                jwt)); // todo: assumed just 1 role
    }

}
