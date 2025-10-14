package com.avos.sipra.sipagri.services.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private ResumeStatsDTO resumeStats;
    private ProductionStatsDTO productionStats;
    private FinancialStatsDTO financialStats;
    private DemographicStatsDTO demographicStats;
}