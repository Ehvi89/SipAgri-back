package com.avos.sipra.sipagri.services.cores.impl;

import com.avos.sipra.sipagri.entities.PasswordResetToken;
import com.avos.sipra.sipagri.enums.SupervisorProfile;
import com.avos.sipra.sipagri.repositories.PasswordResetTokenRepository;
import com.avos.sipra.sipagri.services.cores.AuthService;
import com.avos.sipra.sipagri.services.cores.SupervisorService;
import com.avos.sipra.sipagri.services.dtos.SupervisorDTO;
import com.avos.sipra.sipagri.services.mappers.SupervisorMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Implementation of the AuthService interface that handles user authentication and password management operations.
 * <p>
 * This class provides methods for user registration, password reset token creation, password reset token validation,
 * and user password change. It leverages entities, DTOs, and repositories for processing, and uses BCrypt encoding for
 * password management.
 * <p>
 * Components used in this class:
 * - SupervisorMapper for mapping SupervisorDTO to entity and vice versa.
 * - SupervisorService for saving supervisor data.
 * - PasswordResetTokenRepository for managing PasswordResetToken entities.
 * - BCryptPasswordEncoder for securely encoding passwords.
 * <p>
 * The class interacts with a frontend URL to help specify the reset link while resetting user passwords.
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    /**
     * Handles the mapping between domain objects and data transfer objects (DTOs)
     * for the Supervisor entity. This component is used as a dependency
     * within the AuthServiceImpl class to facilitate transformations between
     * data models, enabling seamless communication between different layers of the application.
     */
    private final SupervisorMapper supervisorMapper;
    /**
     *
     */
    private final SupervisorService supervisorService;
    /**
     * Repository interface for managing password reset tokens.
     * This field is injected into the class to provide access to persistence
     * operations for {@code PasswordResetToken} entities, such as creating,
     * updating, deleting, or retrieving tokens from the database.
     */
    private final PasswordResetTokenRepository tokenRepository;
    /**
     * The BCryptPasswordEncoder object responsible for encrypting and verifying passwords
     * using the BCrypt hashing algorithm. This is primarily used to secure user
     * passwords by hashing them before saving and verifying them during authentication.
     * <p>
     * This instance is declared as final, ensuring that the same encoder is used
     * throughout the lifecycle of the associated system component or service.
     */
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * The URL of the frontend application, used for constructing links or redirecting
     * users to the client-side application. This property value is injected from
     * the application configuration using the key "app.frontend.url".
     */
    @Value("${app.frontend.url}")
    private String frontendUrl;

    /**
     * Constructs an instance of the AuthServiceImpl class with the required dependencies.
     *
     * @param tokenRepository the repository used for managing password reset tokens
     * @param supervisorMapper the mapper used for converting between SupervisorDTO and other models
     * @param supervisorService the service that provides supervisor-specific operations
     * @param bCryptPasswordEncoder the encoder used for encoding passwords securely
     */
    public AuthServiceImpl(
            PasswordResetTokenRepository tokenRepository,
            SupervisorMapper supervisorMapper,
            SupervisorService supervisorService,
            BCryptPasswordEncoder bCryptPasswordEncoder) {
     
        this.tokenRepository = tokenRepository;
        this.supervisorMapper = supervisorMapper;
        this.supervisorService = supervisorService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /**
     * Registers a new supervisor user by encoding their password and setting a default profile if not provided.
     *
     * @param supervisorDTO the SupervisorDTO object containing the user's details to be registered
     * @return the SupervisorDTO object representing the saved user with updated information
     */
    public SupervisorDTO registerUser(SupervisorDTO supervisorDTO) {
        String mdpCrypter = bCryptPasswordEncoder.encode(supervisorDTO.getPassword());
        supervisorDTO.setPassword(mdpCrypter);
        if (supervisorDTO.getProfile() == null) {
            supervisorDTO.setProfile(SupervisorProfile.SUPERVISOR);
        }
        return supervisorService.save(supervisorDTO);
    }

    /**
     * Creates a password reset token for the specified user and assigns it an expiry date.
     * If a token already exists for the user, it is updated with the new token and expiry date.
     * This method also persists the token to the database.
     *
     * @param userDto the {@code SupervisorDTO} object representing the user for whom the token is to be created
     * @param token the password reset token to be assigned to the user
     */
    public void createPasswordResetTokenForUser(SupervisorDTO userDto, String token) {
        // 1. Recherche du token existant
        PasswordResetToken myToken = tokenRepository.findBySupervisor(supervisorMapper.toEntity(userDto));
        if (myToken == null) {
            myToken = new PasswordResetToken();
        }

        // 2. Mise à jour des valeurs
        myToken.setSupervisor(supervisorMapper.toEntity(userDto));
        myToken.setToken(token);
        myToken.setExpiryDate(calculateExpiryDate()); // 15 min

        // 3. Sauvegarde (insert ou update selon l'état)
        tokenRepository.save(myToken);

        // 4. Envoyer l'email
//        sendResetEmail(userDto, token);
        log.info("Created Password Reset Token: {}", myToken.getToken());
    }

    /**
     * Sends a password reset email to the specified user with a generated reset token.
     * The email includes the reset URL with the token and email details.
     *
     * @param usersDTO the {@code SupervisorDTO} object containing the user's information, including email and name
     * @param token the password reset token to be included in the email for verification
     */
    private void sendResetEmail(SupervisorDTO usersDTO, String token) {
        Map<String, Object>  map = Map.of(
                "email", usersDTO.getEmail(),
                "nom", usersDTO.getLastname(),
                "token", token,
                "resetUrl", frontendUrl + "/auth/reset-password?token=" + token,
                "template", "password-reset",
                "subject", "Réinitialisation du mot de passe"
        );
    }

    /**
     * Validates the password reset token by checking if it exists and has not expired.
     * If the token is invalid or expired, an exception is thrown.
     *
     * @param token the password reset token to be validated
     * @throws UsernameNotFoundException if the token is invalid or has expired
     */
    public void validatePasswordResetToken(String token) {
        PasswordResetToken passToken = tokenRepository.findByToken(token);
        if (passToken == null || passToken.getExpiryDate().before(new Date())) {
            throw new UsernameNotFoundException("Token invalide ou expiré");
        }
    }

    /**
     * Updates the password of the specified supervisor by encoding the provided new password
     * and saving the updated supervisor object.
     *
     * @param supervisor the {@code SupervisorDTO} object representing the supervisor whose password
     *                   is to be updated
     * @param newPassword the new password to be encoded and set for the supervisor
     */
    public void changeUserPassword(SupervisorDTO supervisor, String newPassword) {
        String mdpCrypter = bCryptPasswordEncoder.encode(newPassword);
        supervisor.setPassword(mdpCrypter);
        supervisorService.save(supervisor);
    }

    /**
     * Calculates the expiry date, which is set to 15 minutes from the current time.
     *
     * @return the calculated expiry date as a {@code Date} object
     */
    private Date calculateExpiryDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 15);
        return cal.getTime();
    }
}