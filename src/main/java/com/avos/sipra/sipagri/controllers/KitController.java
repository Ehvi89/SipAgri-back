package com.avos.sipra.sipagri.controllers;

import com.avos.sipra.sipagri.annotations.XSSProtected;
import com.avos.sipra.sipagri.services.cores.KitService;
import com.avos.sipra.sipagri.services.dtos.KitDTO;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Represents a REST controller endpoint responsible for managing "Kit" resources.
 * This controller exposes various HTTP methods to perform CRUD operations
 * and supports features like pagination, searching, and partial updates.
 */
@RestController
@RequestMapping("/api/v1/kits")
public class KitController {
    /**
     * Service layer dependency used to handle business logic related to kits.
     * This component is injected into the controller and utilized to process
     * various operations such as retrieving, creating, updating, and deleting kits.
     */
    private final KitService kitService;

    /**
     * Constructs an instance of the KitController.
     *
     * @param kitService the service layer used to manage and interact with kit data
     */
    public KitController(KitService kitService) {
        this.kitService = kitService;
    }

    /**
     * Retrieves a paginated list of KitDTOs.
     *
     * @param page the page number to retrieve, defaults to 0 if not specified
     * @param size the number of items per page, defaults to 10 if not specified
     * @return a ResponseEntity containing a PaginationResponseDTO of KitDTOs if kits are found,
     *         or a ResponseEntity with a 404 status if no kits are found
     */
    @GetMapping
    public ResponseEntity<PaginationResponseDTO<KitDTO>> findAllPaged(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        final Pageable pageable = PageRequest.of(page, size);
        PaginationResponseDTO<KitDTO> response = kitService.findAllPaged(pageable);
        if (response.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a list of all kits available in the system.
     *
     * @return a ResponseEntity containing a list of KitDTO objects if kits are found,
     *         or a ResponseEntity with a 404 Not Found status if no kits are available.
     */
    @GetMapping("/all")
    public ResponseEntity<List<KitDTO>> getAll() {
        List<KitDTO> kitDTOList = kitService.findAll();
        if (kitDTOList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(kitDTOList);
    }

    /**
     * Retrieves a Kit resource by its unique identifier.
     *
     * @param id the unique identifier of the Kit to be retrieved
     * @return a ResponseEntity containing the KitDTO if the resource is found,
     *         or a ResponseEntity with a 404 Not Found status if the resource does not exist
     */
    @GetMapping("/{id}")
    public ResponseEntity<KitDTO> getById(@PathVariable Long id) {
        KitDTO kit = kitService.findOne(id);
        if (kit == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(kit);
    }

    /**
     * Searches for kits based on a given search query and provides paginated results.
     *
     * @param search the search string used to filter kits
     * @param page the page number to retrieve, defaults to 0 if not specified
     * @param size the number of items per page, defaults to 10 if not specified
     * @return a ResponseEntity containing a PaginationResponseDTO of KitDTOs if results are found,
     *         or a ResponseEntity with a 404 status if no results are found
     */
    @GetMapping("/search")
    public ResponseEntity<PaginationResponseDTO<KitDTO>> searchKits(
            @RequestParam String search,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);

        PaginationResponseDTO<KitDTO> responseDTO = kitService.findAllPagedByParams(pageable, search);
        if (responseDTO.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Handles the creation of a new Kit and saves it using the service layer.
     *
     * @param kitDTO the data transfer object containing the details of the Kit to be created
     * @return a ResponseEntity containing the created KitDTO with HTTP status 201 (Created) upon success,
     *         or a ResponseEntity with HTTP status 404 (Not Found) if the Kit could not be saved
     */
    @PostMapping
    @XSSProtected
    public ResponseEntity<KitDTO> save(@RequestBody KitDTO kitDTO) {
        KitDTO kit = kitService.save(kitDTO);
        if (kit == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(kit);
    }

    /**
     * Updates an existing Kit entity with the provided data.
     *
     * @param kitDTO the data transfer object containing the updated details of the Kit
     * @return a ResponseEntity containing the updated KitDTO if the update was successful,
     *         or a ResponseEntity with a status of not found if the Kit resource was not found
     */
    @PutMapping
    @XSSProtected
    public ResponseEntity<KitDTO> update(@RequestBody KitDTO kitDTO) {
        KitDTO kit = kitService.update(kitDTO);
        if (kit == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.accepted().body(kit);
    }

    /**
     * Updates a Kit resource partially based on the provided data.
     *
     * @param kitDTO the Kit data transfer object containing the fields to be updated
     * @return a ResponseEntity containing the updated Kit data if the update was successful,
     *         or a ResponseEntity with a not found status if the resource does not exist
     */
    @PatchMapping
    @XSSProtected
    public ResponseEntity<KitDTO> patch(@RequestBody KitDTO kitDTO) {
        KitDTO kit = kitService.partialUpdate(kitDTO);
        if (kit == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.accepted().body(kit);
    }

    /**
     * Deletes the Kit resource identified by its unique identifier.
     *
     * @param id the unique identifier of the Kit to be deleted
     * @return a ResponseEntity with HTTP status 200 (OK) if the Kit was successfully deleted,
     *         HTTP status 404 (Not Found) if the Kit could not be found,
     *         or HTTP status 500 (Internal Server Error) in case of an unexpected error
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        try {
            KitDTO kit = kitService.findOne(id);
            if (kit == null) {
                return ResponseEntity.notFound().build();
            }
            kitService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // Retourne une réponse plus spécifique
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "Erreur lors de la suppression du kit",
                            "error", e.getMessage()
                    ));
        }
    }
}
