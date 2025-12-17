package com.avos.sipra.sipagri.controllers;

import com.avos.sipra.sipagri.annotations.XSSProtected;
import com.avos.sipra.sipagri.services.cores.ParamsService;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.ParamsDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller that manages CRUD operations and retrieval of parameters.
 * Provides endpoints for fetching, creating, updating, partially updating,
 * and deleting parameter entities.
 */
@RestController
@RequestMapping("/api/v1/params")
public class ParamsController {
    /**
     * Service responsible for handling operations related to parameters within the application.
     * It provides business logic and processing for managing parameter data.
     */
    private final ParamsService planterService;

    /**
     * Constructs a new ParamsController with the specified ParamsService.
     *
     * @param planterService the service used to manage parameters
     */
    public ParamsController(ParamsService planterService) {
        this.planterService = planterService;
    }

    /**
     * Handles HTTP GET requests to retrieve a paginated list of ParamsDTO objects.
     *
     * @param page the page number to retrieve, defaults to 0 if not provided
     * @param size the number of items per page, defaults to 10 if not provided
     * @return a ResponseEntity containing a PaginationResponseDTO with a paginated list of ParamsDTO objects,
     *         or a ResponseEntity with a 404 status if no data is found
     */
    @GetMapping
    public ResponseEntity<PaginationResponseDTO<ParamsDTO>> getParams(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size)
    {
        Pageable pageable = PageRequest.of(page, size);

        PaginationResponseDTO<ParamsDTO> responseDTO = planterService.findAllPaged(pageable);
        if (responseDTO.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Handles HTTP GET requests to retrieve a list of all ParamsDTO objects.
     * Returns the list if the data exists, or a 404 Not Found response if no data is found.
     *
     * @return a ResponseEntity containing a list of ParamsDTO objects with a 200 OK status if data is found,
     *         or a 404 Not Found response if the list is empty.
     */
    @GetMapping("/all")
    public ResponseEntity<List<ParamsDTO>> getAllParams() {
        List<ParamsDTO> paramsDTO = planterService.findAll();
        if (paramsDTO.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(paramsDTO);
    }

    /**
     * Retrieves a ParamsDTO object by its ID.
     *
     * @param id the unique identifier of the ParamsDTO to be retrieved
     * @return a ResponseEntity containing the ParamsDTO if found,
     *         or a ResponseEntity with a 404 status if the ParamsDTO is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ParamsDTO> getParamsById(@PathVariable Long id) {
        ParamsDTO paramsDTO = planterService.findOne(id);
        if (paramsDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(paramsDTO);
    }
    
    /**
     * Retrieves a parameter object based on its name.
     *
     * @param name the name of the parameter to be retrieved
     * @return a ResponseEntity containing the ParamsDTO if found, or a not found response otherwise
     */
    @GetMapping("/name")
    public ResponseEntity<ParamsDTO> getParamsByName(@RequestParam String name) {
        ParamsDTO paramsDTO = planterService.findByName(name);
        if (paramsDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(paramsDTO);
    }

    /**
     * Creates a new set of parameters and saves it to the database.
     *
     * @param paramsDTO the parameters data transfer object containing the details to be saved
     * @return a ResponseEntity containing the created ParamsDTO object with a status of CREATED,
     *         or a ResponseEntity with a status of NOT_FOUND if the save operation fails
     */
    @PostMapping
    @XSSProtected
    public ResponseEntity<ParamsDTO> createParams(@RequestBody ParamsDTO paramsDTO) {
        ParamsDTO planter = planterService.save(paramsDTO);
        if (planter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(planter);
    }

    /**
     * Updates an existing ParamsDTO object based on the provided request body.
     * If the update is successful, the updated object is returned with a status of ACCEPTED.
     * If the object does not exist, a 404 Not Found response is returned.
     *
     * @param paramsDTO the ParamsDTO object containing updated parameter values
     * @return a ResponseEntity containing the updated ParamsDTO object with a status of ACCEPTED,
     *         or a 404 Not Found response if the object to be updated does not exist
     */
    @PutMapping
    @XSSProtected
    public ResponseEntity<ParamsDTO> updateParams(@RequestBody ParamsDTO paramsDTO) {
        ParamsDTO planter = planterService.update(paramsDTO);
        if (planter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(planter);
    }

    /**
     * Partially updates an existing ParamsDTO object with the provided values.
     * If the object exists and is successfully updated, the updated object is returned with a status of ACCEPTED.
     * If the object does not exist, a 404 Not Found response is returned.
     *
     * @param paramsDTO the ParamsDTO object containing updated parameter values
     * @return a ResponseEntity containing the updated ParamsDTO object with a status of ACCEPTED,
     *         or a 404 Not Found response if the object to be updated does not exist
     */
    @PatchMapping
    @XSSProtected
    public ResponseEntity<ParamsDTO> patchParams(@RequestBody ParamsDTO paramsDTO) {
        ParamsDTO planter = planterService.partialUpdate(paramsDTO);
        if (planter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(planter);
    }

    /**
     * Deletes a specified ParamsDTO object by its unique identifier.
     * If the object does not exist, it returns a 404 Not Found response.
     * If the object is successfully deleted, it returns a 200 OK response.
     *
     * @param id the unique identifier of the ParamsDTO to be deleted
     * @return a ResponseEntity with a 200 OK status if the ParamsDTO is successfully deleted,
     *         or a 404 Not Found status if the ParamsDTO does not exist
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ParamsDTO> deleteParams(@PathVariable Long id) {
        ParamsDTO paramsDTO = planterService.findOne(id);
        if (paramsDTO == null) {
            return ResponseEntity.notFound().build();
        }
        planterService.delete(id);
        return ResponseEntity.ok().build();
    }
}
