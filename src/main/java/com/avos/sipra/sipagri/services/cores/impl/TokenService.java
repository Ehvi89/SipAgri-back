package com.avos.sipra.sipagri.services.cores.impl;

import com.avos.sipra.sipagri.entities.PasswordResetToken;
import com.avos.sipra.sipagri.repositories.TokenRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    TokenRepository tokenRepository;
    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public PasswordResetToken findByToken(String email) {
        return tokenRepository.findByToken(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
    }

    public void delete(Long id) {
        tokenRepository.deleteById(id);
    }
}
