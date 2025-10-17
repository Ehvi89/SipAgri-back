package com.avos.sipra.sipagri.services.cores.impl;

import com.avos.sipra.sipagri.exceptions.ResourceNotFoundException;
import com.avos.sipra.sipagri.services.cores.DashboardService;
import com.avos.sipra.sipagri.services.dtos.*;
import com.avos.sipra.sipagri.entities.Planter;
import com.avos.sipra.sipagri.entities.Plantation;
import com.avos.sipra.sipagri.entities.Production;
import com.avos.sipra.sipagri.repositories.PlanterRepository;
import com.avos.sipra.sipagri.repositories.PlantationRepository;
import com.avos.sipra.sipagri.repositories.ProductionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    public static final String F_CE_MOIS = "%+.1f%% ce mois";
    public static final String MMM_YYYY = "MMM yyyy";
    private final PlanterRepository planterRepository;
    private final PlantationRepository plantationRepository;
    private final ProductionRepository productionRepository;

    /**
     * Récupère les statistiques de résumé du dashboard
     */
    public List<ResumeDTO> getResumesData() {
        List<ResumeDTO> resumes = new ArrayList<>();
        LocalDate now = LocalDate.now();
        LocalDate oneMonthAgo = now.minusMonths(1);

        // Nombre de planteurs
        long totalPlanters = planterRepository.count();
        long previousPlanters = planterRepository.countPlantersCreatedBeforeMonth(oneMonthAgo.atStartOfDay());
        double plantersGrowth = calculateGrowthPercentage(previousPlanters, totalPlanters);
        resumes.add(new ResumeDTO(
                "Nombre de planteurs",
                String.valueOf(totalPlanters),
                String.format(F_CE_MOIS, plantersGrowth)
        ));

        // Production totale
        Double totalProduction = productionRepository.sumTotalProduction();
        if (totalProduction == null) totalProduction = 0.0;
        Double previousProduction = productionRepository.sumProductionBeforeMonth(oneMonthAgo.atStartOfDay());
        log.debug("Previous productions : {}", previousProduction);
        if (previousProduction == null) previousProduction = 0.0;
        double productionGrowth = calculateGrowthPercentage(previousProduction, totalProduction);
        resumes.add(new ResumeDTO(
                "Production totale",
                formatProduction(totalProduction),
                String.format(F_CE_MOIS, productionGrowth)
        ));

        // Revenus générés
        Double totalRevenue = productionRepository.sumTotalRevenue();
        if (totalRevenue == null) totalRevenue = 0.0;
        Double previousRevenue = productionRepository.sumRevenueBeforeMonth(oneMonthAgo.atStartOfDay());
        if (previousRevenue == null) previousRevenue = 0.0;
        double revenueGrowth = calculateGrowthPercentage(previousRevenue, totalRevenue);
        resumes.add(new ResumeDTO(
                "Revenus générés",
                formatCurrency(totalRevenue),
                '+' + formatCurrency((revenueGrowth * totalRevenue) / 100.0) + " ce mois"
        ));
        log.debug("Revenus générés : {}", totalRevenue);

        // Plantations actives
        long totalPlantations = plantationRepository.count();
        long previousPlantations = plantationRepository.countPlantationsCreatedBeforeMonth(oneMonthAgo.atStartOfDay());
        resumes.add(new ResumeDTO(
                "Plantations actives",
                String.valueOf(totalPlantations),
                "+ " + (totalPlantations - previousPlantations) + " nouvelles ce mois"
        ));

        return resumes;
    }

    /**
     * Récupère les données de production groupées par période
     */
    public List<ChartDataDTO> getProductionByPeriod(String period) {
        List<Production> productions = productionRepository.findAllOrderByYear();
        Map<String, Double> groupedData = new LinkedHashMap<>();

        for (Production production : productions) {
            String key = formatDateByPeriod(production.getYear(), period);
            groupedData.merge(key, production.getProductionInKg(), Double::sum);
        }

        return groupedData.entrySet().stream()
                .map(entry -> new ChartDataDTO(entry.getKey(), entry.getValue()))
                .toList();
    }

    /**
     * Récupère la répartition de production par plantation
     */
    public List<ChartDataDTO> getProductionByPlantation() {
        List<Object[]> results = productionRepository.sumProductionByPlantation();

        return results.stream()
                .map(result -> new ChartDataDTO(
                        (String) result[0],  // plantation name
                        (Double) result[1]   // total production
                ))
                .toList();
    }

    /**
     * Récupère les données de tendance de production dans le temps
     */
    public List<ProductionTrendDTO> getProductionTrend(int months) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(months);

        // Conversion LocalDate → java.sql.Date pour correspondre à la colonne SQL DATE
        Date start = java.sql.Date.valueOf(startDate);
        Date end = java.sql.Date.valueOf(endDate);

        // Appel du repository avec des paramètres de type Date
        List<Production> productions = productionRepository.findByYearBetween(start, end);

        Map<YearMonth, Double> monthlyData = new TreeMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(MMM_YYYY, Locale.FRENCH);

        for (Production production : productions) {
            Date productionDate = production.getYear();

            // Sécurité : éviter les null
            if (productionDate == null) {
                log.warn("Production sans date : {}", production);
                continue;
            }

            // Conversion Date → LocalDate
            LocalDate localDate = productionDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            // Extraction du mois et de l'année
            YearMonth yearMonth = YearMonth.from(localDate);

            // Agrégation des valeurs (somme par mois)
            monthlyData.merge(yearMonth, production.getProductionInKg(), Double::sum);
        }

        // Conversion en DTO pour le retour
        return monthlyData.entrySet().stream()
                .map(entry -> new ProductionTrendDTO(
                        entry.getKey().format(formatter), // ex: "oct. 2025"
                        entry.getValue()
                ))
                .toList();
    }

    /**
     * Récupère les détails d'un planteur avec ses plantations et productions
     */
    public PlanterDetailsDTO getPlanterDetails(Long planterId) {
        Planter planter = planterRepository.findById(planterId)
                .orElseThrow(() -> new ResourceNotFoundException("Planter", "id", planterId));

        return new PlanterDetailsDTO(planter);
    }

    /**
     * Récupère les planteurs avec le plus de production
     */
    public List<ChartDataDTO> getTopPlanters(int limit) {
        List<Object[]> results = productionRepository.findTopPlantersByProduction(limit);

        return results.stream()
                .map(result -> new ChartDataDTO(
                        result[0] + " " + result[1],  // firstname + lastname
                        (Double) result[2]             // total production
                ))
                .toList();
    }

    /**
     * Récupère les plantations avec le plus de surface cultivée
     */
    public List<ChartDataDTO> getTopPlantationsByArea(int limit) {
        List<Plantation> plantations = plantationRepository
                .findTopByOrderByFarmedAreaDesc(limit);

        return plantations.stream()
                .map(p -> new ChartDataDTO(p.getName(), p.getFarmedArea()))
                .toList();
    }

    /**
     * Récupère les statistiques de paiement
     */
    public PaymentStatisticsDTO getPaymentStatistics() {
        long totalProductions = productionRepository.count();
        long unpaidProductions = productionRepository.countByMustBePaidTrue();
        long paidProductions = totalProductions - unpaidProductions;

        return new PaymentStatisticsDTO(paidProductions, unpaidProductions, totalProductions);
    }

    /**
     * Récupère la répartition des planteurs par village
     */
    public List<ChartDataDTO> getPlantersByVillage() {
        List<Object[]> results = planterRepository.countPlantersByVillage();

        return results.stream()
                .map(result -> new ChartDataDTO(
                        (String) result[0],   // village
                        ((Long) result[1]).doubleValue()  // count
                ))
                .toList();
    }

    /**
     * Récupère la production moyenne par hectare
     */
    public Double getAverageProductionPerHectare() {
        Double totalProduction = productionRepository.sumTotalProduction();
        Double totalArea = plantationRepository.sumTotalFarmedArea();

        if (totalArea == null || totalArea == 0) return 0.0;
        if (totalProduction == null) return 0.0;

        return totalProduction / totalArea;
    }

    /**
     * Récupère le prix d'achat moyen par kg
     */
    public Double getAveragePurchasePrice() {
        Double avg = productionRepository.avgPurchasePrice();
        return avg != null ? avg : 0.0;
    }

    /**
     * Récupère les statistiques démographiques des planteurs
     */
    public PlanterDemographicsDTO getPlanterDemographics() {
        // Répartition par genre
        List<Object[]> genderResults = planterRepository.countByGender();
        List<ChartDataDTO> byGender = genderResults.stream()
                .map(result -> new ChartDataDTO(
                        result[0].toString(),
                        ((Long) result[1]).doubleValue()
                ))
                .toList();

        // Répartition par statut marital
        List<Object[]> maritalResults = planterRepository.countByMaritalStatus();
        List<ChartDataDTO> byMaritalStatus = maritalResults.stream()
                .map(result -> new ChartDataDTO(
                        result[0].toString(),
                        ((Long) result[1]).doubleValue()
                ))
                .toList();

        // Âge moyen
        Double avgAge = planterRepository.calculateAverageAge();
        if (avgAge == null) avgAge = 0.0;

        // Nombre moyen d'enfants
        Double avgChildren = planterRepository.averageChildrenNumber();
        if (avgChildren == null) avgChildren = 0.0;

        return new PlanterDemographicsDTO(byGender, byMaritalStatus, avgAge, avgChildren);
    }

    // ============ Méthodes utilitaires privées ============

    private double calculateGrowthPercentage(double previous, double current) {
        if (previous == 0) return current > 0 ? 100.0 : 0.0;
        return ((current - previous) / previous) * 100.0;
    }

    private String formatProduction(double kg) {
        if (kg >= 1000000) {
            return String.format("%.1f kT", kg / 1000000);
        } else if (kg >= 1000) {
            return String.format("%.1f T", kg / 1000);
        }
        return String.format("%.0f kg", kg);
    }

    private String formatCurrency(double amount) {
        if (amount >= 1_000_000) {
            return String.format("%.1f M F CFA", amount / 1_000_000);
        } else if (amount >= 1_000) {
            return String.format("%.1f K F CFA", amount / 1_000);
        }
        return String.format("%.0f F CFA", amount);
    }

    private String formatDateByPeriod(Date date, String period) {
        DateTimeFormatter formatter;

        // Conversion correcte de java.util.Date vers java.time.LocalDate
        LocalDate localDate = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        switch (period.toLowerCase()) {
            case "week":
                WeekFields weekFields = WeekFields.of(Locale.FRENCH);
                int weekNumber = localDate.get(weekFields.weekOfWeekBasedYear());
                int year = localDate.get(weekFields.weekBasedYear());
                return String.format("S%02d %d", weekNumber, year);

            case "quarter":
                int quarter = (localDate.getMonthValue() - 1) / 3 + 1;
                return String.format("T%d %d", quarter, localDate.getYear());

            case "year":
                return String.valueOf(localDate.getYear());

            default:
                formatter = DateTimeFormatter.ofPattern(MMM_YYYY, Locale.FRENCH);
                return localDate.format(formatter);
        }
    }
}