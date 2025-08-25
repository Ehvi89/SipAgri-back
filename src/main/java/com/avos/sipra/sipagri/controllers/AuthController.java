package com.avos.sipra.sipagri.controllers;

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
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    private SupervisorService supervisorService;

    @Autowired
    private SupervisorMapper supervisorMapper;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthService authService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil) {
            this.authenticationManager = authenticationManager;
            this.jwtUtil = jwtUtil;
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
    public ResponseEntity<SupervisorDTO> createUser(@RequestBody SupervisorDTO usersDTO) {
        SupervisorDTO createdSupervisor = supervisorService.save(usersDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdSupervisor);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody String email) {
        SupervisorDTO usersDTO = supervisorService.findByEmail(email);

        String token = UUID.randomUUID().toString();
        authService.createPasswordResetTokenForUser(usersDTO, token);

        return ResponseEntity.ok()
                .body(Map.of(
                        "status", HttpStatus.OK.value(),
                        "message", "Email de réinitialisation envoyé",
                        "timestamp", LocalDateTime.now()
                ));
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
                            "status", HttpStatus.OK.value(),
                            "message", "Mot de passe mis à jour avec succès",
                            "timestamp", LocalDateTime.now()
                    ));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", 401,
                            "message", "Token invalide ou expiré",
                            "timestamp", LocalDateTime.now()
                    ));
        }
    }

    @RequestMapping("/error")
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        HttpStatus httpStatus = getStatus(request);
        return ResponseEntity.status(httpStatus)
                .body(Map.of(
                        "error", httpStatus.getReasonPhrase(),
                        "message", "An error occurred",
                        "status", httpStatus.value()
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
