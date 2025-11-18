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
 * Utility component for creating and validating JSON Web Tokens (JWT).
 * <p>
 * The signing key is derived from the application property
 * {@code spring.security.oauth2.resourceserver.opaquetoken.client-secret}.
 * For HS256, the secret must be sufficiently long (at least 256 bits) to avoid
 * weak key errors. Tokens currently expire 1 hour after issuance.
 * </p>
 * <p>
 * This class is stateless except for the derived {@link #key} built at startup.
 * </p>
 */
@Component
public class JwtUtil {

    /**
     * Secret used to derive the HMAC signing key. Injected from application properties.
     */
    @Value("${spring.security.oauth2.resourceserver.opaquetoken.client-secret}")
    private String secretKey;

    /**
     * Derived HMAC key used to sign and verify tokens.
     */
    private Key key;

    /**
     * Initializes the signing key from the configured secret. Called once after bean construction.
     * <p>
     * Note: For HS256, ensure the secret length is adequate; otherwise {@code Keys.hmacShaKeyFor}
     * will throw an exception at startup.
     * </p>
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a compact JWT for the given username (as subject), with a 1 hour expiration.
     *
     * @param username the principal identifier to embed as {@code sub}
     * @return a compact serialized JWT string
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
     * Extracts the username (token subject) from the given JWT.
     *
     * @param token compact JWT string
     * @return the embedded subject (username)
     * @throws JwtException if the token cannot be parsed or is malformed
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
     * Validates a token for the given {@link UserDetails} by comparing the subject and expiration.
     *
     * @param token       compact JWT string
     * @param userDetails the user details to validate against
     * @return {@code true} if token belongs to the provided user and is not expired; {@code false} otherwise
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
     * Checks whether the provided token is expired according to its {@code exp} claim.
     *
     * @param token compact JWT string
     * @return {@code true} if expired, otherwise {@code false}
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
