package com.avos.sipra.sipagri.services.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KitDTO {
    private Long id;

    private String name;

    private String description;

    private Double totalCost;

    private List<KitProductDTO> kitProducts;
}
