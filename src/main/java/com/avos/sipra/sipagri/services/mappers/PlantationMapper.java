package com.avos.sipra.sipagri.services.mappers;

import com.avos.sipra.sipagri.entities.Plantation;
import com.avos.sipra.sipagri.entities.Production;
import com.avos.sipra.sipagri.entities.Planter;
import com.avos.sipra.sipagri.enums.PlantationStatus;
import com.avos.sipra.sipagri.services.dtos.PlantationDTO;
import com.avos.sipra.sipagri.services.dtos.ProductionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Component
@RequiredArgsConstructor
public class PlantationMapper {

    private final KitMapper kitMapper;
    private final ProductionMapper productionMapper;

    /**
     * Convertit un PlantationDTO en entité Plantation
     * @param plantationDTO le DTO à convertir
     * @return l'entité Plantation
     */
    public Plantation toEntity(PlantationDTO plantationDTO) {
        if (plantationDTO == null) {
            return null;
        }

        Plantation.PlantationBuilder builder = Plantation.builder()
                .id(plantationDTO.getId())
                .name(plantationDTO.getName())
                .description(plantationDTO.getDescription())
                .gpsLocation(plantationDTO.getGpsLocation())
                .createdAt(plantationDTO.getCreatedAt())
                .updatedAt(plantationDTO.getUpdatedAt())
                .status(plantationDTO.getStatus())
                .sector(plantationDTO.getSector())
                .farmedArea(plantationDTO.getFarmedArea());

        // Mapper le kit si présent
        if (plantationDTO.getKit() != null) {
            builder.kit(kitMapper.toEntity(plantationDTO.getKit()));
        }

        // Créer une référence au planteur si l'ID est fourni
        if (plantationDTO.getPlanterId() != null) {
            Planter planter = new Planter();
            planter.setId(plantationDTO.getPlanterId());
            builder.planter(planter);
        }

        Plantation plantation = builder.build();

        // Gérer les productions avec la relation bidirectionnelle
        if (plantationDTO.getProductions() != null && !plantationDTO.getProductions().isEmpty()) {
            List<Production> productions = plantationDTO.getProductions().stream()
                    .map(productionDTO -> {
                        Production production = productionMapper.toEntity(productionDTO);
                        production.setPlantation(plantation);
                        return production;
                    })
                    .toList();
            plantation.setProductions(productions);
        } else {
            plantation.setProductions(new ArrayList<>());
        }

        return plantation;
    }

    /**
     * Convertit une entité Plantation en PlantationDTO
     * @param plantation l'entité à convertir
     * @return le DTO
     */
    public PlantationDTO toDTO(Plantation plantation) {
        if (plantation == null) {
            return null;
        }

        return PlantationDTO.builder()
                .id(plantation.getId())
                .name(plantation.getName())
                .description(plantation.getDescription())
                .gpsLocation(plantation.getGpsLocation())
                .farmedArea(plantation.getFarmedArea())
                .createdAt(plantation.getCreatedAt())
                .updatedAt(plantation.getUpdatedAt())
                .status(plantation.getStatus())
                .sector(plantation.getSector())
                .productions(plantation.getProductions() != null && !plantation.getProductions().isEmpty() ?
                        plantation.getProductions().stream()
                                .map(productionMapper::toDTO)
                                .toList() : null
                )
                .planterId(plantation.getPlanter() != null ? plantation.getPlanter().getId() : null)
                .kit(plantation.getKit() != null ? kitMapper.toDTO(plantation.getKit()) : null)
                .build();
    }

    /**
     * Met à jour partiellement une entité Plantation avec les données du DTO
     * @param plantation l'entité à mettre à jour
     * @param plantationDTO le DTO contenant les nouvelles données
     * @return l'entité mise à jour
     */
    public Plantation partialUpdate(Plantation plantation, PlantationDTO plantationDTO) {
        if (plantation == null || plantationDTO == null) {
            return plantation;
        }

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
        if (plantationDTO.getSector() != null) {
            plantation.setSector(plantationDTO.getSector());
        }
        if (plantationDTO.getKit() != null) {
            plantation.setKit(kitMapper.toEntity(plantationDTO.getKit()));
            plantation.setStatus(PlantationStatus.ACTIVE);
        }
        if (plantationDTO.getCreatedAt() != null) {
            plantation.setCreatedAt(plantationDTO.getCreatedAt());
        }
        if (plantationDTO.getUpdatedAt() != null) {
            plantation.setUpdatedAt(plantationDTO.getUpdatedAt());
        }
        if (plantationDTO.getPlanterId() != null) {
            Planter planter = new Planter();
            planter.setId(plantationDTO.getPlanterId());
            plantation.setPlanter(planter);
        }
        if (plantationDTO.getProductions() != null) {
            updateProductions(plantation, plantationDTO.getProductions());
            plantation.setStatus(PlantationStatus.INACTIVE);
        }

        return plantation;
    }

    /**
     * Met à jour la liste des productions d'une plantation
     * Ajoute les nouvelles productions et met à jour les existantes
     * @param plantation la plantation à mettre à jour
     * @param productionDTOs la liste des DTOs de production
     */
    private void updateProductions(Plantation plantation, List<ProductionDTO> productionDTOs) {
        if (plantation.getProductions() == null) {
            plantation.setProductions(new ArrayList<>());
        }

        // Récupérer les IDs des productions dans la requête
        Set<Long> requestedIds = productionDTOs.stream()
                .map(ProductionDTO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Supprimer les productions qui ne sont plus dans la requête
        plantation.getProductions().removeIf(p ->
                p.getId() != null && !requestedIds.contains(p.getId())
        );

        // Ajouter ou mettre à jour les productions
        for (ProductionDTO dto : productionDTOs) {
            if (dto.getId() != null) {
                // Mise à jour d'une production existante
                Optional<Production> existingProduction = plantation.getProductions().stream()
                        .filter(p -> p.getId() != null && p.getId().equals(dto.getId()))
                        .findFirst();

                if (existingProduction.isPresent()) {
                    productionMapper.partialUpdate(existingProduction.get(), dto);
                } else {
                    // La production existe en BD, mais pas dans la liste actuelle
                    log.warn("Production avec ID {} introuvable dans la plantation {}",
                            dto.getId(), plantation.getId());
                }
            } else {
                // Nouvelle production
                Production newProduction = productionMapper.toEntity(dto);
                newProduction.setPlantation(plantation);
                plantation.getProductions().add(newProduction);
            }
        }
    }
}