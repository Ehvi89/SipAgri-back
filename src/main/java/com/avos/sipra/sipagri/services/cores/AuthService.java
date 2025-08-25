package com.avos.sipra.sipagri.services.cores;

import com.avos.sipra.sipagri.services.dtos.SupervisorDTO;

public interface AuthService {
    void createPasswordResetTokenForUser(SupervisorDTO supervisor, String token);

    void validatePasswordResetToken(String token);

    void changeUserPassword(SupervisorDTO supervisor, String newPassword);

    SupervisorDTO registerUser(SupervisorDTO usersDTO);
}
