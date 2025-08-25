package com.avos.sipra.sipagri.services.dtos;

import lombok.Data;

@Data
public class PasswordResetDto {
    private String token;
    private String newPassword;
}
