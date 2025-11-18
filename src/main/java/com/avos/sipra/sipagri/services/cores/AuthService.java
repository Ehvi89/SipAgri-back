package com.avos.sipra.sipagri.services.cores;

import com.avos.sipra.sipagri.services.dtos.SupervisorDTO;

/**
 * Authentication and password management contract.
 * <p>
 * Implementations are responsible for issuing and validating password reset tokens,
 * creating new supervisor accounts and updating passwords securely.
 * </p>
 */
public interface AuthService {
    /**
     * Create and persist a password reset token for the given supervisor.
     *
     * @param supervisor target supervisor DTO
     * @param token      generated token value
     */
    void createPasswordResetTokenForUser(SupervisorDTO supervisor, String token);

    /**
     * Validate that a reset token exists and is not expired; otherwise throw a security exception.
     *
     * @param token reset token to validate
     * @throws SecurityException if token is invalid or expired
     */
    void validatePasswordResetToken(String token);

    /**
     * Change the user's password to the provided value, applying necessary hashing.
     *
     * @param supervisor  the supervisor whose password will be changed
     * @param newPassword plaintext new password
     */
    void changeUserPassword(SupervisorDTO supervisor, String newPassword);

    /**
     * Register a new supervisor account.
     *
     * @param usersDTO the supervisor payload
     * @return the created supervisor with generated identifiers
     */
    SupervisorDTO registerUser(SupervisorDTO usersDTO);
}
