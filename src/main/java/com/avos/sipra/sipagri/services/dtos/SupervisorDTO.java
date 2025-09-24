package com.avos.sipra.sipagri.services.dtos;

import com.avos.sipra.sipagri.enums.SupervisorProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupervisorDTO {
    private Long id;

    private String firstname;

    private String lastname;

    private String email;

    private String password;

    private SupervisorProfile profile;
}
