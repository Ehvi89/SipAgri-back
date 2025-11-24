package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.PasswordResetToken;
import com.avos.sipra.sipagri.entities.Supervisor;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link PasswordResetToken} entities.
 * <p>
 * This interface extends {@link JpaRepository}, providing default methods for
 * basic CRUD operations and additional methods for retrieving specific entities
 * based on custom criteria.
 */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    /**
     * Retrieves a {@link PasswordResetToken} entity based on the provided token string.
     *
     * @param token the token string used to identify the specific {@link PasswordResetToken} entity
     * @return the {@link PasswordResetToken} entity associated with the given token,
     *         or null if no matching entity is found
     */
    PasswordResetToken findByToken(String token);

    /**
     * Retrieves a {@link PasswordResetToken} entity associated with the given supervisor.
     *
     * @param user_id the supervisor whose associated password reset token is to be retrieved
     * @return the password reset token associated with the specified supervisor, or null if no token is found
     */
    PasswordResetToken findBySupervisor(Supervisor user_id);
}