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

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductMapper productMapper, ProductRepository productRepository) {
        this.productMapper = productMapper;
        this.productRepository = productRepository;
    }

    @Override
    public ProductDTO save(ProductDTO productDTO) {
        Product product = productMapper.toEntity(productDTO);
        product = productRepository.save(product);
        return productMapper.toDTO(product);
    }

    @Override
    public ProductDTO update(ProductDTO productDTO) {
        if(Objects.isNull(productDTO.getId())) {throw new IllegalArgumentException("Id cannot be null");}
        if(!existsById(productDTO.getId())) {throw new IllegalArgumentException("Product not found");}
        return save(productDTO);
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public ProductDTO findOne(Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.map(productMapper::toDTO).orElse(null);
    }

    @Override
    public List<ProductDTO> findAll() {
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOS = new ArrayList<>();
        for(Product product : products) {
            productDTOS.add(productMapper.toDTO(product));
        }
        return productDTOS;
    }

    @Override
    public PaginationResponseDTO<ProductDTO> findAllPaged(Pageable pageable) {
        final Page<Product> page = productRepository.findAll(pageable);

        final int currentPage = page.getNumber();
        final int totalPages = page.getTotalPages();
        final int totalElements = (int) page.getTotalElements();

        List<ProductDTO> productDTOS = new ArrayList<>();
        for(Product product : page.getContent()) {
            productDTOS.add(productMapper.toDTO(product));
        }

        return new PaginationResponseDTO<>(currentPage, totalPages, totalElements, productDTOS);
    }

    @Override
    public ProductDTO partialUpdate(ProductDTO productDTO) {
        if(Objects.isNull(productDTO.getId())) {throw new IllegalArgumentException("Id cannot be null");}
        if(!existsById(productDTO.getId())) {throw new IllegalArgumentException("Product not found");}
        Optional<Product> productOp = productRepository.findById(productDTO.getId());
        if(productOp.isPresent()) {
            Product product = productMapper.partialUpdate(productOp.get(), productDTO);
            product = productRepository.save(product);
            return productMapper.toDTO(product);
        }
        return null;
    }

    @Override
    public Boolean existsById(Long id) {
        return productRepository.existsById(id);
    }
}
