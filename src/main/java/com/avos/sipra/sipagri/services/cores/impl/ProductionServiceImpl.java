package com.avos.sipra.sipagri.services.cores.impl;

import com.avos.sipra.sipagri.entities.Plantation;
import com.avos.sipra.sipagri.entities.Production;
import com.avos.sipra.sipagri.repositories.PlantationRepository;
import com.avos.sipra.sipagri.repositories.ProductionRepository;
import com.avos.sipra.sipagri.services.cores.CalculationService;
import com.avos.sipra.sipagri.services.cores.ProductionService;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.ProductionDTO;
import com.avos.sipra.sipagri.services.mappers.ProductionMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of the ProductionService interface for handling operations
 * related to production entities. This service manages CRUD operations,
 * business logic, calculations, and interactions with repositories and mappers.
 * <p>
 * Responsibilities:
 * - Save, update, and delete production records.
 * - Retrieve a single production record or all records (paginated or non-paginated).
 * - Perform partial updates to production entities.
 * - Apply business-specific calculations through the CalculationService.
 * - Handle relationships between Production and Plantation entities.
 */
@Service
public class ProductionServiceImpl implements ProductionService{
    private final ProductionMapper productionMapper;
    private final ProductionRepository productionRepository;
    private final CalculationService calculationService;
    private final PlantationRepository plantationRepository;

   public ProductionServiceImpl(ProductionMapper productionMapper,
                                  ProductionRepository productionRepository,
                                 CalculationService calculationService, PlantationRepository plantationRepository) {
        this.productionMapper = productionMapper;
        this.productionRepository = productionRepository;
        this.calculationService = calculationService;
        this.plantationRepository = plantationRepository;
    }


    /**
     * Saves a production record by performing necessary calculations, converting the DTO to an entity,
     * persisting it in the database, and maintaining the relationship with its associated plantation.
     *
     * @param productionDTO the production data transfer object containing information to be saved
     * @return the saved production data transfer object with updated and calculated values
     */
    @Override
    public ProductionDTO save(ProductionDTO productionDTO) {
        productionDTO = calculationService.calculateProductionValues(productionDTO);

        Production production = productionMapper.toEntity(productionDTO);
        production = productionRepository.save(production);

        // Calcul mustBePaid après sauvegarde si on a plantationId
        if (productionDTO.getPlantationId() != null) {
            ProductionDTO finalDTO = productionMapper.toDTO(production);
            finalDTO = calculationService.calculateMustBePaid(finalDTO, productionDTO.getPlantationId());

            if (!Objects.equals(finalDTO.getMustBePaid(), production.getMustBePaid())) {
                production.setMustBePaid(finalDTO.getMustBePaid());
                production = productionRepository.save(production);
            }

            Plantation plantation = plantationRepository.getReferenceById(production.getPlantation().getId());
            plantation.getProductions().add(production);
            plantationRepository.save(plantation);
        }

        return productionMapper.toDTO(production);
    }

    /**
     * Updates an existing production entity with the provided production data.
     * Validates that the production ID is not null and the record exists.
     * Recalculates associated values before saving the updated entity.
     *
     * @param productionDTO the production data transfer object containing updated data
     * @return the updated production data transfer object after persistence
     * @throws IllegalArgumentException if the production ID is null or the production does not exist
     */
    @Override
    public ProductionDTO update(ProductionDTO productionDTO) {
        if (Objects.isNull(productionDTO.getId())) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (Boolean.FALSE.equals(existsById(productionDTO.getId()))) {
            throw new IllegalArgumentException("Production does not exist");
        }

        // Recalculer les valeurs basées sur productionInKg
        productionDTO = calculationService.calculateProductionValues(productionDTO);

        // Conversion en entité et sauvegarde
        Production production = productionMapper.toEntity(productionDTO);
        production = productionRepository.save(production);

        return productionMapper.toDTO(production);
    }

    /**
     * Deletes a production record from the database based on its unique identifier.
     *
     * @param id the unique identifier of the production record to be deleted
     */
    @Override
    public void delete(Long id) {
        productionRepository.deleteById(id);
    }

    /**
     * Retrieves a single production record by its ID.
     * Throws an exception if the production with the given ID does not exist.
     *
     * @param id the unique identifier of the production to be retrieved
     * @return the ProductionDTO representing the retrieved production
     * @throws IllegalArgumentException if no production exists with the given ID
     */
    @Override
    public ProductionDTO findOne(Long id) {
        Optional<Production> productionOptional = productionRepository.findById(id);
        if (productionOptional.isPresent()) {
            return productionMapper.toDTO(productionOptional.get());
        }
        throw new IllegalArgumentException("Production with ID " + id + " does not exist");
    }

    /**
     * Retrieves all production records from the repository, maps them to ProductionDTO objects,
     * and returns the list of mapped DTOs.
     *
     * @return a list of ProductionDTO objects representing all production records.
     */
    @Override
    public List<ProductionDTO> findAll() {
        List<Production> productions = productionRepository.findAll();
        List<ProductionDTO> productionDTOS = new ArrayList<>();
        for (Production production : productions) {
            productionDTOS.add(productionMapper.toDTO(production));
        }
        return productionDTOS;
    }

    /**
     * Retrieves a paginated list of ProductionDTO objects based on the given pageable information.
     *
     * @param pageable the paging and sorting information to apply to the query.
     * @return a PaginationResponseDTO containing the current page, total pages, total elements,
     *         and the list of ProductionDTO objects for the current page.
     */
    @Override
    public PaginationResponseDTO<ProductionDTO> findAllPaged(Pageable pageable) {
        final Page<Production> page = productionRepository.findAll(pageable);

        return getProductionDTOPaginationResponseDTO(page);
    }

    /**
     * Retrieves a paginated list of productions based on the given search parameters.
     * The search parameters are matched either against the plantation name or the production in kilograms.
     *
     * @param pageable the pagination and sorting information
     * @param params the search criteria to filter productions, applied to plantation name or production in kilograms
     * @return a PaginationResponseDTO containing the paginated list of ProductionDTOs and metadata
     */
    @Override
    public PaginationResponseDTO<ProductionDTO> findAllPagedByParams(Pageable pageable, String params) {
        final Page<Production> page = productionRepository.findProductionsByPlantation_NameOrProductionInKg(pageable, params, params);

        return getProductionDTOPaginationResponseDTO(page);
    }

    /**
     * Converts a paginated list of Production entities into a PaginationResponseDTO containing a
     * paginated list of ProductionDTOs along with metadata about the pagination.
     *
     * @param page the paginated list of Production entities retrieved from the database
     * @return a PaginationResponseDTO containing the current page, total pages, total elements,
     *         and a list of ProductionDTOs corresponding to the content of the given page
     */
    private PaginationResponseDTO<ProductionDTO> getProductionDTOPaginationResponseDTO(Page<Production> page) {
        final int currentPage = page.getNumber();
        final int totalPages = page.getTotalPages();
        final int totalElements = (int) page.getTotalElements();

        List<ProductionDTO> productionDTOS = new ArrayList<>();
        for (Production production : page.getContent()) {
            productionDTOS.add(productionMapper.toDTO(production));
        }

        return new PaginationResponseDTO<>(currentPage, totalPages, totalElements, productionDTOS);
    }

    /**
     * Partially updates a production entity based on the provided ProductionDTO.
     * This method recalculates production values if necessary and updates
     * only specified fields, leaving others unchanged.
     *
     * @param productionDTO The data transfer object containing the partial updates
     *                       and the ID of the production entity to be updated.
     * @return The updated ProductionDTO reflecting the applied changes.
     * @throws IllegalArgumentException if the provided ID is null, if the production
     *                                  entity does not exist, or if its ID is invalid.
     */
    @Override
    public ProductionDTO partialUpdate(ProductionDTO productionDTO) {
        if (Objects.isNull(productionDTO.getId())) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (Boolean.FALSE.equals(existsById(productionDTO.getId()))) {
            throw new IllegalArgumentException("Production does not exist");
        }

        Optional<Production> productionOptional = productionRepository.findById(productionDTO.getId());
        if (productionOptional.isPresent()) {
            Production existingProduction = productionOptional.get();

            // Recalculer les valeurs si productionInKg a changé
            if (productionDTO.getProductionInKg() != null) {
                productionDTO = calculationService.calculateProductionValues(productionDTO);
            }

            // Effectuer la mise à jour partielle
            Production production = productionMapper.partialUpdate(existingProduction, productionDTO);
            production = productionRepository.save(production);
            return productionMapper.toDTO(production);
        }
        throw new IllegalArgumentException("Production with ID " + productionDTO.getId() + " does not exist");
    }

    /**
     * Checks whether a production entity exists by its unique identifier.
     *
     * @param id the unique identifier of the production entity to check
     * @return true if a production entity with the given identifier exists, false otherwise
     */
    @Override
    public Boolean existsById(Long id) {
        return productionRepository.existsById(id);
    }
}