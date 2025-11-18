package com.avos.sipra.sipagri.services.cores;

import com.avos.sipra.sipagri.services.dtos.*;

import java.util.List;

public interface DashboardService {

    /**
     * Retrieves a list of resume data.
     *
     * @return a list of {@code ResumeDTO} objects, where each object contains details like name, value, and monthly value
     */
    List<ResumeDTO> getResumesData();

    /**
     * Retrieves a list of resumes corresponding to a specific supervisor.
     *
     * @param supervisor the SupervisorDTO object containing details of the supervisor
     *                   for whom the resume data is to be fetched
     * @return a list of ResumeDTO objects, each representing the resume data associated
     *         with the provided supervisor
     */
    List<ResumeDTO> getResumesDataBySupervisor(Long supervisor);

    /**
     * Retrieves production data categorized by sector for a given year.
     *
     * @param year the year for which production data is to be retrieved; must be a valid integer
     * @return a list of {@code ChartDataDTO} instances, each representing production data for a specific sector
     */
    List<ChartDataDTO> getProductionBySector(Integer year);

    /**
     * Retrieves a list of available years that can be used for querying or processing data.
     *
     * @return a list of integers representing the available years
     */
    List<Integer> getAvailableYears();

    /**
     * Retrieves production data for a specified period.
     *
     * @param period the time period for which production data is required; it should be a valid time period identifier
     * @return a list of {@code ChartDataDTO} objects representing the production data for the specified period
     */
    List<ChartDataDTO> getProductionByPeriod(String period);

    /**
     * Retrieves production data grouped by plantation.
     *
     * @return a list of ChartDataDTO objects containing production information
     *         segmented by plantation, including details such as name, value,
     *         and optionally period or color.
     */
    List<ChartDataDTO> getProductionByPlantation();

    /**
     * Retrieves the production trend data consisting of periods and their corresponding values.
     *
     * @return a list of {@code ProductionTrendDTO} objects, where each object represents
     *         a specific production period along with its associated value.
     */
    List<ProductionTrendDTO> getProductionTrend();

    /**
     * Retrieves production data categorized by both supervisor and sector for a specified year.
     *
     * @param year the year for which production data is to be fetched; must be a valid integer
     * @return a list of {@code ChartDataDTO} objects, each representing production data grouped
     *         by supervisor and sector for the specified year
     */
    List<ChartDataDTO> getProductionBySupervisorBySector(Long supervisor, Integer year);

    /**
     * Retrieves a list of available years associated with a supervisor.
     *
     * @return a list of integers representing the available years for data related to a supervisor
     */
    List<Integer> getAvailableYearsBySupervisor(Long supervisor);

    /**
     * Retrieves production data for a specific supervisor during a specified period.
     *
     * @param supervisor the unique identifier of the supervisor whose production data is to be fetched
     * @param period the time period for which production data is required; must be a valid period identifier
     * @return a list of {@code ChartDataDTO} objects representing the production data associated with the supervisor for the specified period
     */
    List<ChartDataDTO> getProductionBySupervisorByPeriod(Long supervisor, String period);

    /**
     * Retrieves production data grouped by plantation for a specific supervisor.
     *
     * @param supervisor the ID of the supervisor whose plantation production data is to be retrieved
     * @return a list of {@code ChartDataDTO} objects representing the production data segmented
     *         by plantation for the specified supervisor
     */
    List<ChartDataDTO> getProductionByPlantationBySupervisor(Long supervisor);

    /**
     * Retrieves the production trend data for a specific supervisor.
     *
     * @param supervisor the unique identifier of the supervisor whose production trend data is to be retrieved
     * @return a list of {@code ProductionTrendDTO} objects, where each object contains
     *         a specific production period and its associated value
     */
    List<ProductionTrendDTO> getProductionTrendBySupervisor(Long supervisor);
}