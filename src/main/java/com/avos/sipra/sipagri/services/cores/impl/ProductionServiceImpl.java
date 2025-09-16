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
        }
        Plantation plantation = plantationRepository.getReferenceById(production.getPlantation().getId());
        plantation.getProductions().add(production);
        plantationRepository.save(plantation);

        return productionMapper.toDTO(production);
    }

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

    @Override
    public void delete(Long id) {
        productionRepository.deleteById(id);
    }

    @Override
    public ProductionDTO findOne(Long id) {
        Optional<Production> productionOptional = productionRepository.findById(id);
        if (productionOptional.isPresent()) {
            return productionMapper.toDTO(productionOptional.get());
        }
        throw new IllegalArgumentException("Production with ID " + id + " does not exist");
    }

    @Override
    public List<ProductionDTO> findAll() {
        List<Production> productions = productionRepository.findAll();
        List<ProductionDTO> productionDTOS = new ArrayList<>();
        for (Production production : productions) {
            productionDTOS.add(productionMapper.toDTO(production));
        }
        return productionDTOS;
    }

    @Override
    public PaginationResponseDTO<ProductionDTO> findAllPaged(Pageable pageable) {
        final Page<Production> page = productionRepository.findAll(pageable);

        final int currentPage = page.getNumber();
        final int totalPages = page.getTotalPages();
        final int totalElements = (int) page.getTotalElements();

        List<ProductionDTO> productionDTOS = new ArrayList<>();
        for (Production production : page.getContent()) {
            productionDTOS.add(productionMapper.toDTO(production));
        }

        return new PaginationResponseDTO<>(currentPage, totalPages, totalElements, productionDTOS);
    }

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

    @Override
    public Boolean existsById(Long id) {
        return productionRepository.existsById(id);
    }
}