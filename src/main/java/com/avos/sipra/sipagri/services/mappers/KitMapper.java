package com.avos.sipra.sipagri.services.mappers;

import com.avos.sipra.sipagri.entities.Kit;
import com.avos.sipra.sipagri.services.dtos.KitDTO;
import org.springframework.stereotype.Component;

@Component
public class KitMapper {

    private final KitProductMapper kitProductMapper;

    KitMapper(KitProductMapper kitProductMapper) {
        this.kitProductMapper = kitProductMapper;
    }
    public KitDTO toDTO(Kit kit) {
        return KitDTO.builder()
                .id(kit.getId())
                .name(kit.getName())
                .description(kit.getDescription())
                .totalCost(kit.getTotalCost())
                .kitProducts(kit.getKitProducts() != null ? 
                    kit.getKitProducts().stream()
                        .map(kitProductMapper::toDTO)
                        .toList() : 
                    null)
                .build();
    }

    public Kit toEntity(KitDTO kitDTO) {
        return Kit.builder()
                .id(kitDTO.getId())
                .name(kitDTO.getName())
                .description(kitDTO.getDescription())
                .totalCost(kitDTO.getTotalCost())
                .kitProducts(kitDTO.getKitProducts() != null ?
                    kitDTO.getKitProducts().stream()
                        .map(kitProductMapper::toEntity)
                        .toList() :
                    null)
                .build();
    }

    public Kit partialUpdate(Kit kit, KitDTO kitDTO) {
        if (kitDTO.getName() != null) {
            kit.setName(kitDTO.getName());
        }
        if (kitDTO.getDescription() != null) {
            kit.setDescription(kitDTO.getDescription());
        }
        if (kitDTO.getTotalCost() != null) {
            kit.setTotalCost(kitDTO.getTotalCost());
        }
        if (kitDTO.getKitProducts() != null) {
            kit.setKitProducts(kitDTO.getKitProducts().stream()
                .map(kitProductMapper::toEntity)
                .toList());
        }
        return kit;
    }
}
