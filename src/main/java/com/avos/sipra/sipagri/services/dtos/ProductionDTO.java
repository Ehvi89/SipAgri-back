package com.avos.sipra.sipagri.services.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductionDTO {
    private Long id;

    private Long plantationId;

    private Double productionInKg;

    private Double purchasePrice;

    private Boolean mustBePaid;

    private Date year;
}
