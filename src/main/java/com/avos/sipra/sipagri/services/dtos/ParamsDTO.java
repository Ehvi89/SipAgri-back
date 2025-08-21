package com.avos.sipra.sipagri.services.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParamsDTO {
    private Long id;

    private String name;

    private String value;

    private String description;

    private String codeParams;

    private Boolean encrypted;
}
