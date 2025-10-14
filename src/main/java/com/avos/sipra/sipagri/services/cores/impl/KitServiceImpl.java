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

@Service
public class KitServiceImpl implements KitService {

    private final KitMapper kitMapper;

    private final KitRepository kitRepository;
    private final ProductService productService;

    KitServiceImpl(KitRepository kitRepository, KitMapper kitMapper, ProductService productService) {
        this.kitRepository = kitRepository;
        this.kitMapper = kitMapper;
        this.productService = productService;
    }

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

    @Override
    public void delete(Long id) {
        kitRepository.deleteById(id);
    }

    @Override
    public KitDTO findOne(Long id) {
        Optional<Kit> kitOptional = kitRepository.findById(id);
        if (kitOptional.isPresent()) {
            return kitMapper.toDTO(kitOptional.get());
        } else {
            throw new IllegalArgumentException("Kit with ID " + id + " does not exist");
        }
    }

    @Override
    public List<KitDTO> findAll() {
        List<Kit> kits = kitRepository.findAll();
        List<KitDTO> kitDTOs = new ArrayList<>();
        for (Kit kit : kits) {
            kitDTOs.add(kitMapper.toDTO(kit));
        }
        return kitDTOs;
    }

    @Override
    public PaginationResponseDTO<KitDTO> findAllPaged(Pageable pageable) {
        final Page<Kit> page = kitRepository.findAll(pageable);

        return getKitDTOPaginationResponseDTO(page);
    }

    @Override
    public PaginationResponseDTO<KitDTO> findAllPagedByParams(Pageable pageable, String params) {
        final Page<Kit> page = kitRepository.findKitByNameContainingIgnoreCase(pageable, params);

        return getKitDTOPaginationResponseDTO(page);
    }

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

    @Override
    public Boolean existsById(Long id) {
        return kitRepository.existsById(id);
    }
}
