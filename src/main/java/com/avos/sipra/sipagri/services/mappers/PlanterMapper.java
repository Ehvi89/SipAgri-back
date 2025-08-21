package com.avos.sipra.sipagri.services.mappers;

import com.avos.sipra.sipagri.entities.Planter;
import com.avos.sipra.sipagri.services.dtos.PlanterDTO;
import org.springframework.stereotype.Component;

@Component
public class PlanterMapper {

    private final SupervisorMapper supervisorMapper;

    private final PlantationMapper plantationMapper;

    PlanterMapper(PlantationMapper plantationMapper, SupervisorMapper supervisorMapper) {
        this.plantationMapper = plantationMapper;
        this.supervisorMapper = supervisorMapper;
    }
    public PlanterDTO toDTO(Planter planter) {
        return PlanterDTO.builder()
                .id(planter.getId())
                .firstname(planter.getFirstname())
                .lastname(planter.getLastname())
                .birthday(planter.getBirthday())
                .childrenNumber(planter.getChildrenNumber())
                .maritalStatus(planter.getMaritalStatus())
                .gender(planter.getGender())
                .village(planter.getVillage())
                .supervisor(supervisorMapper.toDTO(planter.getSupervisor()))
                .plantations(planter.getPlantations() != null ? 
                    planter.getPlantations().stream()
                        .map(plantationMapper::toDTO)
                        .toList() :
                    null
                )
                .build();
    }

    public Planter toEntity(PlanterDTO planterDTO) {
        return Planter.builder()
                .id(planterDTO.getId())
                .firstname(planterDTO.getFirstname())
                .lastname(planterDTO.getLastname())
                .birthday(planterDTO.getBirthday())
                .childrenNumber(planterDTO.getChildrenNumber())
                .maritalStatus(planterDTO.getMaritalStatus())
                .gender(planterDTO.getGender())
                .village(planterDTO.getVillage())
                .supervisor(supervisorMapper.toEntity(planterDTO.getSupervisor()))
                .plantations(planterDTO.getPlantations() != null ?
                    planterDTO.getPlantations().stream()
                        .map(plantationMapper::toEntity)
                        .toList() :
                    null
                )
                .build();
    }

    public Planter partialUpdate(Planter planter, PlanterDTO planterDTO) {
        if (planterDTO.getFirstname() != null) {
            planter.setFirstname(planterDTO.getFirstname());
        }
        if (planterDTO.getLastname() != null) {
            planter.setLastname(planterDTO.getLastname());
        }
        if (planterDTO.getBirthday() != null) {
            planter.setBirthday(planterDTO.getBirthday());
        }
        if (planterDTO.getChildrenNumber() != null) {
            planter.setChildrenNumber(planterDTO.getChildrenNumber());
        }
        if (planterDTO.getMaritalStatus() != null) {
            planter.setMaritalStatus(planterDTO.getMaritalStatus());
        }
        if (planterDTO.getGender() != null) {
            planter.setGender(planterDTO.getGender());
        }
        if (planterDTO.getVillage() != null) {
            planter.setVillage(planterDTO.getVillage());
        }
        if (planterDTO.getSupervisor() != null) {
            planter.setSupervisor(supervisorMapper.toEntity(planterDTO.getSupervisor()));
        }
        if (planterDTO.getPlantations() != null) {
            planter.setPlantations(planterDTO.getPlantations().stream()
                .map(plantationMapper::toEntity)
                .toList());
        }
        return planter;
    }
}
