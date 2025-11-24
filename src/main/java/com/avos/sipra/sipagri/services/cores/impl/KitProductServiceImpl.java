package com.avos.sipra.sipagri.services.cores.impl;

import com.avos.sipra.sipagri.entities.KitProduct;
import com.avos.sipra.sipagri.repositories.KitProductRepository;
import com.avos.sipra.sipagri.services.cores.KitProductService;
import com.avos.sipra.sipagri.services.cores.ProductService;
import com.avos.sipra.sipagri.services.dtos.KitProductDTO;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.ProductDTO;
import com.avos.sipra.sipagri.services.mappers.KitProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service implementation for managing KitProduct entities.
 * This service provides methods for creating, updating, deleting, and retrieving KitProduct data
 * and includes support for pagination and partial updates.
 */
@Slf4j
@Service
public class KitProductServiceImpl implements KitProductService{

    /**
     * Mapper used for converting between `KitProduct` entities and their corresponding data transfer objects (DTOs).
     * This field is utilized to facilitate transformations during service layer operations in the `KitProductServiceImpl`.
     * It serves to ensure the proper mapping of domain objects to DTOs and vice versa.
     */
    private final KitProductMapper kitProductMapper;
    /**
     * Repository interface for managing persistence operations related to kit products.
     * This field is used to interact with the underlying data source for CRUD operations
     * and custom queries involving KitProduct entities.
     */
    private final KitProductRepository kitProductRepository;
    /**
     * Provides functionality to interact with and manage product-related operations.
     * This service is utilized by the KitProductServiceImpl to perform actions such as
     * product retrieval, product validation, or any other operations related to products.
     */
    private final ProductService productService;

    /**
     * Constructs an instance of KitProductServiceImpl with the required dependencies.
     *
     * @param kitProductMapper the mapper responsible for converting between KitProduct entities and DTOs
     * @param kitProductRepository the repository for managing KitProduct entity persistence
     * @param productService the service for handling product-related operations
     */
    public KitProductServiceImpl(KitProductMapper kitProductMapper, KitProductRepository kitProductRepository, ProductService productService) {
        this.kitProductMapper = kitProductMapper;
        this.kitProductRepository = kitProductRepository;
        this.productService = productService;
    }

    /**
     * Saves a KitProduct entity and calculates the total cost if necessary before persisting.
     *
     * @param kitProductDTO the data transfer object containing information about the KitProduct to be saved
     * @return the saved KitProduct as a data transfer object
     * @throws IllegalArgumentException if the product or quantity is null, making total cost calculation impossible
     */
    @Override
    public KitProductDTO save(KitProductDTO kitProductDTO) {
        KitProduct kitProduct = kitProductMapper.toEntity(kitProductDTO);

        // Calculer le totalCost si nécessaire
        if (kitProduct.getTotalCost() == null) {
            ProductDTO product = productService.findOne(kitProduct.getProduct().getId());
            if (product != null && kitProduct.getQuantity() != null) {
                double totalCost = kitProduct.getQuantity() * product.getPrice();
                kitProduct.setTotalCost(totalCost);
                kitProductDTO.setTotalCost(totalCost);
            } else {
                log.error("Cannot calculate totalCost - product: {}, quantity: {}",
                        product, kitProduct.getQuantity());
                throw new IllegalArgumentException("Product or quantity is null");
            }
        }

        log.debug("Saving KitProduct - quantity: {}, productId: {}, totalCost: {}",
                kitProduct.getQuantity(),
                kitProduct.getProduct().getId(),
                kitProduct.getTotalCost());

        kitProduct = kitProductRepository.save(kitProduct);
        return kitProductMapper.toDTO(kitProduct);
    }

    /**
     * Updates an existing KitProduct entity with the provided data.
     *
     * @param kitProductDTO the data transfer object representing the KitProduct to be updated; must not be null
     * @return the updated KitProductDTO after successful persistence
     * @throws IllegalArgumentException if the provided kitProductDTO is null
     * @throws NullPointerException if the KitProduct with the given ID does not exist
     */
    @Override
    public KitProductDTO update(KitProductDTO kitProductDTO) {
        if (Objects.isNull(kitProductDTO)) {throw new IllegalArgumentException("Id must not be null");}
        if (Boolean.FALSE.equals(existsById(kitProductDTO.getId()))) {throw new NullPointerException("kitProductDTO does not exist");}
        return save(kitProductDTO);
    }

    /**
     * Deletes a Kit product entry from the repository by its unique identifier.
     *
     * @param id the unique identifier of the Kit product to be deleted
     */
    @Override
    public void delete(Long id) {
        kitProductRepository.deleteById(id);
    }

    /**
     * Retrieves a single KitProductDTO by its unique identifier.
     *
     * @param id the unique identifier of the KitProduct to retrieve
     * @return the KitProductDTO corresponding to the given identifier, or null if not found
     */
    @Override
    public KitProductDTO findOne(Long id) {
        Optional<KitProduct> optional = kitProductRepository.findById(id);
        return optional.map(kitProductMapper::toDTO).orElse(null);
    }

    /**
     * Retrieves all KitProduct entities, converts them to KitProductDTO objects, and returns the list.
     *
     * @return a list of KitProductDTO objects representing all KitProduct entities found in the system.
     */
    @Override
    public List<KitProductDTO> findAll() {
        List<KitProduct> kitProducts = kitProductRepository.findAll();
        List<KitProductDTO> kitProductDTO = new ArrayList<>();
        for (KitProduct kitProduct : kitProducts) {
            kitProductDTO.add(kitProductMapper.toDTO(kitProduct));
        }
        return kitProductDTO;
    }

    /**
     * Retrieves a paginated list of KitProductDTO objects from the repository.
     *
     * @param pageable the pagination and sorting information
     * @return a PaginationResponseDTO containing the paginated list of KitProductDTOs along with pagination details
     */
    @Override
    public PaginationResponseDTO<KitProductDTO> findAllPaged(Pageable  pageable) {
        final Page<KitProduct> page = kitProductRepository.findAll(pageable);

        return getKitProductDTOPaginationResponseDTO(page);
    }

    /**
     * Retrieves a paginated list of KitProductDTOs based on the provided search parameters.
     *
     * @param pageable the pagination and sorting information
     * @param params the search parameter used to filter KitProduct entities by product name or quantity
     * @return a PaginationResponseDTO containing the paginated list of KitProductDTOs and pagination details
     */
    @Override
    public PaginationResponseDTO<KitProductDTO> findAllPagedByParams(Pageable pageable, String params) {
        final Page<KitProduct> page = kitProductRepository.findKitProductByProduct_NameOrQuantity(pageable, params, params);

        return getKitProductDTOPaginationResponseDTO(page);
    }

    /**
     * Converts a paginated list of KitProduct entities into a PaginationResponseDTO of KitProductDTOs.
     *
     * @param page the Page object containing KitProduct entities to be converted into DTOs
     * @return a PaginationResponseDTO containing the current page, total pages, total elements,
     *         and the list of KitProductDTOs
     */
    private PaginationResponseDTO<KitProductDTO> getKitProductDTOPaginationResponseDTO(Page<KitProduct> page) {
        final int currentPage = page.getNumber();
        final int totalPages = page.getTotalPages();
        final int totalElements = (int) page.getTotalElements();

        List<KitProductDTO> kitProductDTO = new ArrayList<>();
        for (KitProduct kitProduct : page.getContent()) {
            kitProductDTO.add(kitProductMapper.toDTO(kitProduct));
        }

        return new PaginationResponseDTO<>(currentPage, totalPages, totalElements, kitProductDTO);
    }

    /**
     * Partially updates a KitProduct entity using the data provided in the KitProductDTO.
     * If the entity exists, it merges the provided values, saves the updated entity,
     * and returns the updated DTO.
     *
     * @param kitProductDTO the data transfer object containing the values to update; must not be null
     * @return the updated KitProductDTO after successful persistence
     * @throws IllegalArgumentException if the provided kitProductDTO is null
     * @throws NullPointerException if the KitProduct with the given ID does not exist
     */
    @Override
    public KitProductDTO partialUpdate(KitProductDTO kitProductDTO) {
        if (Objects.isNull(kitProductDTO)) {throw new IllegalArgumentException("Id must not be null");}
        if (Boolean.FALSE.equals(existsById(kitProductDTO.getId()))) {throw new NullPointerException("kitProductDTO does not exist");}
        Optional<KitProduct> optional = kitProductRepository.findById(kitProductDTO.getId());
        if (optional.isPresent()) {
            KitProduct kitProduct = kitProductMapper.partialUpdate(optional.get(), kitProductDTO);
            kitProduct = kitProductRepository.save(kitProduct);
            return kitProductMapper.toDTO(kitProduct);
        }
        throw new NullPointerException("kitProductDTO does not exist");
    }

    /**
     * Checks if an entity with the given unique identifier exists in the repository.
     *
     * @param id the unique identifier of the entity to check for existence
     * @return true if an entity with the given identifier exists, false otherwise
     */
    @Override
    public Boolean existsById(Long id) {
        return kitProductRepository.existsById(id);
    }
}
