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

@Slf4j
@Service
public class KitProductServiceImpl implements KitProductService{

    private final KitProductMapper kitProductMapper;
    private final KitProductRepository kitProductRepository;
    private final ProductService productService;

    public KitProductServiceImpl(KitProductMapper kitProductMapper, KitProductRepository kitProductRepository, ProductService productService) {
        this.kitProductMapper = kitProductMapper;
        this.kitProductRepository = kitProductRepository;
        this.productService = productService;
    }

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

    @Override
    public KitProductDTO update(KitProductDTO kitProductDTO) {
        if (Objects.isNull(kitProductDTO)) {throw new IllegalArgumentException("Id must not be null");}
        if (Boolean.FALSE.equals(existsById(kitProductDTO.getId()))) {throw new NullPointerException("kitProductDTO does not exist");}
        return save(kitProductDTO);
    }

    @Override
    public void delete(Long id) {
        kitProductRepository.deleteById(id);
    }

    @Override
    public KitProductDTO findOne(Long id) {
        Optional<KitProduct> optional = kitProductRepository.findById(id);
        return optional.map(kitProductMapper::toDTO).orElse(null);
    }

    @Override
    public List<KitProductDTO> findAll() {
        List<KitProduct> kitProducts = kitProductRepository.findAll();
        List<KitProductDTO> kitProductDTO = new ArrayList<>();
        for (KitProduct kitProduct : kitProducts) {
            kitProductDTO.add(kitProductMapper.toDTO(kitProduct));
        }
        return kitProductDTO;
    }

    @Override
    public PaginationResponseDTO<KitProductDTO> findAllPaged(Pageable  pageable) {
        final Page<KitProduct> page = kitProductRepository.findAll(pageable);

        final int currentPage = page.getNumber();
        final int totalPages = page.getTotalPages();
        final int totalElements = (int) page.getTotalElements();

        List<KitProductDTO> kitProductDTO = new ArrayList<>();
        for (KitProduct kitProduct : page.getContent()) {
            kitProductDTO.add(kitProductMapper.toDTO(kitProduct));
        }

        return new PaginationResponseDTO<>(currentPage, totalPages, totalElements, kitProductDTO);
    }

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

    @Override
    public Boolean existsById(Long id) {
        return kitProductRepository.existsById(id);
    }
}
