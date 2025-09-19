package com.avos.sipra.sipagri.services.mappers;

import com.avos.sipra.sipagri.entities.Plantation;
import com.avos.sipra.sipagri.entities.Production;
import com.avos.sipra.sipagri.services.dtos.PlantationDTO;
import com.avos.sipra.sipagri.services.dtos.ProductionDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Component
public class PlantationMapper {

    private final KitMapper kitMapper;

    private final ProductionMapper productionMapper;

    PlantationMapper(KitMapper kitMapper, ProductionMapper productionMapper) {
        this.kitMapper = kitMapper;
        this.productionMapper = productionMapper;
    }

    public Plantation toEntity(PlantationDTO plantationDTO) {
        Plantation plantation = Plantation.builder()
                .id(plantationDTO.getId())
                .name(plantationDTO.getName())
                .description(plantationDTO.getDescription())
                .gpsLocation(plantationDTO.getGpsLocation())
                .farmedArea(plantationDTO.getFarmedArea())
                .planterId(plantationDTO.getPlanterId())
                .kit(kitMapper.toEntity(plantationDTO.getKit()))
                .build();

        // Gérer les productions séparément
        if (plantationDTO.getProductions() != null) {
            List<Production> productions = plantationDTO.getProductions().stream()
                    .map(productionDTO -> {
                        Production production = productionMapper.toEntity(productionDTO);
                        production.setPlantation(plantation); // Définir la relation parent
                        return production;
                    })
                    .collect(Collectors.toCollection(ArrayList::new));
            plantation.setProductions(productions);
        }

        return plantation;
    }

    public PlantationDTO toDTO(Plantation plantation) {
        return PlantationDTO.builder()
                .id(plantation.getId())
                .name(plantation.getName())
                .description(plantation.getDescription())
                .gpsLocation(plantation.getGpsLocation())
                .farmedArea(plantation.getFarmedArea())
                // Cette ligne ne causera plus de boucle car ProductionMapper ne mappe plus plantation
                .productions(plantation.getProductions() != null ?
                        plantation.getProductions().stream()
                                .map(productionMapper::toDTO)
                                .collect(Collectors.toCollection(ArrayList::new)) : null
                )
                .planterId(plantation.getPlanterId())
                .kit(kitMapper.toDTO(plantation.getKit()))
                .build();
    }

    public Plantation partialUpdate(Plantation plantation, PlantationDTO plantationDTO) {
        if (plantationDTO.getName() != null) {
            plantation.setName(plantationDTO.getName());
        }
        if (plantationDTO.getDescription() != null) {
            plantation.setDescription(plantationDTO.getDescription());
        }
        if (plantationDTO.getGpsLocation() != null) {
            plantation.setGpsLocation(plantationDTO.getGpsLocation());
        }
        if (plantationDTO.getFarmedArea() != null) {
            plantation.setFarmedArea(plantationDTO.getFarmedArea());
        }
        if (plantationDTO.getProductions() != null) {
            updateProductions(plantation, plantationDTO.getProductions());
        }
        if (plantationDTO.getPlanterId() != null) {
            plantation.setPlanterId(plantationDTO.getPlanterId());
        }
        if (plantationDTO.getKit() != null) {
            plantation.setKit(kitMapper.toEntity(plantationDTO.getKit()));
        }
        return plantation;
    }

    private void updateProductions(Plantation plantation, List<ProductionDTO> productionDTOs) {
        // IDs des productions dans la requête
        Set<Long> requestedProductionIds = productionDTOs.stream()
                .map(ProductionDTO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (ProductionDTO dto : productionDTOs) {
            if (dto.getId() != null) {
                // Mise à jour d'une production existante
                plantation.getProductions().stream()
                        .filter(p -> p.getId().equals(dto.getId()))
                        .findFirst()
                        .ifPresent(existing -> productionMapper.partialUpdate(existing, dto));
            } else {
                // Nouvelle production
                Production newProduction = productionMapper.toEntity(dto);
                newProduction.setPlantation(plantation);
                plantation.getProductions().add(newProduction);
            }
        }
    }
}
