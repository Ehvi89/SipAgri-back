package com.avos.sipra.sipagri.controllers;

import com.avos.sipra.sipagri.annotations.XSSProtected;
import com.avos.sipra.sipagri.services.cores.PlantationService;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.PlantationDTO;
import com.avos.sipra.sipagri.enums.PlantationStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/plantations")
public class PlantationController {

    /**
     * Service dependency for managing plantation operations. This variable is
     * a final instance of PlantationService, which provides business logic
     * and operations related to plantations. It is initialized via dependency injection
     * and used across the PlantationController to handle plantation-related functionalities.
     */
    private final PlantationService plantationService;

    /**
     * Constructor for the PlantationController class, which initializes the controller
     * with the provided PlantationService instance for handling plantation-related operations.
     *
     * @param plantationService the service instance used to perform various operations
     *                          related to plantations
     */
    public PlantationController(PlantationService plantationService) {
        this.plantationService = plantationService;
    }

    /**
     * Retrieves a paginated list of plantations.
     *
     * @param page the page number to be retrieved, default is 0
     * @param size the number of items per page, default is 10
     * @return a ResponseEntity containing the paginated response of PlantationDTO objects
     */
    @GetMapping
    public ResponseEntity<PaginationResponseDTO<PlantationDTO>> getAll(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PaginationResponseDTO<PlantationDTO> response = plantationService.findAllPaged(pageable);
        if (response.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a specific plantation by its unique identifier.
     *
     * @param id the unique identifier of the plantation to retrieve
     * @return a ResponseEntity containing the PlantationDTO object if found,
     *         or a ResponseEntity with a not-found status if the plantation does not exist
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlantationDTO> getPlantation(@PathVariable Long id){
        PlantationDTO plantationDTO = plantationService.findOne(id);
        if(plantationDTO == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(plantationDTO);
    }

    /**
     * Retrieves a list of all plantations.
     *
     * @return a ResponseEntity containing a list of PlantationDTO objects if data is found,
     *         or a ResponseEntity with a not-found status if no data is available.
     */
    @GetMapping("/all")
    public ResponseEntity<List<PlantationDTO>> getAllPlantations(){
        List<PlantationDTO> plantationDTOs = plantationService.findAll();
        if(plantationDTOs == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(plantationDTOs);
    }

    /**
     * Retrieves a paginated list of plantations managed by a specific supervisor.
     *
     * @param page the page number to be retrieved, default is 0
     * @param size the number of items per page, default is 10
     * @param supervisorId the ID of the supervisor whose plantations are to be retrieved
     * @return a ResponseEntity containing the paginated response of PlantationDTO objects,
     *         or a ResponseEntity with a not-found status if no data is available
     */
    @GetMapping("/by_supervisor")
    public ResponseEntity<PaginationResponseDTO<PlantationDTO>> getAll(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam Long supervisorId
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PaginationResponseDTO<PlantationDTO> response = plantationService.findAllPagedPlantationByPlanterSupervisor(pageable, supervisorId);
        if (response.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Searches for plantations based on the provided search keyword and criteria,
     * returning a paginated response of the matching results.
     *
     * @param search the primary keyword or term to search for plantations
     * @param criteria an optional parameter defining the search criteria,
     *                 such as "status", "village", or "params" (default is "params")
     * @param page the page number to retrieve in the paginated response, default is 0
     * @param size the number of items per page for the paginated response, default is 10
     * @return a {@code ResponseEntity} containing a {@code PaginationResponseDTO<PlantationDTO>}
     *         with the search results, or a 404 status if no data is found
     */
    @GetMapping("/search")
    public ResponseEntity<PaginationResponseDTO<PlantationDTO>> searchPlantations(
            @RequestParam String search,
            @RequestParam(required = false, defaultValue = "params") String criteria,
            @RequestParam(required = false) Long supervisorId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        PaginationResponseDTO<PlantationDTO> responseDTO;

        if ("status".equalsIgnoreCase(criteria) && !search.equalsIgnoreCase("")) {
            PlantationStatus statusEnum;
            statusEnum = PlantationStatus.valueOf(search.toUpperCase());
            responseDTO = supervisorId != null ?
                    plantationService.findAllPagedByStatus(pageable, statusEnum, supervisorId) :
                    plantationService.findAllPagedByStatus(pageable, statusEnum);

        } else if ("village".equalsIgnoreCase(criteria)) {
            responseDTO = supervisorId != null ?
                    plantationService.findAllPagedByVillage(pageable, search, supervisorId) :
                    plantationService.findAllPagedByVillage(pageable, search);
        } else {
            responseDTO = supervisorId != null ?
                    plantationService.findAllPagedByParams(pageable, search, supervisorId) :
                    plantationService.findAllPagedByParams(pageable, search);
        }

        if (responseDTO == null || responseDTO.getData() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Creates a new plantation based on the provided data.
     *
     * @param dto the PlantationDTO containing the details of the plantation to be created
     * @return ResponseEntity containing the created PlantationDTO with HTTP status 201 (Created),
     *         or HTTP status 404 (Not Found) if the plantation creation fails
     */
    @PostMapping
    @XSSProtected
    public ResponseEntity<PlantationDTO> createPlantation(@RequestBody PlantationDTO dto){
        PlantationDTO plantationDTO = plantationService.save(dto);
        if(plantationDTO == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(plantationDTO);
    }

    /**
     * Updates an existing plantation with the provided data.
     *
     * @param dto the {@code PlantationDTO} object containing the updated plantation details
     * @return a {@code ResponseEntity} containing the updated {@code PlantationDTO} if the update is successful,
     *         or a {@code ResponseEntity} with a "Not Found" status if the plantation does not exist
     */
    @PutMapping
    @XSSProtected
    public ResponseEntity<PlantationDTO> updatePlantation(@RequestBody PlantationDTO dto){
        PlantationDTO plantationDTO = plantationService.update(dto);
        if(plantationDTO == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.accepted().body(plantationDTO);
    }

    /**
     * Partially updates an existing plantation with the provided data.
     *
     * @param dto the {@code PlantationDTO} object containing the fields to update in the plantation
     * @return a {@code ResponseEntity} containing the updated {@code PlantationDTO} if the update is successful,
     *         or a {@code ResponseEntity} with a "Not Found" status if the plantation does not exist
     */
    @PatchMapping
    public ResponseEntity<PlantationDTO> patchPlantation(@RequestBody PlantationDTO dto){
        PlantationDTO plantationDTO = plantationService.partialUpdate(dto);
        if(plantationDTO == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.accepted().body(plantationDTO);
    }

    /**
     * Deletes a plantation identified by its unique ID.
     * If the plantation does not exist, a "Not Found" response will be returned.
     *
     * @param id the unique identifier of the plantation to be deleted
     * @return a {@code ResponseEntity} with HTTP status 200 if the deletion is successful,
     *         or HTTP status 404 if the plantation is not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<PlantationDTO> deletePlantation(@PathVariable Long id){
        PlantationDTO plantationDTO = plantationService.findOne(id);
        if(plantationDTO == null){
            return ResponseEntity.notFound().build();
        }
        plantationService.delete(id);
        return ResponseEntity.ok().build();
    }
}
