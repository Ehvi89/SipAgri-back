package com.avos.sipra.sipagri.services.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto implements Serializable {
    private String token;
    private SupervisorDTO supervisor;
}
