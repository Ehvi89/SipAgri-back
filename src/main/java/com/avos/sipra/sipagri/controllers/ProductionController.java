package com.avos.sipra.sipagri.controllers;

import com.avos.sipra.sipagri.annotations.XSSProtected;
import com.avos.sipra.sipagri.services.cores.ProductionService;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.ProductionDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class that manages production-related endpoints.
 * This class provides RESTful APIs for handling operations such as retrieving, creating,
 * updating, searching, and deleting production records.
 */
@RestController
@RequestMapping("/api/v1/productions")
public class ProductionController {
    /**
     * An instance of ProductionService used to manage production-related operations.
     * This service is responsible for handling the business logic related to production management
     * such as creating, updating, retrieving, and deleting production data.
     * It acts as a dependency for the controller layer, facilitating interaction with the underlying services and data layers.
     */
    private final ProductionService productionService;

    /**
     * Constructor for ProductionController.
     *
     * @param productionService the production service used to handle production-related business logic
     */
    public ProductionController(ProductionService productionService) {
        this.productionService = productionService;
    }

    /**
     * Retrieves a paginated list of production records.
     *
     * @param page the page number to retrieve, default is 0
     * @param size the number of records per page, default is 10
     * @return a ResponseEntity containing the paginated list of production records,
     *         or a not found response if no records are available
     */
    @GetMapping
    public ResponseEntity<PaginationResponseDTO<ProductionDTO>> findProductions(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PaginationResponseDTO<ProductionDTO> response = productionService.findAllPaged(pageable);

        if (response.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
    /**
     * Retrieves a paginated list of productions associated with a specific supervisor.
     *
     * @param page the page number to retrieve, default value is 0.
     * @param size the size of the page to retrieve, default value is 10.
     * @param supervisorId the ID of the supervisor whose productions should be retrieved.
     * @return a ResponseEntity containing a PaginationResponseDTO of ProductionDTO objects if data is found,
     *         or a 404 Not Found response if no data is available.
     */
    @GetMapping("/by_supervisor")
    public ResponseEntity<PaginationResponseDTO<ProductionDTO>> findProductionsBySupervisor(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam Long supervisorId
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PaginationResponseDTO<ProductionDTO> response = productionService.findProductionByPlantationPlanterSupervisor(pageable, supervisorId);

        if (response.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Fetches all productions from the system.
     *
     * @return a {@code ResponseEntity} containing a list of {@code ProductionDTO} objects,
     *         or a {@code ResponseEntity} with a 404 status if no productions are found.
     */
    @GetMapping("/all")
    public ResponseEntity<List<ProductionDTO>> findAll(@RequestParam(required = false) Long supervisorId) {
        List<ProductionDTO> productions = supervisorId != null?
                productionService.findAll(supervisorId) :
                productionService.findAll();
        if (productions == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productions);
    }

    /**
     * Retrieves a production record by its ID.
     *
     * @param id the ID of the production record to retrieve
     * @return a ResponseEntity containing the ProductionDTO if found, or a 404 Not Found response if the record does not exist
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductionDTO> findById(@PathVariable long id) {
        ProductionDTO productionDTO = productionService.findOne(id);
        if (productionDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productionDTO);
    }

    /**
     * Searches productions based on a provided search query and returns a paginated response.
     *
     * @param search the search query to filter productions
     * @param page the page number to retrieve, default value is 0
     * @param size the number of records per page, default value is 10
     * @return a ResponseEntity containing a PaginationResponseDTO of ProductionDTO objects
     *         if data is found, or a 404 Not Found response if no data is available
     */
    @GetMapping("/search")
    public ResponseEntity<PaginationResponseDTO<ProductionDTO>> searchProductions(
            @RequestParam String search,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);

        PaginationResponseDTO<ProductionDTO> responseDTO = productionService.findAllPagedByParams(pageable, search);
        if (responseDTO.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Saves a new production record based on the provided ProductionDTO object.
     *
     * @param productionDTO the ProductionDTO object containing the details of the production to be saved
     * @return a ResponseEntity containing the saved ProductionDTO with a CREATED status if successful,
     *         or a NOT FOUND status if the operation fails
     */
    @PostMapping
    @XSSProtected
    public ResponseEntity<ProductionDTO> save(@RequestBody ProductionDTO productionDTO) {
        ProductionDTO production = productionService.save(productionDTO);
        if (production == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(production);
    }

    /**
     * Updates an existing production entity with the provided details```.
     java *

     */
    @PutMapping
    @XSSProtected
    public ResponseEntity<ProductionDTO> update(@RequestBody ProductionDTO productionDTO) {
        ProductionDTO production = productionService.update(productionDTO);
        if (production == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(production);
    }

    /**
     * Partially updates a production record with the provided data.
     * Only the fields present in the request body will be updated.
     *
     * @param productionDTO the DTO containing the fields to update for the production
     * @return a ResponseEntity containing the updated ProductionDTO if the update is successful,
     *         or a 404 Not Found response if the production record is not found
     */
    @PatchMapping
    @XSSProtected
    public ResponseEntity<ProductionDTO> patch(@RequestBody ProductionDTO productionDTO) {
        ProductionDTO production = productionService.partialUpdate(productionDTO);
        if (production == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(production);
    }

    /**
     * Deletes a production record identified by the given ID.
     *
     * @param id the ID of the production record to delete
     * @return a ResponseEntity indicating the outcome of the operation:
     *         - 200 OK if the production record was successfully deleted
     *         - 404 Not Found if the production record could not be found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ProductionDTO> delete(@PathVariable long id) {
        ProductionDTO productionDTO = productionService.findOne(id);
        if (productionDTO == null) {
            return ResponseEntity.notFound().build();
        }
        productionService.delete(id);
        return ResponseEntity.ok().build();
    }
}
