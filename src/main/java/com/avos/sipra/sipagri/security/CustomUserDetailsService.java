package com.avos.sipra.sipagri.security;

import com.avos.sipra.sipagri.entities.Supervisor;
import com.avos.sipra.sipagri.repositories.SupervisorRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service implementation of {@link UserDetailsService} for loading user-specific data
 * during authentication. This class integrates with the Spring Security framework and
 * provides user information necessary for authentication and authorization.
 * <p>
 * The primary role of this implementation is to retrieve user details from a data source
 * using the {@link SupervisorRepository}, specifically for {@link Supervisor} entities.
 * The returned {@link UserDetails} is wrapped in a {@link CustomUserDetails} object which
 * extends Spring Security's {@link UserDetails} interface.
 * <p>
 * The {@code username} parameter is treated as the email address associated with a supervisor.
 * If the supervisor corresponding to the provided username is not found, the service will
 * throw a {@link UsernameNotFoundException}.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * Repository used for performing database operations related to {@link Supervisor} entities.
     * This component provides methods to retrieve supervisors, such as finding a supervisor
     * by their email address, primarily to support authentication and authorization functionality
     * in the application.
     * <p>
     * Typically interacts with the persistence layer, serving as a bridge between the service layer
     * and the database where supervisor data is stored.
     */
    private final SupervisorRepository supervisorRepository;

    /**
     * Constructs a new instance of {@link CustomUserDetailsService}.
     * The service relies on the provided {@link SupervisorRepository} to fetch
     * user-specific data during authentication processes.
     *
     * @param supervisorRepository the repository used to retrieve {@link Supervisor} entities
     */
    public CustomUserDetailsService(SupervisorRepository supervisorRepository) {
        this.supervisorRepository = supervisorRepository;
    }

    /**
     * Loads a user by their username (email address) for authentication purposes.
     * <p>
     * This method fetches a {@link Supervisor} entity from the data source using the provided
     * email address and wraps it into a {@link CustomUserDetails} object to adhere to the
     * {@link UserDetails} interface required by Spring Security.
     *
     * @param username the email address of the user to be loaded
     * @return a {@link UserDetails} object representing the user with their credentials and authorities
     * @throws UsernameNotFoundException if no user is found with the provided username
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Supervisor supervisor = supervisorRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        return new CustomUserDetails(supervisor);
    }
}

