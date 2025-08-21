package com.avos.sipra.sipagri.services.mappers;

import com.avos.sipra.sipagri.entities.Supervisor;
import com.avos.sipra.sipagri.services.dtos.SupervisorDTO;
import org.springframework.stereotype.Component;

@Component
public class SupervisorMapper {

    public SupervisorDTO toDTO(Supervisor supervisor) {
        return SupervisorDTO.builder()
                .id(supervisor.getId())
                .firstname(supervisor.getFirstname())
                .lastname(supervisor.getLastname())
                .build();
    }

    public Supervisor toEntity(SupervisorDTO supervisorDTO) {
        return Supervisor.builder()
                .id(supervisorDTO.getId())
                .firstname(supervisorDTO.getFirstname())
                .lastname(supervisorDTO.getLastname())
                .build();
    }

    public Supervisor partialUpdate(Supervisor supervisor, SupervisorDTO supervisorDTO) {
        if (supervisorDTO.getFirstname() != null) {
            supervisor.setFirstname(supervisorDTO.getFirstname());
        }
        if (supervisorDTO.getLastname() != null) {
            supervisor.setLastname(supervisorDTO.getLastname());
        }
        return supervisor;
    }
}
