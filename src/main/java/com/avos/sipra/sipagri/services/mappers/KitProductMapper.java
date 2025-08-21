package com.avos.sipra.sipagri.services.mappers;

import com.avos.sipra.sipagri.entities.KitProduct;
import com.avos.sipra.sipagri.services.dtos.KitProductDTO;
import org.springframework.stereotype.Component;

@Component
public class KitProductMapper {

    private final ProductMapper productMapper;

    KitProductMapper(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }
    public KitProduct toEntity(KitProductDTO kitProductDTO) {
        return KitProduct.builder()
                .id(kitProductDTO.getId())
                .product(kitProductDTO.getProduct() != null ? 
                    productMapper.toEntity(kitProductDTO.getProduct()) : null)
                .totalCost(kitProductDTO.getTotalCost())
                .quantity(kitProductDTO.getQuantity())
                .build();
    }

    public KitProductDTO toDTO(KitProduct kitProduct) {
        return KitProductDTO.builder()
                .id(kitProduct.getId())
                .product(kitProduct.getProduct() != null ? 
                    productMapper.toDTO(kitProduct.getProduct()) : null)
                .totalCost(kitProduct.getTotalCost())
                .quantity(kitProduct.getQuantity())
                .build();
    }

    public KitProduct partialUpdate(KitProduct kitProduct, KitProductDTO kitProductDTO) {
        if (kitProductDTO.getProduct() != null) {
            kitProduct.setProduct(productMapper.toEntity(kitProductDTO.getProduct()));
        }
        if (kitProductDTO.getTotalCost() != null) {
            kitProduct.setTotalCost(kitProductDTO.getTotalCost());
        }
        if (kitProductDTO.getQuantity() != null) {
            kitProduct.setQuantity(kitProductDTO.getQuantity());
        }
        return kitProduct;
    }
}
