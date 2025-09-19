package com.avos.sipra.sipagri.controllers;

import com.avos.sipra.sipagri.annotations.XSSProtected;
import com.avos.sipra.sipagri.entities.PasswordResetToken;
import com.avos.sipra.sipagri.security.JwtUtil;
import com.avos.sipra.sipagri.services.cores.AuthService;
import com.avos.sipra.sipagri.services.cores.SupervisorService;
import com.avos.sipra.sipagri.services.cores.impl.TokenService;
import com.avos.sipra.sipagri.services.dtos.LoginRequestDto;
import com.avos.sipra.sipagri.services.dtos.LoginResponseDto;
import com.avos.sipra.sipagri.services.dtos.PasswordResetDto;
import com.avos.sipra.sipagri.services.dtos.SupervisorDTO;
import com.avos.sipra.sipagri.services.mappers.SupervisorMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    private final SupervisorService supervisorService;
    private final SupervisorMapper supervisorMapper;
    private final TokenService tokenService;
    private final AuthService authService;
    
    private static final String MESSAGE = "message";
    private static final String STATUS = "status";
    private static final String TIMESTAMP = "timestamp";

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          SupervisorService supervisorService,
                          SupervisorMapper supervisorMapper,
                          TokenService tokenService,
                          AuthService authService) {
            this.authenticationManager = authenticationManager;
            this.jwtUtil = jwtUtil;
            this.supervisorService = supervisorService;
            this.supervisorMapper = supervisorMapper;
            this.tokenService = tokenService;
            this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        String token = jwtUtil.generateToken(loginRequest.getEmail());
        if (authentication.isAuthenticated()) {
            SupervisorDTO supervisor = supervisorService.findByEmail(loginRequest.getEmail());
            return ResponseEntity.ok(new LoginResponseDto(token, supervisor));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @PostMapping("/register")
    @XSSProtected
    public ResponseEntity<SupervisorDTO> createUser(@RequestBody SupervisorDTO usersDTO) {
        SupervisorDTO supervisor = supervisorService.findByEmail(usersDTO.getEmail());
        if (supervisor == null) {
            SupervisorDTO createdSupervisor = authService.registerUser(usersDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSupervisor);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/forgot-password")
    @XSSProtected
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody String email) {
        SupervisorDTO usersDTO = supervisorService.findByEmail(email);

        if (usersDTO != null) {
            String token = UUID.randomUUID().toString();
            authService.createPasswordResetTokenForUser(usersDTO, token);

            return ResponseEntity.ok()
                    .body(Map.of(
                            STATUS, HttpStatus.OK.value(),
                            MESSAGE, "Email de réinitialisation envoyé",
                            TIMESTAMP, LocalDateTime.now()
                    ));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody PasswordResetDto dto) {
        try {
            authService.validatePasswordResetToken(dto.getToken());

            PasswordResetToken token = tokenService.findByToken(dto.getToken());
            SupervisorDTO usersDTO = supervisorMapper.toDTO(token.getSupervisor());

            authService.changeUserPassword(usersDTO, dto.getNewPassword());
            tokenService.delete(token.getId());

            return ResponseEntity.ok()
                    .body(Map.of(
                            STATUS, HttpStatus.OK.value(),
                            MESSAGE, "Mot de passe mis à jour avec succès",
                            TIMESTAMP, LocalDateTime.now()
                    ));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            STATUS, 401,
                            MESSAGE, "Token invalide ou expiré",
                            TIMESTAMP, LocalDateTime.now()
                    ));
        }
    }

    @PostMapping("/email-exist")
    public ResponseEntity<SupervisorDTO> checkEmailExist(@RequestBody String email) {
        SupervisorDTO supervisor = supervisorService.findByEmail(email);
        if (supervisor != null) {
            return ResponseEntity.ok(supervisor);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @RequestMapping("/error")
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        HttpStatus httpStatus = getStatus(request);
        return ResponseEntity.status(httpStatus)
                .body(Map.of(
                        "error", httpStatus.getReasonPhrase(),
                        MESSAGE, "An error occurred",
                        STATUS, httpStatus.value()
                ));
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}
