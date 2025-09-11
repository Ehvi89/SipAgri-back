package com.avos.sipra.sipagri.services.mappers;

import com.avos.sipra.sipagri.entities.Production;
import com.avos.sipra.sipagri.services.dtos.ProductionDTO;
import org.springframework.stereotype.Component;

@Component
public class ProductionMapper {

    public ProductionDTO toDTO(Production production) {
        return ProductionDTO.builder()
                .id(production.getId())
                .plantationId(production.getPlantationId())
                .productionInKg(production.getProductionInKg())
                .purchasePrice(production.getPurchasePrice())
                .mustBePaid(production.getMustBePaid())
                .year(production.getYear())
                .build();
    }

    public Production toEntity(ProductionDTO productionDTO) {
        return Production.builder()
                .id(productionDTO.getId())
                .plantationId(productionDTO.getPlantationId())
                .productionInKg(productionDTO.getProductionInKg())
                .purchasePrice(productionDTO.getPurchasePrice())
                .mustBePaid(productionDTO.getMustBePaid())
                .year(productionDTO.getYear())
                .build();
    }

    public Production partialUpdate(Production production, ProductionDTO productionDTO) {
        if (productionDTO.getProductionInKg() != null) {
            production.setProductionInKg(productionDTO.getProductionInKg());
        }
        if (productionDTO.getPlantationId() != null) {
            production.setPlantationId(productionDTO.getPlantationId());
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
        return production;
    }
}
