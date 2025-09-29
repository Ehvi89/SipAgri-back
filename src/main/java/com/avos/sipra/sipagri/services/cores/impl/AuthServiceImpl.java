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

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final SupervisorMapper supervisorMapper;
    private final SupervisorService supervisorService;
    private final PasswordResetTokenRepository tokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${app.frontend.url}")
    private String frontendUrl;

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

    public SupervisorDTO registerUser(SupervisorDTO supervisorDTO) {
        String mdpCrypter = bCryptPasswordEncoder.encode(supervisorDTO.getPassword());
        supervisorDTO.setPassword(mdpCrypter);
        if (supervisorDTO.getProfile() == null) {
            supervisorDTO.setProfile(SupervisorProfile.SUPERVISOR);
        }
        return supervisorService.save(supervisorDTO);
    }

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

    public void validatePasswordResetToken(String token) {
        PasswordResetToken passToken = tokenRepository.findByToken(token);
        if (passToken == null || passToken.getExpiryDate().before(new Date())) {
            throw new UsernameNotFoundException("Token invalide ou expiré");
        }
    }

    public void changeUserPassword(SupervisorDTO supervisor, String newPassword) {
        String mdpCrypter = bCryptPasswordEncoder.encode(newPassword);
        supervisor.setPassword(mdpCrypter);
        supervisorService.save(supervisor);
    }

    private Date calculateExpiryDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 15);
        return cal.getTime();
    }
}