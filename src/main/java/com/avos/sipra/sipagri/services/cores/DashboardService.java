package com.avos.sipra.sipagri.services.cores;

import com.avos.sipra.sipagri.services.dtos.*;

import java.util.List;

public interface DashboardService {

    /**
     * Récupère les statistiques de résumé du dashboard
     */
    List<ResumeDTO> getResumesData();

    /**
     * ✨ NOUVEAU : Production par secteur avec filtre année
     */
    List<ChartDataDTO> getProductionBySector(Integer year);

    /**
     * ✨ NOUVEAU : Liste des années disponibles
     */
    List<Integer> getAvailableYears();

    /**
     * Récupère les données de production groupées par période
     */
    List<ChartDataDTO> getProductionByPeriod(String period);

    /**
     * Récupère la répartition de production par plantation
     */
    List<ChartDataDTO> getProductionByPlantation();

    /**
     * ✨ MODIFIÉ : Récupère la tendance de production par année (plus de limite de mois)
     */
    List<ProductionTrendDTO> getProductionTrend();

    /**
     * Récupère les détails d'un planteur
     */
    PlanterDetailsDTO getPlanterDetails(Long planterId);

    /**
     * Récupère les top planteurs
     */
    List<ChartDataDTO> getTopPlanters(int limit);

    /**
     * Récupère les top plantations par surface
     */
    List<ChartDataDTO> getTopPlantationsByArea(int limit);

    /**
     * Récupère les statistiques de paiement
     */
    PaymentStatisticsDTO getPaymentStatistics();

    /**
     * Récupère la répartition des planteurs par village
     */
    List<ChartDataDTO> getPlantersByVillage();

    /**
     * Récupère la production moyenne par hectare
     */
    Double getAverageProductionPerHectare();

    /**
     * Récupère le prix d'achat moyen
     */
    Double getAveragePurchasePrice();

    /**
     * Récupère les statistiques démographiques
     */
    PlanterDemographicsDTO getPlanterDemographics();
}