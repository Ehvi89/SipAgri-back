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

@Configuration
@EnableWebSecurity
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
public class SecurityConfig {

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    /**
     * Configures the main Spring Security filter chain.
     *
     * - Enables CORS with configured origins
     * - Disables CSRF (stateless API)
     * - Permits /api/v1/auth/** and /error without authentication
     * - Requires authentication for all other requests
     * - Sets session management to STATELESS
     * - Adds {@link JwtRequestFilter} before {@link UsernamePasswordAuthenticationFilter}
     * - Customizes 401/403 responses as JSON
     *
     * @param http the HTTP security builder
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if configuration fails
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
     * Defines the CORS configuration for the API.
     * <p>
     * Allowed origins include the configured frontend URL from {@code app.frontend.url}
     * and some local development URLs. Credentials are allowed so Authorization headers
     * and cookies can be sent when required.
     * </p>
     *
     * @return a {@link CorsConfigurationSource} applied to all paths
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
     * Exposes the {@link AuthenticationManager} built by Spring from configured providers.
     *
     * @param config the auto-configured authentication configuration
     * @return the application {@link AuthenticationManager}
     * @throws Exception if the manager cannot be obtained
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Password encoder used to hash user passwords (BCrypt).
     *
     * @return a BCrypt-based {@link PasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
