package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


/**
 * Repository interface for managing {@link PasswordResetToken} entities and
 * performing operations such as token lookup.
 * <p>
 * This interface extends {@link JpaRepository}, providing default CRUD
 * methods for data access, as well as a custom query method for retrieving
 * tokens based on specific criteria.
 */
public interface TokenRepository extends JpaRepository<PasswordResetToken, Long>{
    /**
     * Retrieves an optional {@link PasswordResetToken} entity based on the provided token value.
     *
     * @param token the token string to search for
     * @return an {@link Optional} containing the {@link PasswordResetToken} entity if found, or an empty {@link Optional} if not found
     */
    Optional<PasswordResetToken> findByToken(String token);

}
