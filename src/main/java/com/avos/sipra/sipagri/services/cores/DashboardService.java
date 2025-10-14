package com.avos.sipra.sipagri.services.cores;

import com.avos.sipra.sipagri.services.dtos.*;

import java.util.List;

public interface DashboardService {
    List<ResumeDTO> getResumesData();
    List<ChartDataDTO> getProductionByPeriod(String period);
    List<ChartDataDTO> getProductionByPlantation();
    List<ProductionTrendDTO> getProductionTrend(int months);
    PlanterDetailsDTO getPlanterDetails(Long planterId);
    List<ChartDataDTO> getTopPlanters(int limit);
    List<ChartDataDTO> getTopPlantationsByArea(int limit);
    PaymentStatisticsDTO getPaymentStatistics();
    List<ChartDataDTO> getPlantersByVillage();
    Double getAverageProductionPerHectare();
    Double getAveragePurchasePrice();
    PlanterDemographicsDTO getPlanterDemographics();
}
