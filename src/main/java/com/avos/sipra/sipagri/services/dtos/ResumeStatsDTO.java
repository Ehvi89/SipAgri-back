package com.avos.sipra.sipagri.services.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeStatsDTO {
    private long totalPlanters;
    private long totalPlantations;
    private double totalProduction;
    private double totalRevenue;
    private double plantersGrowth;
    private double productionGrowth;
    private double revenueGrowth;
}