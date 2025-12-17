package com.avos.sipra.sipagri.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security configuration for the SipAgri REST API.
 * <p>
 * The application is secured using stateless JWT authentication. This configuration:
 * </p>
 * <ul>
 *   <li>Enables CORS for the configured frontend origins</li>
 *   <li>Disables CSRF (token-based API)</li>
 *   <li>Permits unauthenticated access to auth endpoints and HTTP OPTIONS</li>
 *   <li>Requires authentication for all other endpoints</li>
 *   <li>Sets session management to STATELESS</li>
 *   <li>Registers a {@link JwtRequestFilter} before {@link org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter}</li>
 *   <li>Customizes 401/403 error responses as JSON</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Represents the base URL of the frontend application.
     * This value is dynamically injected from the application properties
     * using the Spring Expression Language (SpEL) annotation.
     * <p>
     * It is typically used to define the frontend resource location
     * for enabling cross-origin resource sharing (CORS) or redirecting users
     * to the frontend application after specific operations.
     */
    @Value("${app.frontend.url}")
    private String frontendUrl;

    /**
     * JwtRequestFilter is a dependency that handles JSON Web Token (JWT) authentication for HTTP requests.
     * It is responsible for validating incoming requests by processing the JWT token provided in the
     * "Authorization" header, extracting and validating user details, and populating the Spring Security
     * context with the authenticated user's credentials.
     * <p>
     * This filter ensures proper access control for protected endpoints by rejecting requests with missing,
     * invalid, or expired tokens. Additionally, it identifies paths that are explicitly excluded from JWT
     * filtering to allow unauthenticated access when appropriate.
     * <p>
     * The JwtRequestFilter is designed to integrate seamlessly with Spring Security's filter chain and is
     * essential for enforcing authentication and authorization within the application's security framework.
     */
    private final JwtRequestFilter jwtRequestFilter;

    /**
     * Constructs a new instance of {@link SecurityConfig}.
     * This configuration class sets up the security infrastructure for the application
     * by integrating the provided {@link JwtRequestFilter} to manage JWT-based authentication.
     *
     * @param jwtRequestFilter the filter responsible for handling and validating JWT tokens
     *                         in incoming HTTP requests
     */
    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    /**
     * Configures a security filter chain for the application. This method sets up
     * CORS, CSRF, session management, exception handling, and authentication filters
     * for securing API endpoints.
     *
     * @param http the {@link HttpSecurity} object used to configure the security settings
     *             for the application.
     * @return the configured {@link SecurityFilterChain} that defines the security
     *         mechanisms and filters applied to incoming requests.
     * @throws Exception if an error occurs during the configuration of the security filter chain.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/error").permitAll()
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .exceptionHandling(exception -> exception
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write(
                        String.format("{\"error\":\"%s\",\"message\":\"%s\"}",
                            "Forbidden",
                            accessDeniedException.getMessage())
                    );
                })
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write(
                        String.format("{\"error\":\"%s\",\"message\":\"%s\"}",
                            "Unauthorized",
                            authException.getMessage())
                    );
                })
            );

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Configures Cross-Origin Resource Sharing (CORS) for the application.
     * It allows specifying origins, HTTP methods, headers, and credentials support
     * to handle requests from different origins securely.
     *
     * @return a configured {@link CorsConfigurationSource} instance that defines
     *         CORS policies for handling requests across different origins
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(frontendUrl, "http://localhost:4200", "https://localhost:8080", "https://192.168.10.195:8080", "http://localhost:8080", "http://192.168.10.195:8080", "http://af2-sge-1:8080", "https://af2-sge-1:8080"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // si tu utilises cookies ou Authorization header

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Creates and provides a Spring Security {@link AuthenticationManager} bean.
     * The {@link AuthenticationManager} is retrieved from the provided {@link AuthenticationConfiguration},
     * which contains the security configuration for handling authentication.
     *
     * @param config the {@link AuthenticationConfiguration} used to configure and retrieve the {@link AuthenticationManager}
     * @return the configured {@link AuthenticationManager} instance
     * @throws Exception if an error occurs during the retrieval of the {@link AuthenticationManager}
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Provides a {@link PasswordEncoder} bean for encoding passwords.
     * This bean utilizes the BCrypt hashing algorithm to ensure password security.
     *
     * @return an instance of {@link PasswordEncoder} using BCrypt hashing
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
