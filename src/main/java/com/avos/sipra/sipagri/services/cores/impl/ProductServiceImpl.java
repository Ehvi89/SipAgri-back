package com.avos.sipra.sipagri.services.cores.impl;

import com.avos.sipra.sipagri.entities.Product;
import com.avos.sipra.sipagri.repositories.ProductRepository;
import com.avos.sipra.sipagri.services.cores.ProductService;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.ProductDTO;
import com.avos.sipra.sipagri.services.mappers.ProductMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service implementation for managing products.
 * This class provides methods to create, update, delete, retrieve individual products,
 * and perform batch operations like finding all products or paginated retrieval.
 * Products are mapped between entities and DTOs using a ProductMapper.
 * The underlying persistence is managed by a ProductRepository.
 */
@Service
public class ProductServiceImpl implements ProductService {
    /**
     * Mapper component used to convert between Product entities and ProductDTOs.
     * Provides a mechanism for mapping object structures between layers of the application.
     * Typically used in service methods to facilitate transformation logic.
     */
    private final ProductMapper productMapper;
    /**
     * The ProductRepository instance used for interacting with the persistence layer for
     * product-related operations.
     * <p>
     * This repository provides the necessary database access methods for managing product data,
     * allowing CRUD operations and other complex queries to be executed on the product entities.
     * <p>
     * It is injected into the ProductServiceImpl class to facilitate database operations
     * required by the business logic implemented within the service.
     */
    private final ProductRepository productRepository;

    /**
     * Constructs a new instance of ProductServiceImpl.
     *
     * @param productMapper       the mapper responsible for transforming domain objects to DTOs and vice versa
     * @param productRepository   the repository interface for managing product persistence operations
     */
    public ProductServiceImpl(ProductMapper productMapper, ProductRepository productRepository) {
        this.productMapper = productMapper;
        this.productRepository = productRepository;
    }

    /**
     * Saves a new product or updates an existing product in the repository.
     * Transforms the provided ProductDTO into a Product entity, persists it,
     * and then converts it back to a ProductDTO.
     *
     * @param productDTO the product DTO containing the information to be saved
     * @return the saved ProductDTO after the save operation
     */
    @Override
    public ProductDTO save(ProductDTO productDTO) {
        Product product = productMapper.toEntity(productDTO);
        product = productRepository.save(product);
        return productMapper.toDTO(product);
    }

    /**
     * Updates an existing product based on the provided ProductDTO.
     * Throws an exception if the product ID is null or does not exist in the system.
     *
     * @param productDTO the product DTO containing updated product details
     * @return the updated product DTO after the update operation
     * @throws IllegalArgumentException if the product ID is null or the product does not exist
     */
    @Override
    public ProductDTO update(ProductDTO productDTO) {
        if(Objects.isNull(productDTO.getId())) {throw new IllegalArgumentException("Id cannot be null");}
        if(Boolean.FALSE.equals(existsById(productDTO.getId()))) {throw new IllegalArgumentException("Product not found");}
        return save(productDTO);
    }

    /**
     * Deletes a product from the repository by its ID.
     *
     * @param id the ID of the product to be deleted
     */
    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    /**
     * Retrieves a single ProductDTO by its unique identifier.
     *
     * @param id the unique identifier of the product to retrieve
     * @return a ProductDTO representing the product if found, or null if no product exists with the given id
     */
    @Override
    public ProductDTO findOne(Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.map(productMapper::toDTO).orElse(null);
    }

    /**
     * Retrieves all products from the repository, converts them into ProductDTO objects using the productMapper,
     * and returns a list of these ProductDTOs.
     *
     * @return a List of ProductDTO objects representing all available products
     */
    @Override
    public List<ProductDTO> findAll() {
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOS = new ArrayList<>();
        for(Product product : products) {
            productDTOS.add(productMapper.toDTO(product));
        }
        return productDTOS;
    }

    /**
     * Retrieves a paginated list of products as a DTO response.
     *
     * @param pageable the pagination information, including page number, size, and sorting options
     * @return a {@link PaginationResponseDTO} containing the current page, total pages, total elements,
     *         and a list of {@link ProductDTO} instances
     */
    @Override
    public PaginationResponseDTO<ProductDTO> findAllPaged(Pageable pageable) {
        final Page<Product> page = productRepository.findAll(pageable);

        return getProductDTOPaginationResponseDTO(page);
    }

    /**
     * Retrieves a paginated list of products filtered by specified parameters.
     *
     * @param pageable the pagination information including page number, page size, and sorting details
     * @param params the filtering criteria, such as product name for searching
     * @return a PaginationResponseDTO containing a paginated list of ProductDTO objects
     */
    @Override
    public PaginationResponseDTO<ProductDTO> findAllPagedByParams(Pageable pageable, String params) {
        final Page<Product> page = productRepository.findProductsByNameContainingIgnoreCase(pageable, params);

        return getProductDTOPaginationResponseDTO(page);
    }

    /**
     * Converts a Page of Product entities into a PaginationResponseDTO containing ProductDTOs.
     *
     * @param page the Page of Product entities, containing paginated results including content,
     *             current page number, total pages, and total elements
     * @return a PaginationResponseDTO containing the current page, total pages, total elements,
     *         and a list of ProductDTO instances
     */
    private PaginationResponseDTO<ProductDTO> getProductDTOPaginationResponseDTO(Page<Product> page) {
        final int currentPage = page.getNumber();
        final int totalPages = page.getTotalPages();
        final int totalElements = (int) page.getTotalElements();

        List<ProductDTO> productDTOS = new ArrayList<>();
        for(Product product : page.getContent()) {
            productDTOS.add(productMapper.toDTO(product));
        }

        return new PaginationResponseDTO<>(currentPage, totalPages, totalElements, productDTOS);
    }

    /**
     * Partially updates an existing product based on the provided ProductDTO.
     * The method verifies that the product ID is not null and that the product exists in the repository.
     * If the product is found, updates are applied to the product entity, and the updated entity is saved and converted back to a DTO.
     *
     * @param productDTO the product DTO containing partial updates and the product ID
     * @return the updated ProductDTO after the save operation, or null if the product does not exist
     * @throws IllegalArgumentException if the product ID is null or the product cannot be found
     */
    @Override
    public ProductDTO partialUpdate(ProductDTO productDTO) {
        if(Objects.isNull(productDTO.getId())) {throw new IllegalArgumentException("Id cannot be null");}
        if(Boolean.FALSE.equals(existsById(productDTO.getId()))) {throw new IllegalArgumentException("Product not found");}
        Optional<Product> productOp = productRepository.findById(productDTO.getId());
        if(productOp.isPresent()) {
            Product product = productMapper.partialUpdate(productOp.get(), productDTO);
            product = productRepository.save(product);
            return productMapper.toDTO(product);
        }
        return null;
    }

    /**
     * Checks whether an entity with the specified ID exists in the repository.
     *
     * @param id the ID of the entity to check for existence
     * @return true if an entity with the given ID exists, false otherwise
     */
    @Override
    public Boolean existsById(Long id) {
        return productRepository.existsById(id);
    }
}
