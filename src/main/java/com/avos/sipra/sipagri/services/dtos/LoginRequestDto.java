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
 * Request payload for authentication.
 */
public class LoginRequestDto implements Serializable {
    /** User email (also used as username). */
    private String email;
    /** Plaintext password to authenticate. */
    private String password;
}
