package com.avos.sipra.sipagri.controllers;

import com.avos.sipra.sipagri.annotations.XSSProtected;
import com.avos.sipra.sipagri.services.cores.ProductService;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.ProductDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing product-related operations.
 * Provides endpoints to perform CRUD operations on products
 * and to retrieve product data with pagination and search capabilities.
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    /**
     * Service used to manage product-related operations within the application.
     * This service is injected into the ProductController to facilitate
     * the handling of business logic associated with products.
     */
    private final ProductService productService;

    /**
     * Constructs a ProductController with the specified ProductService.
     *
     * @param productService the ProductService instance used to handle product-related operations
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Retrieves a paginated list of products.
     *
     * @param page the page number to retrieve, starting at 0. Defaults to 0 if not provided.
     * @param size the number of items per page. Defaults to 10 if not provided.
     * @return a ResponseEntity containing a PaginationResponseDTO of ProductDTO objects.
     *         Returns a 404 (Not Found) response if no products are available.
     */
    @GetMapping
    public ResponseEntity<PaginationResponseDTO<ProductDTO>> findProducts(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PaginationResponseDTO<ProductDTO> response = productService.findAllPaged(pageable);

        if (response.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all products from the service layer.
     *
     * @return a ResponseEntity containing a list of ProductDTO objects. If no products are found,
     *         returns a ResponseEntity with a not found (404) status.
     */
    @GetMapping("/all")
    public ResponseEntity<List<ProductDTO>> findAll() {
        List<ProductDTO> products = productService.findAll();
        if (products == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(products);
    }

    /**
     * Retrieves a product by its unique identifier.
     *
     * @param id the unique identifier of the product to be retrieved
     * @return a {@link ResponseEntity} containing the product details as a {@link ProductDTO}
     *         if found, or a {@link ResponseEntity} with a 404 Not Found status if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable long id) {
        ProductDTO productDTO = productService.findOne(id);
        if (productDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productDTO);
    }

    /**
     * Searches for products based on a search term and returns a paginated response.
     *
     * @param search The search term to filter products.
     * @param page The page number to retrieve, starting from 0. Default is 0.
     * @param size The number of products per page. Default is 10.
     * @return A {@code ResponseEntity} containing the paginated results of products as a {@code PaginationResponseDTO<ProductDTO>}.
     *         If no products are found, a {@code ResponseEntity} with a 404 status is returned.
     */
    @GetMapping("/search")
    public ResponseEntity<PaginationResponseDTO<ProductDTO>> searchProducts(
            @RequestParam String search,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);

        PaginationResponseDTO<ProductDTO> responseDTO = productService.findAllPagedByParams(pageable, search);
        if (responseDTO.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Saves a new product using the provided product data and returns the created product.
     *
     * @param productDTO the product data to be saved
     * @return a response entity containing the created product if successful, or a response indicating the product was not created
     */
    @PostMapping
    @XSSProtected
    public ResponseEntity<ProductDTO> save(@RequestBody ProductDTO productDTO) {
        ProductDTO product = productService.save(productDTO);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    /**
     * Updates an existing product using the provided product data.
     *
     * @param productDTO the {@link ProductDTO} object containing updated product details
     * @return a {@link ResponseEntity} containing the updated product as a {@link ProductDTO}
     *         with a status of 202 Accepted, or a 404 Not Found status if the product does not exist
     */
    @PutMapping
    @XSSProtected
    public ResponseEntity<ProductDTO> update(@RequestBody ProductDTO productDTO) {
        ProductDTO product = productService.update(productDTO);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(product);
    }

    /**
     * Partially updates the details of an existing product using the provided data.
     * If the product does not exist, returns a 404 (Not Found) response.
     * If successful, returns the updated product with a status of 202 Accepted.
     *
     * @param productDTO the {@link ProductDTO} object containing the product details to be updated
     * @return a {@link ResponseEntity} containing the updated {@link ProductDTO} with a status of 202 Accepted,
     *         or a 404 Not Found status if the product does not exist
     */
    @PatchMapping
    @XSSProtected
    public ResponseEntity<ProductDTO> patch(@RequestBody ProductDTO productDTO) {
        ProductDTO product = productService.partialUpdate(productDTO);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(product);
    }

    /**
     * Deletes a product identified by its unique identifier.
     * If the product does not exist, returns a 404 (Not Found) response.
     * If the product is successfully deleted, returns a 200 (OK) status.
     *
     * @param id the unique identifier of the product to be deleted
     * @return a {@link ResponseEntity} with a 200 (OK) status if the product is successfully deleted,
     *         or a 404 (Not Found) status if the product does not exist
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ProductDTO> delete(@PathVariable long id) {
        ProductDTO productDTO = productService.findOne(id);
        if (productDTO == null) {
            return ResponseEntity.notFound().build();
        }
        productService.delete(id);
        return ResponseEntity.ok().build();
    }
}
