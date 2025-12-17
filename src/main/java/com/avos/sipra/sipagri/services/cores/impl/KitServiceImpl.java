package com.avos.sipra.sipagri.services.cores.impl;

import com.avos.sipra.sipagri.entities.Kit;
import com.avos.sipra.sipagri.repositories.KitRepository;
import com.avos.sipra.sipagri.services.cores.KitService;
import com.avos.sipra.sipagri.services.cores.ProductService;
import com.avos.sipra.sipagri.services.dtos.KitDTO;
import com.avos.sipra.sipagri.services.dtos.KitProductDTO;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.ProductDTO;
import com.avos.sipra.sipagri.services.mappers.KitMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of the KitService interface, providing functionality for managing Kit entities
 * and their related data operations.
 *
 * This service handles the creation, update, retrieval, deletion, and pagination of kits,
 * as well as partial updates. It ensures consistent business logic such as calculating
 * total costs for kits and their products, and validating entity existence during operations.
 *
 * Dependencies:
 * - KitRepository: Handles persistence operations for Kit entities.
 * - KitMapper: Converts between Kit entity and KitDTO objects.
 * - ProductService: Provides product data for cost calculations.
 */
@Service
public class KitServiceImpl implements KitService {

    /**
     * Responsible for mapping between Kit entities and Kit DTOs.
     * Handles conversion logic to facilitate transfer of data between different layers
     * of the application, supporting data consistency and separation of concerns.
     */
    private final KitMapper kitMapper;

    /**
     * Repository instance for managing data access related to Kits.
     * Provides abstraction for CRUD operations and querying the Kit data source.
     */
    private final KitRepository kitRepository;
    /**
     * Service for interacting with product-related operations.
     * This variable is used to delegate calls and perform business logic
     * related to the product lifecycle management or related functionalities.
     * It is a dependency required by the {@code KitServiceImpl} class
     * to handle operations involving products within its methods.
     */
    private final ProductService productService;

    /**
     * Constructs a new KitServiceImpl with the given dependencies.
     *
     * @param kitRepository the repository for managing Kit entities
     * @param kitMapper the mapper to convert between Kit and KitDTO
     * @param productService the service for handling operations related to products
     */
    KitServiceImpl(KitRepository kitRepository, KitMapper kitMapper, ProductService productService) {
        this.kitRepository = kitRepository;
        this.kitMapper = kitMapper;
        this.productService = productService;
    }

    /**
     * Saves a KitDTO to the repository, ensuring proper cost calculation
     * for all associated KitProductDTOs and the total cost of the Kit.
     *
     * @param kitDTO the KitDTO to save, including its associated KitProductDTOs.
     *               Each KitProductDTO should have the required product details
     *               and quantities for accurate cost computations.
     * @return the KitDTO after it has been persisted in the repository,
     *         including updated total costs for both the kit and its products.
     */
    @Override
    public KitDTO save(KitDTO kitDTO) {
        // D'abord calculer les coûts sur les DTOs
        double totalKitCost = 0.0;

        if (kitDTO.getKitProducts() != null) {
            for (KitProductDTO kitProductDTO : kitDTO.getKitProducts()) {
                if (kitProductDTO.getTotalCost() == null) {
                    ProductDTO productDTO = productService.findOne(kitProductDTO.getProduct().getId());
                    double productTotalCost = productDTO.getPrice() * kitProductDTO.getQuantity();
                    kitProductDTO.setTotalCost(productTotalCost);
                }
                totalKitCost += kitProductDTO.getTotalCost();
            }
        }

        // Ensuite convertir en entité (maintenant les DTOs ont les bons totalCost)
        Kit kit = kitMapper.toEntity(kitDTO);
        kit.setTotalCost(totalKitCost);

        kit = kitRepository.save(kit);
        return kitMapper.toDTO(kit);
    }

    /**
     * Updates an existing Kit with new details provided in the given KitDTO.
     * Validates that the Kit ID is not null and that it exists in the repository before performing the update.
     *
     * @param kitDTO the KitDTO containing updated details for the kit.
     *               The KitDTO must have a non-null ID and the kit with the provided ID must exist.
     * @return the updated KitDTO after successfully saving the changes.
     * @throws IllegalArgumentException if the Kit ID is null or if no Kit with the given ID exists.
     */
    @Override
    public KitDTO update(KitDTO kitDTO) {
        if(Objects.isNull(kitDTO.getId())) {
            throw new IllegalArgumentException("Kit ID must not be null for update");
        }
        if(Boolean.FALSE.equals(existsById(kitDTO.getId()))) {
            throw new IllegalArgumentException("Kit with ID " + kitDTO.getId() + " does not exist");
        }
        return save(kitDTO); 
    }

    /**
     * Deletes a record from the repository based on the provided ID.
     *
     * @param id the unique identifier of the entity to delete
     */
    @Override
    public void delete(Long id) {
        kitRepository.deleteById(id);
    }

    /**
     * Finds a Kit entity by its ID and returns it as a KitDTO.
     * If no Kit with the specified ID exists, an IllegalArgumentException is thrown.
     *
     * @param id the unique identifier of the Kit to find
     * @return the KitDTO representation of the found Kit entity
     * @throws IllegalArgumentException if no Kit with the given ID exists
     */
    @Override
    public KitDTO findOne(Long id) {
        Optional<Kit> kitOptional = kitRepository.findById(id);
        if (kitOptional.isPresent()) {
            return kitMapper.toDTO(kitOptional.get());
        } else {
            throw new IllegalArgumentException("Kit with ID " + id + " does not exist");
        }
    }

    /**
     * Fetches all Kit entities from the repository, converts them to KitDTO objects,
     * and returns the list of KitDTOs.
     *
     * @return a list of KitDTO objects representing all Kit entities in the repository
     */
    @Override
    public List<KitDTO> findAll() {
        List<Kit> kits = kitRepository.findAll();
        List<KitDTO> kitDTOs = new ArrayList<>();
        for (Kit kit : kits) {
            kitDTOs.add(kitMapper.toDTO(kit));
        }
        return kitDTOs;
    }

    /**
     * Retrieves a paged list of KitDTO objects based on the provided pagination parameters.
     *
     * @param pageable the pagination and sorting information
     * @return a PaginationResponseDTO containing the current page, total pages, total elements,
     *         and the list of KitDTO objects for the requested page
     */
    @Override
    public PaginationResponseDTO<KitDTO> findAllPaged(Pageable pageable) {
        final Page<Kit> page = kitRepository.findAll(pageable);

        return getKitDTOPaginationResponseDTO(page);
    }

    /**
     * Retrieves a paginated list of KitDTO objects based on the provided search parameters.
     *
     * @param pageable the pagination information, including page number and size
     * @param params a string to filter kits by name containing the specified value, case-insensitive
     * @return a PaginationResponseDTO containing the current page number, total pages, total elements, and the list of KitDTO objects
     */
    @Override
    public PaginationResponseDTO<KitDTO> findAllPagedByParams(Pageable pageable, String params) {
        final Page<Kit> page = kitRepository.findKitByNameContainingIgnoreCase(pageable, params);

        return getKitDTOPaginationResponseDTO(page);
    }

    /**
     * Converts a paginated Page object containing Kit entities into a PaginationResponseDTO object
     * containing KitDTOs and pagination metadata (current page, total pages, and total elements).
     *
     * @param page the Page object containing Kit entities and pagination metadata
     * @return a PaginationResponseDTO containing the current page, total pages, total elements,
     *         and a list of KitDTO objects created from the Kit entities in the page
     */
    private PaginationResponseDTO<KitDTO> getKitDTOPaginationResponseDTO(Page<Kit> page) {
        final int currentPage = page.getNumber();
        final int totalPages = page.getTotalPages();
        final int totalElements = (int) page.getTotalElements();

        List<KitDTO> kitDTO = new ArrayList<>();
        for (Kit kit : page.getContent()) {
            kitDTO.add(kitMapper.toDTO(kit));
        }

        return new PaginationResponseDTO<>(currentPage, totalPages, totalElements, kitDTO);
    }

    /**
     * Partially updates an existing Kit entity using the values provided in the given KitDTO.
     * Validates that the Kit ID is not null and verifies the existence of the Kit in the repository.
     * If the Kit exists, updates only the non-null fields of the entity and saves the changes.
     *
     * @param kitDTO the KitDTO containing the fields for partial update.
     *               The KitDTO must have a non-null ID and correspond to an existing Kit.
     * @return the updated KitDTO after the partial update operation is successfully performed.
     * @throws IllegalArgumentException if the Kit ID is null or if no Kit with the given ID exists.
     */
    @Override
    public KitDTO partialUpdate(KitDTO kitDTO) {
        if (Objects.isNull(kitDTO.getId())) {
            throw new IllegalArgumentException("Kit ID must not be null for partial update");
        }
        if (Boolean.FALSE.equals(existsById(kitDTO.getId()))) {
            throw new IllegalArgumentException("Kit with ID " + kitDTO.getId() + " does not exist");
        }
        Optional<Kit> kitOptional = kitRepository.findById(kitDTO.getId());
        if (kitOptional.isPresent()) {
            Kit kit = kitMapper.partialUpdate(kitOptional.get(), kitDTO);
            kit = kitRepository.save(kit);
            return kitMapper.toDTO(kit);
        } else {
            throw new IllegalArgumentException("Kit with ID " + kitDTO.getId() + " does not exist");
        }
    }

    /**
     * Checks if an entity with the given ID exists in the repository.
     *
     * @param id the unique identifier of the entity to check for existence
     * @return true if an entity with the given ID exists, false otherwise
     */
    @Override
    public Boolean existsById(Long id) {
        return kitRepository.existsById(id);
    }
}
