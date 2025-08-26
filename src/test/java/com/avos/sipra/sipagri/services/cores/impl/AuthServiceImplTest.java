package com.avos.sipra.sipagri.services.cores.impl;

import com.avos.sipra.sipagri.entities.PasswordResetToken;
import com.avos.sipra.sipagri.entities.Supervisor;
import com.avos.sipra.sipagri.repositories.PasswordResetTokenRepository;
import com.avos.sipra.sipagri.services.cores.SupervisorService;
import com.avos.sipra.sipagri.services.dtos.SupervisorDTO;
import com.avos.sipra.sipagri.services.mappers.SupervisorMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires pour AuthServiceImpl")
class AuthServiceImplTest {

    @Mock
    private SupervisorMapper supervisorMapper;

    @Mock
    private SupervisorService supervisorService;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private SupervisorDTO supervisorDTO;
    private Supervisor supervisor;
    private PasswordResetToken passwordResetToken;

    @BeforeEach
    void setUp() {
        // Configuration du frontend URL via réflexion
        ReflectionTestUtils.setField(authService, "frontendUrl", "http://localhost:3000");

        // Données de test
        supervisorDTO = SupervisorDTO.builder()
                .id(1L)
                .email("test@example.com")
                .lastname("Doe")
                .firstname("John")
                .password("plainPassword")
                .build();

        supervisor = new Supervisor();
        supervisor.setId(1L);
        supervisor.setEmail("test@example.com");
        supervisor.setLastname("Doe");

        passwordResetToken = new PasswordResetToken();
        passwordResetToken.setId(1L);
        passwordResetToken.setSupervisor(supervisor);
        passwordResetToken.setToken("test-token");
        passwordResetToken.setExpiryDate(new Date(System.currentTimeMillis() + 900000)); // +15 min
    }

    @Test
    @DisplayName("Doit enregistrer un utilisateur avec mot de passe crypté")
    void registerUser_ShouldEncryptPasswordAndSaveUser() {
        // Given
        String encryptedPassword = "encryptedPassword";
        when(bCryptPasswordEncoder.encode("plainPassword")).thenReturn(encryptedPassword);
        when(supervisorService.save(any(SupervisorDTO.class))).thenReturn(supervisorDTO);

        // When
        SupervisorDTO result = authService.registerUser(supervisorDTO);

        // Then
        verify(bCryptPasswordEncoder).encode("plainPassword");
        verify(supervisorService).save(supervisorDTO);
        assertEquals(encryptedPassword, supervisorDTO.getPassword());
        assertNotNull(result);
    }

    @Test
    @DisplayName("Doit créer un nouveau token de réinitialisation quand aucun n'existe")
    void createPasswordResetTokenForUser_WhenNoExistingToken_ShouldCreateNew() {
        // Given
        String token = "new-reset-token";
        when(supervisorMapper.toEntity(supervisorDTO)).thenReturn(supervisor);
        when(tokenRepository.findBySupervisor(supervisor)).thenReturn(null);

        // When
        authService.createPasswordResetTokenForUser(supervisorDTO, token);

        // Then
        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepository).save(tokenCaptor.capture());

        PasswordResetToken savedToken = tokenCaptor.getValue();
        assertEquals(supervisor, savedToken.getSupervisor());
        assertEquals(token, savedToken.getToken());
        assertNotNull(savedToken.getExpiryDate());

        // Vérifier que la date d'expiration est dans environ 15 minutes
        long timeDiff = savedToken.getExpiryDate().getTime() - System.currentTimeMillis();
        assertTrue(timeDiff > 800000 && timeDiff < 1000000); // Entre 13 et 17 minutes
    }

    @Test
    @DisplayName("Doit mettre à jour un token existant")
    void createPasswordResetTokenForUser_WhenExistingToken_ShouldUpdateExisting() {
        // Given
        String newToken = "updated-reset-token";
        PasswordResetToken existingToken = new PasswordResetToken();
        existingToken.setId(1L);
        existingToken.setSupervisor(supervisor);
        existingToken.setToken("old-token");

        when(supervisorMapper.toEntity(supervisorDTO)).thenReturn(supervisor);
        when(tokenRepository.findBySupervisor(supervisor)).thenReturn(existingToken);

        // When
        authService.createPasswordResetTokenForUser(supervisorDTO, newToken);

        // Then
        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepository).save(tokenCaptor.capture());

        PasswordResetToken savedToken = tokenCaptor.getValue();
        assertEquals(1L, savedToken.getId());
        assertEquals(supervisor, savedToken.getSupervisor());
        assertEquals(newToken, savedToken.getToken());
        assertNotNull(savedToken.getExpiryDate());
    }

    @Test
    @DisplayName("Doit valider un token valide et non expiré")
    void validatePasswordResetToken_WhenValidToken_ShouldPass() {
        // Given
        String token = "valid-token";
        Date futureDate = new Date(System.currentTimeMillis() + 900000); // +15 min
        passwordResetToken.setExpiryDate(futureDate);
        when(tokenRepository.findByToken(token)).thenReturn(passwordResetToken);

        // When & Then
        assertDoesNotThrow(() -> authService.validatePasswordResetToken(token));
        verify(tokenRepository).findByToken(token);
    }

    @Test
    @DisplayName("Doit lever une exception pour un token inexistant")
    void validatePasswordResetToken_WhenTokenNotFound_ShouldThrowException() {
        // Given
        String token = "non-existent-token";
        when(tokenRepository.findByToken(token)).thenReturn(null);

        // When & Then
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> authService.validatePasswordResetToken(token)
        );

        assertEquals("Token invalide ou expiré", exception.getMessage());
        verify(tokenRepository).findByToken(token);
    }

    @Test
    @DisplayName("Doit lever une exception pour un token expiré")
    void validatePasswordResetToken_WhenTokenExpired_ShouldThrowException() {
        // Given
        String token = "expired-token";
        Date pastDate = new Date(System.currentTimeMillis() - 900000); // -15 min
        passwordResetToken.setExpiryDate(pastDate);
        when(tokenRepository.findByToken(token)).thenReturn(passwordResetToken);

        // When & Then
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> authService.validatePasswordResetToken(token)
        );

        assertEquals("Token invalide ou expiré", exception.getMessage());
        verify(tokenRepository).findByToken(token);
    }

    @Test
    @DisplayName("Doit changer le mot de passe utilisateur avec cryptage")
    void changeUserPassword_ShouldEncryptAndSaveNewPassword() {
        // Given
        String newPassword = "newPassword123";
        String encryptedPassword = "encryptedNewPassword";
        when(bCryptPasswordEncoder.encode(newPassword)).thenReturn(encryptedPassword);
        when(supervisorService.save(any(SupervisorDTO.class))).thenReturn(supervisorDTO);

        // When
        authService.changeUserPassword(supervisorDTO, newPassword);

        // Then
        verify(bCryptPasswordEncoder).encode(newPassword);
        verify(supervisorService).save(supervisorDTO);
        assertEquals(encryptedPassword, supervisorDTO.getPassword());
    }

    @Test
    @DisplayName("Doit calculer une date d'expiration de 15 minutes")
    void calculateExpiryDate_ShouldReturn15MinutesFromNow() {
        // Given
        long beforeCalculation = System.currentTimeMillis();

        // When
        authService.createPasswordResetTokenForUser(supervisorDTO, "test-token");

        // Then
        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepository).save(tokenCaptor.capture());

        PasswordResetToken savedToken = tokenCaptor.getValue();
        long afterCalculation = System.currentTimeMillis();

        // Vérifier que la date d'expiration est d'environ 15 minutes (900000 ms)
        long expiryTime = savedToken.getExpiryDate().getTime();
        long expectedMinTime = beforeCalculation + 900000; // +15 min
        long expectedMaxTime = afterCalculation + 900000; // +15 min

        assertTrue(expiryTime >= expectedMinTime && expiryTime <= expectedMaxTime,
                "La date d'expiration devrait être à 15 minutes de maintenant");
    }

    @Test
    @DisplayName("Doit gérer le cas où le supervisorMapper retourne null")
    void createPasswordResetTokenForUser_WhenMapperReturnsNull_ShouldHandleGracefully() {
        // Given
        String token = "test-token";
        when(supervisorMapper.toEntity(supervisorDTO)).thenReturn(null);
        when(tokenRepository.findBySupervisor(null)).thenReturn(null);

        // When
        authService.createPasswordResetTokenForUser(supervisorDTO, token);

        // Then
        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepository).save(tokenCaptor.capture());

        PasswordResetToken savedToken = tokenCaptor.getValue();
        assertNull(savedToken.getSupervisor());
        assertEquals(token, savedToken.getToken());
    }

    @Test
    @DisplayName("Doit valider un token exactement à la limite d'expiration")
    void validatePasswordResetToken_WhenTokenExpiresNow_ShouldThrowException() {
        // Given
        String token = "expiring-now-token";
        Date nowDate = new Date(); // Exactement maintenant
        passwordResetToken.setExpiryDate(nowDate);
        when(tokenRepository.findByToken(token)).thenReturn(passwordResetToken);

        // When & Then - Le token qui expire maintenant devrait être considéré comme expiré
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> authService.validatePasswordResetToken(token)
        );

        assertEquals("Token invalide ou expiré", exception.getMessage());
    }
}