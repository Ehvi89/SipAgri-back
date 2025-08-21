package com.avos.sipra.sipagri.services.mappers;

import com.avos.sipra.sipagri.entities.Params;
import com.avos.sipra.sipagri.services.dtos.ParamsDTO;
import org.springframework.stereotype.Component;

@Component
public class ParamsMapper {
    public Params toEntity(ParamsDTO params) {
        return Params.builder()
                .id(params.getId())
                .name(params.getName())
                .description(params.getDescription())
                .value(params.getValue())
                .encrypted(params.getEncrypted())
                .codeParams(params.getCodeParams())
                .build();
    }

    public ParamsDTO toDTO(Params params) {
        return ParamsDTO.builder()
                .id(params.getId())
                .name(params.getName())
                .description(params.getDescription())
                .value(params.getValue())
                .encrypted(params.getEncrypted())
                .codeParams(params.getCodeParams())
                .build();
    }

    public Params partialUpdate(Params params, ParamsDTO paramsDTO) {
        if (paramsDTO.getName() != null) {
            params.setName(paramsDTO.getName());
        }
        if (paramsDTO.getDescription() != null) {
            params.setDescription(paramsDTO.getDescription());
        }
        if (paramsDTO.getValue() != null) {
            params.setValue(paramsDTO.getValue());
        }
        if (paramsDTO.getEncrypted() != null) {
            params.setEncrypted(paramsDTO.getEncrypted());
        }
        if (paramsDTO.getCodeParams() != null) {
            params.setCodeParams(paramsDTO.getCodeParams());
        }
        return params;
    }
}
