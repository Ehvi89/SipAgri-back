package com.avos.sipra.sipagri;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the SipAgri backend application.
 * <p>
 * This Spring Boot application exposes a REST API for managing SIPRA corn planters,
 * their fields and production cycles. The application relies on Oracle database with
 * Liquibase-managed schema migrations, and uses JWT-based authentication.
 * </p>
 */
@SpringBootApplication
public class SipAgriApplication {

    /**
     * Bootstraps the Spring application context and starts the embedded web server.
     *
     * @param args standard command-line arguments, unused by default
     */
    public static void main(String[] args) {
        SpringApplication.run(SipAgriApplication.class, args);
    }

}
