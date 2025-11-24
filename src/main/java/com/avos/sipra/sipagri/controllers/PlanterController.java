package com.avos.sipra.sipagri.controllers;

import com.avos.sipra.sipagri.annotations.XSSProtected;
import com.avos.sipra.sipagri.services.cores.PlanterService;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.PlanterDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The PlanterController class is a REST controller responsible for handling HTTP requests
 * related to the management of planters. It provides endpoints to retrieve, create, update,
 * partially update, and delete planters, as well as to search for and filter planters
 * based on specific criteria.
 *
 * The controller communicates with the PlanterService to perform business logic operations
 * on planter data and returns the corresponding responses to the clients.
 *
 * Mappings:
 * - GET /api/v1/planters: Fetches a paginated list of all planters.
 * - GET /api/v1/planters/by_supervisor: Fetches a paginated list of planters filtered by supervisor ID.
 * - GET /api/v1/planters/all: Retrieves all planters, optionally filtered by supervisor ID.
 * - GET /api/v1/planters/{id}: Fetches a specific planter by its ID.
 * - GET /api/v1/planters/search: Searches for planters based on a search term and optional criteria such as "village".
 * - POST /api/v1/planters: Creates a new planter record.
 * - PUT /api/v1/planters: Updates an existing planter record.
 * - PATCH /api/v1/planters: Partially updates an existing planter record.
 * - DELETE /api/v1/planters/{id}: Deletes an existing planter record by ID.
 *
 * Note:
 * - Pagination parameters are supported for endpoints retrieving multiple planters, including `page` and `size`.
 * - Some endpoints, such as those handling `POST`, `PUT`, and `PATCH` operations, use input validation to handle data.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/planters")
public class PlanterController {
    /**
     * Service layer dependency used for managing planter operations.
     * Provides business logic required to interact with planter data.
     * This variable is injected into the class and is utilized by the
     * controller methods to handle various planter-related requests.
     */
    private final PlanterService planterService;

    /**
     * Constructs a PlanterController with the provided PlanterService instance.
     *
     * @param planterService the service layer dependency to manage planter-related operations
     */
    public PlanterController(PlanterService planterService) {
        this.planterService = planterService;
    }

    /**
     * Retrieves a paginated list of planters.
     *
     * @param page the page number to be retrieved, defaults to 0 if not provided.
     * @param size the number of items per page, defaults to 10 if not provided.
     * @return a {@link ResponseEntity} containing a {@link PaginationResponseDTO} with the list of planters.
     *         Returns a 404 response if no data is found.
     */
    @GetMapping
    public ResponseEntity<PaginationResponseDTO<PlanterDTO>> getPlanters(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size)
    {
        Pageable pageable = PageRequest.of(page, size);

        PaginationResponseDTO<PlanterDTO> responseDTO = planterService.findAllPaged(pageable);
        if (responseDTO.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Retrieves a paginated list of planters associated with a specific supervisor.
     *
     * @param page the page number to retrieve; defaults to 0 if not specified
     * @param size the number of items per page; defaults to 10 if not specified
     * @param supervisorId the ID of the supervisor whose associated planters are to be retrieved
     * @return a ResponseEntity containing a PaginationResponseDTO of PlanterDTOs;
     *         returns 404 Not Found if no data is available
     */
    @GetMapping("/by_supervisor")
    public ResponseEntity<PaginationResponseDTO<PlanterDTO>> getPlantersBySupervisor(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam Long supervisorId)
    {
        Pageable pageable = PageRequest.of(page, size);

        PaginationResponseDTO<PlanterDTO> responseDTO = planterService.findPlanterBySupervisor(pageable, supervisorId);
        if (responseDTO.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Retrieves a list of all planters. Optionally filters the planters
     * by a specific supervisor if a supervisor ID is provided.
     *
     * @param supervisorId an optional parameter to filter planters by a specific supervisor.
     *                     If null, all planters are retrieved without filtering.
     * @return a ResponseEntity containing a list of PlanterDTO objects. If no planters are found,
     *         a ResponseEntity with a not-found status is returned.
     */
    @GetMapping("/all")
    public ResponseEntity<List<PlanterDTO>> getAllPlanters(
            @RequestParam(required = false) Long supervisorId) {
        List<PlanterDTO> planterDTO = supervisorId != null ?
                planterService.findAll(supervisorId) :
                planterService.findAll();
        if (planterDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(planterDTO);
    }

    /**
     * Retrieves a planter by its unique identifier.
     *
     * @param id the unique identifier of the planter to retrieve
     * @return a {@code ResponseEntity} containing the {@code PlanterDTO} if found;
     *         otherwise, a {@code ResponseEntity} with a not found status
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlanterDTO> getPlanterById(@PathVariable Long id) {
        PlanterDTO planterDTO = planterService.findOne(id);
        if (planterDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(planterDTO);
    }

    /**
     * Searches for planters based on the specified search term and optional criteria.
     * Results are paginated according to the provided page and size parameters.
     *
     * @param search the search term to filter planters
     * @param criteria optional criteria key to modify the search behavior (e.g., "village");
     *                 defaults to "false" if not provided
     * @param page the zero-based page index for pagination; defaults to 0
     * @param size the size of the page for pagination; defaults to 10
     * @return a paginated response containing a list of matching planters;
     *         if no matches are found, a 404 response is returned
     */
    @GetMapping("/search")
    public ResponseEntity<PaginationResponseDTO<PlanterDTO>> searchPlanters(
            @RequestParam String search,
            @RequestParam(required = false, defaultValue = "false") String criteria,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PaginationResponseDTO<PlanterDTO> responseDTO = "village".equalsIgnoreCase(criteria) ?
                planterService.findAllPagedByVillage(pageable, search) :
                planterService.findAllPagedByParams(pageable, search);

        if (responseDTO == null || responseDTO.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Creates a new planter resource based on the provided data.
     *
     * @param planterDTO the planter data transfer object containing the details of the planter to be created
     * @return a ResponseEntity containing the created PlanterDTO with an HTTP status of 201 (Created) if successful,
     *         or an HTTP status of 404 (Not Found) if the planter could not be created
     */
    @PostMapping
    @XSSProtected
    public ResponseEntity<PlanterDTO> createPlanter(@RequestBody PlanterDTO planterDTO) {
        PlanterDTO planter = planterService.save(planterDTO);
        if (planter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(planter);
    }

    /**
     * Updates an existing planter resource with the provided data.
     *
     * @param planterDTO the planter data transfer object containing the details to update
     * @return a {@code ResponseEntity} containing the updated {@code PlanterDTO} object with an
     *         HTTP status of 202 (Accepted) if the update is successful; otherwise, a
     *         {@code ResponseEntity} with a not found status if the planter does not exist
     */
    @PutMapping
    @XSSProtected
    public ResponseEntity<PlanterDTO> updatePlanter(@RequestBody PlanterDTO planterDTO) {
        PlanterDTO planter = planterService.update(planterDTO);
        if (planter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(planter);
    }

    /**
     * Partially updates the information of an existing planter.
     *
     * @param planterDTO the data transfer object containing the planter details to be patched
     * @return a {@code ResponseEntity} containing the updated planter information if successful,
     *         or a {@code ResponseEntity} with a not found status if the planter does not exist
     */
    @PatchMapping
    @XSSProtected
    public ResponseEntity<PlanterDTO> patchPlanter(@RequestBody PlanterDTO planterDTO) {
        PlanterDTO planter = planterService.partialUpdate(planterDTO);
        if (planter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(planter);
    }

    /**
     * Deletes a specific planter resource identified by its unique ID.
     * If the planter does not exist, a 404 response status is returned.
     * If the planter exists, it is deleted, and a 200 response status is returned.
     *
     * @param id the unique identifier of the planter to delete
     * @return a {@code ResponseEntity} with an HTTP status of 200 (OK)
     *         if the deletion is successful. If the planter does not exist,
     *         an HTTP status of 404 (Not Found) is returned.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<PlanterDTO> deletePlanter(@PathVariable Long id) {
        PlanterDTO planterDTO = planterService.findOne(id);
        if (planterDTO == null) {
            return ResponseEntity.notFound().build();
        }
        planterService.delete(id);
        return ResponseEntity.ok().build();
    }
}
