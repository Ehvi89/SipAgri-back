package com.avos.sipra.sipagri.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JwtRequestFilter is responsible for handling JWT authentication for incoming HTTP requests.
 * It extends the {@link OncePerRequestFilter} class, ensuring the filter is executed
 * only once per request within a single request thread.
 * <p>
 * The filter performs the following functions:
 * <p>
 * - Excludes specific paths from filtering based on predefined paths in {@code EXCLUDED_PATHS}.
 * - Validates the presence and structure of the Authorization header in the request.
 * - Extracts and validates the JWT token from the Authorization header.
 * - Retrieves the username from the token and loads user details using {@link CustomUserDetailsService}.
 * - Validates the token against the loaded user details.
 * - Sets up Spring Security context with the authenticated user if the validation is successful.
 * <p>
 * If any step of the token validation fails (e.g., missing token, invalid token, expired token),
 * an appropriate error response is sent with an HTTP 401 status code, and the request is terminated.
 * <p>
 * The {@link CustomUserDetailsService} is utilized to load user-specific data required for
 * authentication, and the {@link JwtUtil} is used for token parsing and validation.
 */
@Component
@Profile("!test")
public class JwtRequestFilter extends OncePerRequestFilter {

    /**
     * A list of URI paths that are excluded from JWT filtering.
     * <p>
     * These paths typically represent endpoints such as authentication, registration,
     * and error handling, which do not require JWT token validation. Requests to these
     * paths will bypass the JWT filter logic.
     */
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/refresh-token",
            "/api/v1/auth/forgot-password",
            "/api/v1/auth/reset-password",
            "/error"
    );

    /**
     * Service used for retrieving user-specific data, specifically targeting the
     * authentication and authorization processes within the Spring Security framework.
     * <p>
     * This dependency is responsible for loading user details during security operations,
     * particularly when verifying credentials and obtaining roles or permissions of a user.
     * It integrates directly with the authentication mechanism, ensuring accurate user
     * and authority resolution from the data source.
     */
    private final CustomUserDetailsService userDetailsService;
    /**
     * Utility object for handling JSON Web Token (JWT) operations such as token generation,
     * validation, and extracting user information within the context of filtering requests.
     * This instance is injected to facilitate JWT-driven authentication and authorization logic.
     */
    private final JwtUtil jwtUtil;

    /**
     * Constructs a new instance of {@link JwtRequestFilter}.
     * This filter is responsible for handling incoming HTTP requests by verifying JWT tokens
     * and ensuring authentication and authorization are properly established using the provided services.
     *
     * @param userDetailsService the service used to retrieve user details and verify user-specific information
     * @param jwtUtil the utility component for creating, validating, and extracting data from JWT tokens
     */
    public JwtRequestFilter(CustomUserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Determines whether a given HTTP request should bypass filtering.
     *
     * @param request the {@link HttpServletRequest} object to check for exclusion
     * @return {@code true} if the request path matches any excluded paths and should not be filtered;
     *         {@code false} otherwise
     */
    @Override
    protected boolean shouldNotFilter(@org.springframework.lang.NonNull HttpServletRequest request) {
        return EXCLUDED_PATHS.stream()
                .anyMatch(path -> request.getServletPath().startsWith(path));
    }

    /**
     * Processes incoming HTTP requests to implement JWT-based authentication. If the request contains
     * a valid JWT token in the "Authorization" header, the user details are extracted, validated, and
     * the user's authentication is established in the security context. If the token is invalid or
     * missing, an error response is sent back to the client.
     *
     * @param request  the incoming HTTP servlet request
     * @param response the HTTP servlet response to be sent
     * @param chain    the filter chain to pass the request and response to the next filter
     * @throws ServletException if an issue occurs while processing the request
     * @throws IOException      if an I/O error occurs during the request/response handling
     */
    @Override
    protected void doFilterInternal(@org.springframework.lang.NonNull HttpServletRequest request,
                                    @org.springframework.lang.NonNull HttpServletResponse response,
                                    @org.springframework.lang.NonNull FilterChain chain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // Pour les routes protégées, exiger un token valide
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header missing or invalid");
            return;
        }

        try {
            final String token = authHeader.substring(7);

            // Validation basique du token avant traitement
            if (token.isEmpty()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }

            final String username = jwtUtil.extractUsername(token);

            if (username == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (!jwtUtil.validateToken(token, userDetails)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                return;
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            logger.error("Authentication error", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
            return;
        }

        chain.doFilter(request, response);
    }
}