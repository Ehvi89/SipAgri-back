package com.avos.sipra.sipagri.services.cores.impl;

import com.avos.sipra.sipagri.entities.PasswordResetToken;
import com.avos.sipra.sipagri.repositories.TokenRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * The TokenService class provides functionality for managing tokens,
 * including retrieving tokens by email and deleting tokens by their ID.
 * This service depends on a TokenRepository for data access.
 */
@Service
public class TokenService {

    /**
     * A repository for handling persistent storage and retrieval of token data.
     * Used by the TokenService to perform database operations related to tokens.
     */
    TokenRepository tokenRepository;
    /**
     * Constructs a new TokenService with the specified TokenRepository.
     *
     * @param tokenRepository the repository used to manage token persistence and retrieval
     */
    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    /**
     * Retrieves a password reset token associated with the specified email.
     *
     * @param email the email address associated with the password reset token
     * @return the {@code PasswordResetToken} if found
     * @throws UsernameNotFoundException if no token is found for the given email
     */
    public PasswordResetToken findByToken(String email) {
        return tokenRepository.findByToken(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
    }

    /**
     * Deletes the token associated with the specified ID.
     *
     * @param id The ID of the token to be deleted.
     */
    public void delete(Long id) {
        tokenRepository.deleteById(id);
    }
}
