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
/**
 * Response payload for successful authentication.
 */
public class LoginResponseDto implements Serializable {
    /** Signed JWT token to be used in Authorization header (Bearer). */
    private String token;
    /** Authenticated supervisor profile. */
    private SupervisorDTO supervisor;
}
