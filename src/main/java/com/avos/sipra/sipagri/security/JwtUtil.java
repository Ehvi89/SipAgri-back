package com.avos.sipra.sipagri.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * Utility class for generating, parsing, and validating JSON Web Tokens (JWTs).
 * This class relies on an HMAC signing key derived from a secret injected via application properties.
 */
@Component
public class JwtUtil {

    /**
     * Represents the secret key used for signing and validating JSON Web Tokens (JWTs).
     * The value is injected from application properties using a Spring Expression Language (SpEL) annotation.
     * Typically, it is used to derive a cryptographic key for securing tokens in the application.
     * The secret key must be kept confidential to ensure the integrity and validity of generated tokens.
     */
    @Value("${spring.security.oauth2.resourceserver.opaquetoken.client-secret}")
    private String secretKey;

    /**
     * The cryptographic key used for signing and verifying JSON Web Tokens (JWTs).
     * This key is derived from the secret key provided via application properties and is initialized post-construction.
     * It ensures secure token generation and validation processes.
     */
    private Key key;

    /**
     * Initializes the HMAC signing key used for JWT generation and validation.
     * This method is called automatically after the class is constructed and all
     * required dependencies are injected. It converts the injected secret key
     * string into a {@code Key} instance, which is then used for creating and
     * parsing JSON Web Tokens (JWTs).
     *
     * @throws IllegalArgumentException if the secret key is invalid or cannot
     *                                  be converted into a valid HMAC key.
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a JSON Web Token (JWT) for the specified username.
     * The token includes the username as its subject, a timestamp for when it was issued,
     * an expiration time of one hour from the current time, and is signed with an HMAC
     * derived key using the HS256 algorithm.
     *
     * @param username the username to include as the subject in the token
     * @return a compact JWT as a String
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the username (subject) from the given JSON Web Token (JWT).
     *
     * @param token the JWT from which the username is to be extracted
     * @return the username (subject) contained within the provided token
     * @throws JwtException if the token structure is invalid or the parsing fails
     */
    public String extractUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            throw new JwtException("Invalid token structure: " + e.getMessage());
        }
    }

    /**
     * Validates the given JWT (JSON Web Token) against the provided user details to ensure
     * it is associated with the correct user and has not expired.
     *
     * @param token the JWT to be validated
     * @param userDetails the user details containing the expected username for validation
     * @return {@code true} if the token is valid, the username matches, and it has not expired;
     *         {@code false} otherwise
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return username != null
                    && username.equals(userDetails.getUsername())
                    && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if the given JSON Web Token (JWT) is expired based on its expiration claim.
     *
     * @param token the JWT to be checked for expiration
     * @return {@code true} if the token is expired, {@code false} otherwise
     */
    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
}
