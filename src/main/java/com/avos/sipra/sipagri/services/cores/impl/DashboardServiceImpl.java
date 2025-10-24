package com.avos.sipra.sipagri.services.cores.impl;

import com.avos.sipra.sipagri.enums.PlantationStatus;
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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    public static final String F_CETTE_ANNEE = " cette année";
    public static final String MMM_YYYY = "MMM yyyy";
    private final PlanterRepository planterRepository;
    private final PlantationRepository plantationRepository;
    private final ProductionRepository productionRepository;

    /**
     * ✨ CORRIGÉ : Récupère les statistiques de résumé du dashboard (tout en années)
     */
    @Override
    public List<ResumeDTO> getResumesData() {
        List<ResumeDTO> resumes = new ArrayList<>();
        LocalDate now = LocalDate.now();
        LocalDate startOfYear = LocalDate.of(now.getYear(), 1, 1);

        // 1. Nombre de planteurs (total + ajoutés cette année)
        long totalPlanters = planterRepository.count();
        long currentYearPlanters = planterRepository.countPlantersCreatedAfter(startOfYear.atStartOfDay());
        resumes.add(new ResumeDTO(
                "Nombre de planteurs",
                String.valueOf(totalPlanters),
                "+ " + currentYearPlanters + F_CETTE_ANNEE
        ));

        // 2. Production totale (total + cette année)
        Double totalProduction = productionRepository.sumTotalProduction();
        if (totalProduction == null) totalProduction = 0.0;
        Double currentYearProduction = productionRepository.sumProductionForYear(now.getYear());
        if (currentYearProduction == null) currentYearProduction = 0.0;
        resumes.add(new ResumeDTO(
                "Production totale",
                formatProduction(totalProduction),
                "+" + formatProduction(currentYearProduction) + F_CETTE_ANNEE
        ));

        // 3. Revenus générés (total + cette année)
        Double totalRevenue = productionRepository.sumTotalRevenue();
        log.info("Total revenue: {}", totalRevenue);
        if (totalRevenue == null) totalRevenue = 0.0;
        Double currentYearRevenue = productionRepository.sumRevenueForYear(now.getYear());
        log.info("Current year revenue: {}", currentYearRevenue);
        if (currentYearRevenue == null) currentYearRevenue = 0.0;
        resumes.add(new ResumeDTO(
                "Revenus générés",
                formatCurrency(totalRevenue),
                "+" + formatCurrency(currentYearRevenue) + " F CFA" + F_CETTE_ANNEE
        ));

        // 4. Plantations (total + actives)
        long totalPlantations = plantationRepository.count();
        long activePlantations = plantationRepository.countByStatus(PlantationStatus.ACTIVE);
        resumes.add(new ResumeDTO(
                "Plantations",
                String.valueOf(totalPlantations) + " total",
                activePlantations + " actives"
        ));

        // 5. Valeur des kits (total + plantations actives)
        Double totalKitsValue = plantationRepository.sumAllKitsValue();
        if (totalKitsValue == null) totalKitsValue = 0.0;
        Double activeKitsValue = plantationRepository.sumKitsValueForActivePlantations(PlantationStatus.ACTIVE);
        if (activeKitsValue == null) activeKitsValue = 0.0;
        resumes.add(new ResumeDTO(
                "Valeur des kits",
                formatCurrency(totalKitsValue),
                formatCurrency(activeKitsValue) +  " F CFA (plantations actives)"
        ));

        return resumes;
    }

    /**
     * ✨ CORRIGÉ : Production par secteur (groupé correctement)
     */
    @Override
    public List<ChartDataDTO> getProductionBySector(Integer year) {
        List<Object[]> results;

        if (year != null) {
            results = productionRepository.sumProductionBySectorAndYear(year);
            log.debug("Production par secteur pour l'année {} : {} secteurs", year, results.size());
        } else {
            results = productionRepository.sumProductionBySector();
            log.debug("Production totale par secteur : {} secteurs", results.size());
        }

        return results.stream()
                .map(result -> {
                    String sectorName = (String) result[0];
                    Double totalProduction = (Double) result[1];
                    log.debug("Secteur: {}, Production: {} kg", sectorName, totalProduction);
                    return new ChartDataDTO(sectorName, totalProduction);
                })
                .toList();
    }

    /**
     * Liste des années disponibles
     */
    @Override
    public List<Integer> getAvailableYears() {
        List<Integer> years = productionRepository.findDistinctYears();
        log.debug("Années disponibles : {}", years);
        return years;
    }

    /**
     * Récupère les données de production groupées par période
     */
    @Override
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
    @Override
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
     * Tendance de production par année
     */
    @Override
    public List<ProductionTrendDTO> getProductionTrend() {
        List<Production> productions = productionRepository.findAllOrderByYear();

        Map<Integer, Double> yearlyData = new TreeMap<>();

        for (Production production : productions) {
            Date productionDate = production.getYear();

            if (productionDate == null) {
                log.warn("Production sans date : {}", production);
                continue;
            }

            LocalDate localDate = productionDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            int year = localDate.getYear();
            yearlyData.merge(year, production.getProductionInKg(), Double::sum);
        }

        List<ProductionTrendDTO> result = yearlyData.entrySet().stream()
                .map(entry -> new ProductionTrendDTO(
                        String.valueOf(entry.getKey()),
                        entry.getValue()
                ))
                .toList();

        log.debug("Tendance de production : {} années trouvées", result.size());
        return result;
    }

    /**
     * Récupère les détails d'un planteur
     */
    @Override
    public PlanterDetailsDTO getPlanterDetails(Long planterId) {
        Planter planter = planterRepository.findById(planterId)
                .orElseThrow(() -> new ResourceNotFoundException("Planter", "id", planterId));
        return new PlanterDetailsDTO(planter);
    }

    /**
     * Récupère les planteurs avec le plus de production
     */
    @Override
    public List<ChartDataDTO> getTopPlanters(int limit) {
        List<Object[]> results = productionRepository.findTopPlantersByProduction(limit);

        return results.stream()
                .map(result -> new ChartDataDTO(
                        result[0] + " " + result[1],
                        (Double) result[2]
                ))
                .toList();
    }

    /**
     * Récupère les plantations avec le plus de surface cultivée
     */
    @Override
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
    @Override
    public PaymentStatisticsDTO getPaymentStatistics() {
        long totalProductions = productionRepository.count();
        long unpaidProductions = productionRepository.countByMustBePaidTrue();
        long paidProductions = totalProductions - unpaidProductions;

        return new PaymentStatisticsDTO(paidProductions, unpaidProductions, totalProductions);
    }

    /**
     * Récupère la répartition des planteurs par village
     */
    @Override
    public List<ChartDataDTO> getPlantersByVillage() {
        List<Object[]> results = planterRepository.countPlantersByVillage();

        return results.stream()
                .map(result -> new ChartDataDTO(
                        (String) result[0],
                        ((Long) result[1]).doubleValue()
                ))
                .toList();
    }

    /**
     * Récupère la production moyenne par hectare
     */
    @Override
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
    @Override
    public Double getAveragePurchasePrice() {
        Double avg = productionRepository.avgPurchasePrice();
        return avg != null ? avg : 0.0;
    }

    /**
     * Récupère les statistiques démographiques des planteurs
     */
    @Override
    public PlanterDemographicsDTO getPlanterDemographics() {
        List<Object[]> genderResults = planterRepository.countByGender();
        List<ChartDataDTO> byGender = genderResults.stream()
                .map(result -> new ChartDataDTO(
                        result[0].toString(),
                        ((Long) result[1]).doubleValue()
                ))
                .toList();

        List<Object[]> maritalResults = planterRepository.countByMaritalStatus();
        List<ChartDataDTO> byMaritalStatus = maritalResults.stream()
                .map(result -> new ChartDataDTO(
                        result[0].toString(),
                        ((Long) result[1]).doubleValue()
                ))
                .toList();

        Double avgAge = planterRepository.calculateAverageAge();
        if (avgAge == null) avgAge = 0.0;

        Double avgChildren = planterRepository.averageChildrenNumber();
        if (avgChildren == null) avgChildren = 0.0;

        return new PlanterDemographicsDTO(byGender, byMaritalStatus, avgAge, avgChildren);
    }

    // ============ Méthodes utilitaires privées ============

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
            return String.format("%.1f M", amount / 1_000_000);
        } else if (amount >= 1_000) {
            return String.format("%.1f K", amount / 1_000);
        }
        return String.format("%.0f", amount);
    }

    private String formatDateByPeriod(Date date, String period) {
        DateTimeFormatter formatter;

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