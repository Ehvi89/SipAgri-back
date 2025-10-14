package com.avos.sipra.sipagri.controllers;

import com.avos.sipra.sipagri.services.dtos.*;
import com.avos.sipra.sipagri.services.cores.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Récupère les statistiques de résumé du dashboard
     */
    @GetMapping("/resumes")
    public ResponseEntity<ApiResponse<List<ResumeDTO>>> getResumesData() {
        List<ResumeDTO> resumes = dashboardService.getResumesData();
        return ResponseEntity.ok(new ApiResponse<>(true, resumes, "Statistiques récupérées avec succès"));
    }

    /**
     * Récupère les données de production groupées par période
     * @param period - week, month, quarter, year
     */
    @GetMapping("/production-by-period")
    public ResponseEntity<ApiResponse<List<ChartDataDTO>>> getProductionByPeriod(
            @RequestParam(defaultValue = "month") String period) {
        List<ChartDataDTO> data = dashboardService.getProductionByPeriod(period);
        return ResponseEntity.ok(new ApiResponse<>(true, data, "Production par période récupérée"));
    }

    /**
     * Récupère la répartition de production par plantation
     */
    @GetMapping("/production-by-plantation")
    public ResponseEntity<ApiResponse<List<ChartDataDTO>>> getProductionByPlantation() {
        List<ChartDataDTO> data = dashboardService.getProductionByPlantation();
        return ResponseEntity.ok(new ApiResponse<>(true, data, "Production par plantation récupérée"));
    }

    /**
     * Récupère les données de tendance de production dans le temps
     * @param months - Nombre de mois à afficher
     */
    @GetMapping("/production-trend")
    public ResponseEntity<ApiResponse<List<ProductionTrendDTO>>> getProductionTrend(
            @RequestParam(defaultValue = "12") int months) {
        List<ProductionTrendDTO> data = dashboardService.getProductionTrend(months);
        return ResponseEntity.ok(new ApiResponse<>(true, data, "Tendance de production récupérée"));
    }

    /**
     * Récupère les détails d'un planteur avec ses plantations et productions
     */
    @GetMapping("/planter/{planterId}")
    public ResponseEntity<ApiResponse<PlanterDetailsDTO>> getPlanterDetails(
            @PathVariable Long planterId) {
        PlanterDetailsDTO details = dashboardService.getPlanterDetails(planterId);
        return ResponseEntity.ok(new ApiResponse<>(true, details, "Détails du planteur récupérés"));
    }

    /**
     * Récupère les planteurs avec le plus de production
     */
    @GetMapping("/top-planters")
    public ResponseEntity<ApiResponse<List<ChartDataDTO>>> getTopPlanters(
            @RequestParam(defaultValue = "10") int limit) {
        List<ChartDataDTO> data = dashboardService.getTopPlanters(limit);
        return ResponseEntity.ok(new ApiResponse<>(true, data, "Top planteurs récupérés"));
    }

    /**
     * Récupère les plantations avec le plus de surface cultivée
     */
    @GetMapping("/top-plantations-by-area")
    public ResponseEntity<ApiResponse<List<ChartDataDTO>>> getTopPlantationsByArea(
            @RequestParam(defaultValue = "10") int limit) {
        List<ChartDataDTO> data = dashboardService.getTopPlantationsByArea(limit);
        return ResponseEntity.ok(new ApiResponse<>(true, data, "Top plantations récupérées"));
    }

    /**
     * Récupère les statistiques de paiement
     */
    @GetMapping("/payment-statistics")
    public ResponseEntity<ApiResponse<PaymentStatisticsDTO>> getPaymentStatistics() {
        PaymentStatisticsDTO stats = dashboardService.getPaymentStatistics();
        return ResponseEntity.ok(new ApiResponse<>(true, stats, "Statistiques de paiement récupérées"));
    }

    /**
     * Récupère la répartition des planteurs par village
     */
    @GetMapping("/planters-by-village")
    public ResponseEntity<ApiResponse<List<ChartDataDTO>>> getPlantersByVillage() {
        List<ChartDataDTO> data = dashboardService.getPlantersByVillage();
        return ResponseEntity.ok(new ApiResponse<>(true, data, "Répartition par village récupérée"));
    }

    /**
     * Récupère la production moyenne par hectare
     */
    @GetMapping("/avg-production-per-hectare")
    public ResponseEntity<ApiResponse<Double>> getAverageProductionPerHectare() {
        Double avg = dashboardService.getAverageProductionPerHectare();
        return ResponseEntity.ok(new ApiResponse<>(true, avg, "Production moyenne calculée"));
    }

    /**
     * Récupère le prix d'achat moyen par kg
     */
    @GetMapping("/avg-purchase-price")
    public ResponseEntity<ApiResponse<Double>> getAveragePurchasePrice() {
        Double avg = dashboardService.getAveragePurchasePrice();
        return ResponseEntity.ok(new ApiResponse<>(true, avg, "Prix moyen calculé"));
    }

    /**
     * Récupère les statistiques démographiques des planteurs
     */
    @GetMapping("/planter-demographics")
    public ResponseEntity<ApiResponse<PlanterDemographicsDTO>> getPlanterDemographics() {
        PlanterDemographicsDTO demographics = dashboardService.getPlanterDemographics();
        return ResponseEntity.ok(new ApiResponse<>(true, demographics, "Démographie récupérée"));
    }

}