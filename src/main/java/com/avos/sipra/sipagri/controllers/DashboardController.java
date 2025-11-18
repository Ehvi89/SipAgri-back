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

    /**
     *
     */
    private final DashboardService dashboardService;

    /**
     * Retrieves a list of resumes data.
     *
     * @return a ResponseEntity containing an ApiResponse with a list of ResumeDTOs.
     *         The ApiResponse includes a success flag, the list of ResumeDTOs as data,
     *         and a message indicating successful retrieval.
     */
    @GetMapping("/resumes")
    public ResponseEntity<ApiResponse<List<ResumeDTO>>> getResumesData() {
        List<ResumeDTO> resumes = dashboardService.getResumesData();
        return ResponseEntity.ok(new ApiResponse<>(true, resumes, "Statistiques récupérées avec succès"));
    }

    @GetMapping("/resumes-by-supervisor")
    public ResponseEntity<ApiResponse<List<ResumeDTO>>> getResumesDataBySupervisor(@RequestParam Long supervisor) {
        List<ResumeDTO> resumes = dashboardService.getResumesDataBySupervisor(supervisor);
        return ResponseEntity.ok(new ApiResponse<>(true, resumes, "Statistiques récupérées avec succès"));
    }

    /**
     * Retrieves production data grouped by sector for a specified year.
     *
     * @param year an optional parameter specifying the year for which production data is to be retrieved.
     *             If not provided, the data for all available years will be returned.
     * @return a ResponseEntity containing an ApiResponse with a list of ChartDataDTO objects.
     *         The ApiResponse includes a success flag, the production data grouped by sector as data,
     *         and a message indicating successful retrieval.
     */
    @GetMapping("/production-by-sector")
    public ResponseEntity<ApiResponse<List<ChartDataDTO>>> getProductionBySector(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long supervisor) {
        List<ChartDataDTO> data = supervisor != null ?
                dashboardService.getProductionBySupervisorBySector(supervisor, year) :
                dashboardService.getProductionBySector(year);
        return ResponseEntity.ok(new ApiResponse<>(true, data, "Production par secteur récupérée"));
    }

    /**
     * Retrieves a list of available years.
     *
     * @return a ResponseEntity containing an ApiResponse with a list of integers.
     *         The ApiResponse includes a success flag, the list of available years as data,
     *         and a success message indicating the retrieval.
     */
    @GetMapping("/available-years")
    public ResponseEntity<ApiResponse<List<Integer>>> getAvailableYears(
            @RequestParam(required = false) Long supervisor
    ) {
        List<Integer> years = supervisor != null ?
                dashboardService.getAvailableYearsBySupervisor(supervisor) :
                dashboardService.getAvailableYears();
        return ResponseEntity.ok(new ApiResponse<>(true, years, "Années disponibles récupérées"));
    }

    /**
     * Retrieves production data based on the specified time period.
     *
     * @param period the period for which the production data should be retrieved. Default is "month".
     *               Accepted values typically include predefined time intervals such as "month", "year", etc.
     * @return a ResponseEntity containing an ApiResponse with a list of ChartDataDTO objects.
     *         The ApiResponse includes a success flag, the list of ChartDataDTOs as data,
     *         and a message indicating successful retrieval of production data by period.
     */
    @GetMapping("/production-by-period")
    public ResponseEntity<ApiResponse<List<ChartDataDTO>>> getProductionByPeriod(
            @RequestParam(defaultValue = "month") String period,
            @RequestParam(required = false) Long supervisor) {
        List<ChartDataDTO> data = supervisor != null ?
                dashboardService.getProductionBySupervisorByPeriod(supervisor, period) :
                dashboardService.getProductionByPeriod(period);
        return ResponseEntity.ok(new ApiResponse<>(true, data, "Production par période récupérée"));
    }

    /**
     * Retrieves production data grouped by plantation.
     *
     * @return a ResponseEntity containing an ApiResponse with a list of ChartDataDTO objects.
     *         The ApiResponse includes a success flag, the list of ChartDataDTOs as data,
     *         and a message indicating successful retrieval of production data by plantation.
     */
    @GetMapping("/production-by-plantation")
    public ResponseEntity<ApiResponse<List<ChartDataDTO>>> getProductionByPlantation(
            @RequestParam(required = false) Long supervisor
    ) {
        List<ChartDataDTO> data = supervisor != null ?
                dashboardService.getProductionByPlantationBySupervisor(supervisor) :
                dashboardService.getProductionByPlantation();
        return ResponseEntity.ok(new ApiResponse<>(true, data, "Production par plantation récupérée"));
    }

    /**
     * Retrieves the production trend data.
     *
     * @return a ResponseEntity containing an ApiResponse with a list of ProductionTrendDTO.
     *         The ApiResponse includes a success flag, the list of ProductionTrendDTO as data,
     *         and a message indicating successful retrieval of the production trend.
     */
    @GetMapping("/production-trend")
    public ResponseEntity<ApiResponse<List<ProductionTrendDTO>>> getProductionTrend(
            @RequestParam(required = false) Long supervisor
    ) {
        List<ProductionTrendDTO> data = supervisor != null ?
                dashboardService.getProductionTrendBySupervisor(supervisor) :
                dashboardService.getProductionTrend();
        return ResponseEntity.ok(new ApiResponse<>(true, data, "Tendance de production récupérée"));
    }
}