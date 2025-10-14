package com.avos.sipra.sipagri.services.mappers;

import com.avos.sipra.sipagri.entities.Production;
import com.avos.sipra.sipagri.entities.Plantation;
import com.avos.sipra.sipagri.services.dtos.ProductionDTO;
import org.springframework.stereotype.Component;

@Component
public class ProductionMapper {

    public ProductionDTO toDTO(Production production) {
        return ProductionDTO.builder()
                .id(production.getId())
                // NE PAS MAPPER LA PLANTATION COMPLÈTE - juste l'ID
                .plantationId(production.getPlantation() != null ? production.getPlantation().getId() : null)
                .productionInKg(production.getProductionInKg())
                .purchasePrice(production.getPurchasePrice())
                .mustBePaid(production.getMustBePaid())
                .year(production.getYear())
                .createdAt(production.getCreatedAt())
                .updatedAt(production.getUpdatedAt())
                .build();
    }

    public Production toEntity(ProductionDTO productionDTO) {
        Production.ProductionBuilder builder = Production.builder()
                .id(productionDTO.getId())
                .productionInKg(productionDTO.getProductionInKg())
                .purchasePrice(productionDTO.getPurchasePrice())
                .mustBePaid(productionDTO.getMustBePaid())
                .updatedAt(productionDTO.getUpdatedAt())
                .createdAt(productionDTO.getCreatedAt())
                .year(productionDTO.getYear());

        // Créer une référence plantation si nécessaire
        if (productionDTO.getPlantationId() != null) {
            Plantation plantation = new Plantation();
            plantation.setId(productionDTO.getPlantationId());
            builder.plantation(plantation);
        }

        return builder.build();
    }

    public Production partialUpdate(Production production, ProductionDTO productionDTO) {
        if (productionDTO.getProductionInKg() != null) {
            production.setProductionInKg(productionDTO.getProductionInKg());
        }
        if (productionDTO.getPurchasePrice() != null) {
            production.setPurchasePrice(productionDTO.getPurchasePrice());
        }
        if (productionDTO.getMustBePaid() != null) {
            production.setMustBePaid(productionDTO.getMustBePaid());
        }
        if (productionDTO.getYear() != null) {
            production.setYear(productionDTO.getYear());
        }
        if (productionDTO.getUpdatedAt() != null) {
            production.setUpdatedAt(productionDTO.getUpdatedAt());
        }
        if (productionDTO.getCreatedAt() != null) {
            production.setCreatedAt(productionDTO.getCreatedAt());
        }
        return production;
    }
}
