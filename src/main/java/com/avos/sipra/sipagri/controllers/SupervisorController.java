package com.avos.sipra.sipagri.controllers;

import com.avos.sipra.sipagri.annotations.XSSProtected;
import com.avos.sipra.sipagri.enums.SupervisorProfile;
import com.avos.sipra.sipagri.services.cores.SupervisorService;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.SupervisorDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The SupervisorController provides REST API endpoints for managing supervisors.
 * It handles operations such as retrieving, searching, adding, updating, and deleting supervisor records.
 * The controller interacts with the SupervisorService to process and manage supervisor data.
 */
@RestController
@RequestMapping("/api/v1/supervisors")
public class SupervisorController {
    /**
     * A service instance responsible for handling business logic and interactions related
     * to supervisor operations. This service is utilized within the {@code SupervisorController}
     * to delegate tasks related to managing supervisor data, such as retrieving, creating,
     * updating, or deleting supervisors.
     */
    private final SupervisorService supervisorService;

    /**
     * Constructs a new instance of SupervisorController with the specified SupervisorService.
     *
     * @param supervisorService the service layer used for supervisor operations
     */
    public SupervisorController(SupervisorService supervisorService) {
        this.supervisorService = supervisorService;
    }

    /**
     * Retrieves a paginated list of supervisors.
     *
     * @param page the page number to retrieve, zero-based. Defaults to 0 if not provided.
     * @param size the number of results per page. Defaults to 10 if not provided.
     * @return a {@link ResponseEntity} containing a {@link PaginationResponseDTO} with the list of supervisors.
     *         Returns a 404 status if no data is found.
     */
    @GetMapping
    public ResponseEntity<PaginationResponseDTO<SupervisorDTO>> findSupervisors(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PaginationResponseDTO<SupervisorDTO> response = supervisorService.findAllPaged(pageable);

        if (response.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a list of all supervisors.
     *
     * @return a {@link ResponseEntity} containing a list of {@link SupervisorDTO} objects
     *         if data is available. Returns a {@link ResponseEntity} with a not found status
     *         if no supervisors are available.
     */
    @GetMapping("/all")
    public ResponseEntity<List<SupervisorDTO>> findAll() {
        List<SupervisorDTO> supervisors = supervisorService.findAll();
        if (supervisors == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(supervisors);
    }

    /**
     * Retrieves a supervisor by their unique identifier.
     *
     * @param id the unique identifier of the supervisor to retrieve
     * @return a {@code ResponseEntity} containing the {@code SupervisorDTO} if found,
     *         or a {@code ResponseEntity} with a not found (404) status if the supervisor is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<SupervisorDTO> findById(@PathVariable long id) {
        SupervisorDTO supervisorDTO = supervisorService.findOne(id);
        if (supervisorDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(supervisorDTO);
    }

    /**
     * Searches for supervisors based on the provided search criteria and returns a paginated response.
     *
     * @param search the search term to filter supervisors.
     * @param page the page number to retrieve, starting from 0. Defaults to 0 if not provided.
     * @param size the number of items per page. Defaults to 10 if not provided.
     * @param profile an optional filter to search based on supervisor profile.
     * @return a ResponseEntity containing a paginated response of SupervisorDTOs if found,
     *         or a ResponseEntity with a 404 status if no results are found.
     */
    @GetMapping("/search")
    public ResponseEntity<PaginationResponseDTO<SupervisorDTO>> searchSupervisors(
            @RequestParam String search,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) SupervisorProfile profile) {
        Pageable pageable = PageRequest.of(page, size);

        PaginationResponseDTO<SupervisorDTO> responseDTO = supervisorService.findAllPagedByParams(pageable, search, profile);
        if (responseDTO.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Saves a new supervisor entity and returns the created supervisor data.
     *
     * @param supervisorDTO the data of the supervisor to be created
     * @return a ResponseEntity containing the created SupervisorDTO object with
     *         HTTP status 201 (Created), or HTTP status 404 (Not Found) if the creation fails
     */
    @PostMapping
    @XSSProtected
    public ResponseEntity<SupervisorDTO> save(@RequestBody SupervisorDTO supervisorDTO) {
        SupervisorDTO supervisor = supervisorService.save(supervisorDTO);
        if (supervisor == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(supervisor);
    }

    /**
     * Updates an existing supervisor with the provided data.
     *
     * @param supervisorDTO the data transfer object containing the updated supervisor details
     * @return a {@code ResponseEntity} containing the updated {@code SupervisorDTO} with
     *         HTTP status 202 (Accepted) if the update is successful, or a
     *         {@code ResponseEntity} with a 404 (Not Found) status if the supervisor does not exist
     */
    @PutMapping
    @XSSProtected
    public ResponseEntity<SupervisorDTO> update(@RequestBody SupervisorDTO supervisorDTO) {
        SupervisorDTO supervisor = supervisorService.update(supervisorDTO);
        if (supervisor == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(supervisor);
    }

    /**
     * Partially updates the details of a supervisor with the provided data.
     *
     * @param supervisorDTO the data transfer object containing the partial update details
     * @return a response entity containing the updated {@code SupervisorDTO} if the update is successful,
     *         or a {@code ResponseEntity} with a not found status if the supervisor does not exist
     */
    @PatchMapping
    @XSSProtected
    public ResponseEntity<SupervisorDTO> patch(@RequestBody SupervisorDTO supervisorDTO) {
        SupervisorDTO supervisor = supervisorService.partialUpdate(supervisorDTO);
        if (supervisor == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(supervisor);
    }

    /**
     * Deletes a supervisor identified by the provided unique identifier.
     *
     * @param id the unique identifier of the supervisor to be deleted
     * @return a {@code ResponseEntity} with HTTP 200 status if the deletion is successful,
     *         or a {@code ResponseEntity} with HTTP 404 status if the supervisor is not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<SupervisorDTO> delete(@PathVariable long id) {
        SupervisorDTO supervisorDTO = supervisorService.findOne(id);
        if (supervisorDTO == null) {
            return ResponseEntity.notFound().build();
        }
        supervisorService.delete(id);
        return ResponseEntity.ok().build();
    }
}
