package com.avos.sipra.sipagri.services.dtos;

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
}
