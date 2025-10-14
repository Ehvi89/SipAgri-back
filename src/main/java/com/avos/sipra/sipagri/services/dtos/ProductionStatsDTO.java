package com.avos.sipra.sipagri.services.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionStatsDTO {
    private double totalProductionKg;
    private double averageProductionPerPlantation;
    private double averageProductionPerHectare;
    private double maxProduction;
    private double minProduction;
}