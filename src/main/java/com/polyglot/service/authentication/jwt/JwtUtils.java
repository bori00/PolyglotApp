package com.polyglot.service.authentication.jwt;


import com.polyglot.service.authentication.UserDetailsServiceImpl;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Utility class that generates and parses JWT tokens.
 */
@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${foodDelivery.app.jwtSecret}")
    private String jwtSecret;

    @Value("${foodDelivery.app.jwtExpirationMs}")
    private int jwtExpirationMs;


    /**
     * Generates a JWT token for the given user based on their name, the currant date and with an
     * expiration date.
     * @param authentication specifies the user for whom the token is generated.
     * @return a string representing a JWT token.
     */
    public String generateJwtToken(Authentication authentication) {
        UserDetailsServiceImpl.UserDetailsImpl userPrincipal = (UserDetailsServiceImpl.UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * Parses a JWT token and returns the username from it.
     * @param token is the string token to be parsed.
     * @return the username extracted from the token.
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Validates a JWT token.
     * @param authToken is the token to validate.
     * @return true if the token is valid, false otherwise.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("JWT ERROR - Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("JWT ERROR - Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT ERROR - JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT ERROR - JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT ERROR - JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
