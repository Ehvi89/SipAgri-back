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

/**
 * Implementation of the Dashboard Service that provides methods for retrieving and summarizing data
 * related to planters, production, revenue, plantations, and other relevant categories.
 * This class interacts with various repositories to fetch and process data for generating
 * summaries, trends, and visualizations.
 * <p>
 * Fields:
 * - F_CETTE_ANNEE: Constant or configuration for denoting the current year.
 * - MMM_YYYY: Format specifier for representing dates.
 * - planterRepository: Repository for accessing and querying planter-related data.
 * - plantationRepository: Repository for accessing and querying plantation-related data.
 * - productionRepository: Repository for accessing and querying production-related data.
 * <p>
 * Methods:
 * - getResumesData(): Provides summarized data for planters, production, revenues, plantations, and kit values.
 * - getResumesDataBySupervisor(Long supervisor): Fetches summarized data for a specific supervisor.
 * - getProductionBySector(Integer year): Aggregates production data by sector for a specific year or all years if not specified.
 * - getProductionBySupervisorBySector(Long supervisor, Integer year): Aggregates production data by supervisor and sector, filtered by year if specified.
 * - getAvailableYears(): Retrieves distinct years from the production repository.
 * - getAvailableYearsBySupervisor(Long supervisor): Retrieves distinct years for a specific supervisor from the production repository.
 * - getProductionByPeriod(String period): Groups production data by a specified period such as week, quarter, or year.
 * - getProductionBySupervisorByPeriod(Long supervisor, String period): Groups production data by supervisor for a specified period.
 * - getProductionByPlantation(): Provides total production data grouped by plantation.
 * - getProductionByPlantationBySupervisor(Long supervisor): Provides plantation-specific production totals for a given supervisor.
 * - getProductionTrend(): Analyzes and returns production trends aggregated by year.
 * - getProductionTrendBySupervisor(Long supervisor): Analyzes production trends for a specific supervisor.
 * - formatProduction(double kg): Converts a production value into a readable string format with units like kg, tons (T), or kilotons (kT).
 * - formatCurrency(double amount): Formats monetary values into a simplified currency string (e.g., K for thousands, M for millions).
 * - formatDate(Date date, String period): Formats dates based on a specified period, such as quarter, week, or year.
 */
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
     * Retrieves summarized data for various categories including planters, production, revenue, plantations, and kit values.
     * The summary includes total values and current year-specific values or statuses where applicable.
     *
     * @return a list of {@code ResumeDTO} objects containing summarized data for planters, production, revenue generated,
     * plantations, and kit values. Each summary includes a description, a total, and additional contextual information
     * such as values or counts related to the current year or specific statuses.
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
     * Retrieves summarized data based on the provided supervisor ID.
     * This summary includes details such as the number of planters, production volumes,
     * revenue, plantations, and kit values. Each aspect provides the total value
     * along with additional data specific to the current year, active statuses, or similar distinctions.
     *
     * @param supervisor the ID of the supervisor for whom the data is being retrieved
     * @return a list of {@code ResumeDTO} objects containing summarized data including descriptions,
     *         total values, and contextual information for planters, production, revenues,
     *         plantations, and kit values
     */
    public List<ResumeDTO> getResumesDataBySupervisor(Long supervisor) {
        List<ResumeDTO> resumes = new ArrayList<>();
        LocalDate now = LocalDate.now();
        LocalDate startOfYear = LocalDate.of(now.getYear(), 1, 1);

        // 1. Nombre de planteurs (total + ajoutés cette année)
        long totalPlanters = planterRepository.countPlantersBySupervisor_Id(supervisor);
        long currentYearPlanters = planterRepository.countBySupervisor_IdAndCreatedAtGreaterThanEqual(supervisor, startOfYear.atStartOfDay());
        resumes.add(new ResumeDTO(
                "Nombre de planteurs",
                String.valueOf(totalPlanters),
                "+ " + currentYearPlanters + F_CETTE_ANNEE
        ));

        // 2. Production totale (total + cette année)
        Double totalProduction = productionRepository.countTotalProductionBySupervisor(supervisor);
        if (totalProduction == null) totalProduction = 0.0;
        Double currentYearProduction = productionRepository.sumProductionForYearBySupervisor(now.getYear(), supervisor);
        if (currentYearProduction == null) currentYearProduction = 0.0;
        resumes.add(new ResumeDTO(
                "Production totale",
                formatProduction(totalProduction),
                "+" + formatProduction(currentYearProduction) + F_CETTE_ANNEE
        ));

        // 3. Revenus générés (total + cette année)
        Double totalRevenue = productionRepository.sumTotalRevenueBySupervisor(supervisor);
        log.info("Total revenue: {}", totalRevenue);
        if (totalRevenue == null) totalRevenue = 0.0;
        Double currentYearRevenue = productionRepository.sumRevenueForYearBySupervisor(now.getYear(), supervisor);
        log.info("Current year revenue: {}", currentYearRevenue);
        if (currentYearRevenue == null) currentYearRevenue = 0.0;
        resumes.add(new ResumeDTO(
                "Revenus générés",
                formatCurrency(totalRevenue),
                "+" + formatCurrency(currentYearRevenue) + " F CFA" + F_CETTE_ANNEE
        ));

        // 4. Plantations (total + actives)
        long totalPlantations = plantationRepository.countPlantationBySupervisor(supervisor);
        long activePlantations = plantationRepository.countPlantationBySupervisorByStatus(PlantationStatus.ACTIVE, supervisor);
        resumes.add(new ResumeDTO(
                "Plantations",
                String.valueOf(totalPlantations) + " total",
                activePlantations + " actives"
        ));

        // 5. Valeur des kits (total + plantations actives)
        Double totalKitsValue = plantationRepository.sumAllKitsValueBySupervisor(supervisor);
        if (totalKitsValue == null) totalKitsValue = 0.0;
        Double activeKitsValue = plantationRepository.sumKitsValueForActivePlantationsBySupervisor(PlantationStatus.ACTIVE, supervisor);
        if (activeKitsValue == null) activeKitsValue = 0.0;
        resumes.add(new ResumeDTO(
                "Valeur des kits",
                formatCurrency(totalKitsValue),
                formatCurrency(activeKitsValue) +  " F CFA (plantations actives)"
        ));

        return resumes;
    }

    /**
     * Retrieves production data aggregated by sector.
     * <p>
     * If a specific year is provided, the method fetches the sum of production
     * values for each sector for that year. If no year is specified, it retrieves
     * the total production data grouped by sector across all available years.
     * <p>
     * The production data is then mapped into a list of {@code ChartDataDTO} objects,
     * with each object containing the sector name and corresponding production value.
     *
     * @param year the year for which production data is to be retrieved; if null,
     *             the method retrieves data for all years
     * @return a list of {@code ChartDataDTO} objects, where each object contains
     *         the sector name and its corresponding total production value
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
     * Retrieves production data grouped by supervisor and sector. If a specific year
     * is provided, the data is filtered to include only the corresponding year's production.
     * Otherwise, it retrieves the total production data grouped by sector.
     *
     * @param supervisor the identifier of the supervisor for whom the production data
     *                   needs to be retrieved
     * @param year       the specific year for which production data is requested.
     *                   If null, the method retrieves total production data without
     *                   year-based filtering
     * @return a list of {@code ChartDataDTO} objects containing production data for each sector
     */
    @Override
    public List<ChartDataDTO> getProductionBySupervisorBySector(Long supervisor, Integer year) {
        List<Object[]> results;

        if (year != null) {
            results = productionRepository.sumProductionBySupervisorBySectorAndYear(supervisor, year);
            log.debug("Production par secteur pour l'année {} : {} secteurs", year, results.size());
        } else {
            results = productionRepository.sumProductionBySupervisorBySector(supervisor);
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
     * Retrieves the list of available years from the production data.
     * The years are distinct and represent the periods during which production data is available.
     *
     * @return a list of integers, where each integer represents an available year.
     */
    @Override
    public List<Integer> getAvailableYears() {
        List<Integer> years = productionRepository.findDistinctYears();
        log.debug("Années disponibles : {}", years);
        return years;
    }

    /**
     * Retrieves a list of distinct years associated with supervisors from the production repository.
     *
     * @return a list of integers representing the distinct years available for supervisors
     */
    @Override
    public List<Integer> getAvailableYearsBySupervisor(Long supervisor) {
        List<Integer> years = productionRepository.findDistinctYearsBySupervisor(supervisor);
        log.debug("Années disponibles : {}", years);
        return years;
    }

    /**
     * Retrieves production data aggregated by the specified period.
     *
     * @param period the period for grouping production data. Possible values include "week", "quarter", "year",
     *               or any custom format supported by the implementation.
     * @return a list of ChartDataDTO containing the aggregated production values grouped by the specified period.
     */
    @Override
    public List<ChartDataDTO> getProductionByPeriod(String period) {
        List<Production> productions = productionRepository.findAllOrderByYear();
        return getChartDataDTOS((String) period, productions);
    }

    /**
     * Retrieves production data for a specific supervisor and period.
     *
     * @param supervisor the ID of the supervisor whose production data is to be retrieved
     * @param period the time period for which production data is to be retrieved
     * @return a list of ChartDataDTO objects containing the production data for the specified supervisor and period
     */
    @Override
    public List<ChartDataDTO> getProductionBySupervisorByPeriod(Long supervisor, String period) {
        List<Production> productions = productionRepository.findAllBySupervisorOrderByYear(supervisor);
        return getChartDataDTOS((String) period, productions);
    }

    /**
     * Retrieves the total production data grouped by plantation.
     * <p>
     * The method fetches the production data aggregated by plantations from
     * the production repository, then maps the results into a list of
     * ChartDataDTO objects, which contain the plantation name and the corresponding
     * total production value.
     *
     * @return a list of ChartDataDTO objects, where each object contains the plantation name
     *         and corresponding production total.
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
     * Retrieves the total production data grouped by plantation for a given supervisor.
     *
     * @param supervisor The ID of the supervisor whose production data is to be retrieved.
     * @return A list of ChartDataDTO objects, each containing the plantation name and its associated total production.
     */
    @Override
    public List<ChartDataDTO> getProductionByPlantationBySupervisor(Long supervisor) {
        List<Object[]> results = productionRepository.sumProductionByPlantationBySupervisor(supervisor);

        return results.stream()
                .map(result -> new ChartDataDTO(
                        (String) result[0],  // plantation name
                        (Double) result[1]   // total production
                ))
                .toList();
    }

    /**
     * Retrieves the production trend data aggregated by year.
     * <p>
     * This method fetches all production records, groups them by year,
     * and calculates the total production in kilograms for each year.
     * The resulting data is transformed into a list of {@code ProductionTrendDTO}
     * objects, where each object represents a specific year and its corresponding
     * total production value.
     *
     * @return a list of {@code ProductionTrendDTO} objects, where each object contains
     *         the year (as a string) and the corresponding total production value.
     */
    @Override
    public List<ProductionTrendDTO> getProductionTrend() {
        List<Production> productions = productionRepository.findAllOrderByYear();

        return getProductionTrendDTOS(productions);
    }

    /**
     * Retrieves the production trend data for a specific supervisor.
     *
     * @param supervisor the ID of the supervisor for whom the production trend data is being fetched.
     * @return a list of ProductionTrendDTO objects representing the production trends associated with the specified supervisor.
     */
    @Override
    public List<ProductionTrendDTO> getProductionTrendBySupervisor(Long supervisor) {
        List<Production> productions = productionRepository.findAllBySupervisorOrderByYear(supervisor);

        return getProductionTrendDTOS(productions);
    }

    // ============ Méthodes utilitaires privées ============

    /**
     * Formats a production value in kilograms into a more readable string representation
     * with units of kg, tons (T), or kilotons (kT) depending on the scale of the value.
     *
     * @param kg the production value in kilograms to format
     * @return a formatted string representation of the production value
     */
    private String formatProduction(double kg) {
        if (kg >= 1000000) {
            return String.format("%.1f kT", kg / 1000000);
        } else if (kg >= 1000) {
            return String.format("%.1f T", kg / 1000);
        }
        return String.format("%.0f kg", kg);
    }

    /**
     * Formats a given numeric amount into a simplified currency string format.
     * If the amount is 1,000,000 or more, it will be represented in millions ("M").
     * If the amount is 1,000 or more but less than 1,000,000, it will be represented in thousands ("K").
     * Otherwise, it will be displayed as a whole number.
     *
     * @param amount the numeric value to be formatted
     * @return a formatted string representation of the monetary amount
     */
    private String formatCurrency(double amount) {
        if (amount >= 1_000_000) {
            return String.format("%.1f M", amount / 1_000_000);
        } else if (amount >= 1_000) {
            return String.format("%.1f K", amount / 1_000);
        }
        return String.format("%.0f", amount);
    }

    /**
     * Formats a given date based on the specified period (e.g., week, quarter, year, or default monthly format).
     *
     * @param date   the date object to be formatted
     * @param period the period to format by; supported values are "week", "quarter", "year", or a default
     *               monthly format when no valid period is provided
     * @return the formatted date string corresponding to the specified period
     */
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

    /**
     * Aggregates production data by the specified period and transforms the results
     * into a list of ChartDataDTO objects. The method groups production values based
     * on the formatted period and calculates the total production for each group.
     *
     * @param period the period by which production data should be grouped. Supported
     *               values include "week", "quarter", "year", or other formats supported
     *               by the implementation.
     * @param productions a list of Production objects containing the production data to be aggregated.
     * @return a list of ChartDataDTO objects, where each object represents a group
     *         with its associated aggregated production value and name.
     */
    private List<ChartDataDTO> getChartDataDTOS(String period, List<Production> productions) {
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
     * Calculates and returns the production trend data aggregated by year.
     * Each year's total production value is computed by summing up the production
     * values for all entries within that year and then mapped into a list
     * of ProductionTrendDTO objects.
     *
     * @param productions the list of Production objects containing production data
     *                    including production date and production values in kilograms
     * @return a list of ProductionTrendDTO objects, where each object contains
     *         the year (as a string) and the corresponding total production value
     */
    private static List<ProductionTrendDTO> getProductionTrendDTOS(List<Production> productions) {
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
}