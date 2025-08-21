package com.avos.sipra.sipagri.services.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KitProductDTO {
    private Long id;

    private ProductDTO product;

    private Double totalCost;

    private Integer quantity;
}
