package com.avos.sipra.sipagri.services.cores.impl;

import com.avos.sipra.sipagri.entities.Plantation;
import com.avos.sipra.sipagri.entities.Planter;
import com.avos.sipra.sipagri.repositories.PlantationRepository;
import com.avos.sipra.sipagri.repositories.PlanterRepository;
import com.avos.sipra.sipagri.services.cores.CalculationService;
import com.avos.sipra.sipagri.services.cores.PlantationService;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.PlantationDTO;
import com.avos.sipra.sipagri.services.dtos.ProductionDTO;
import com.avos.sipra.sipagri.services.mappers.PlantationMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PlantationServiceImpl implements PlantationService {
    private final PlantationMapper plantationMapper;
    private final PlantationRepository plantationRepository;
    private final CalculationService calculationService;
    private final PlanterRepository planterRepository;

    public PlantationServiceImpl(PlantationMapper plantationMapper,
                                 PlantationRepository plantationRepository,
                                 CalculationService calculationService,
                                 PlanterRepository planterRepository) {
        this.plantationMapper = plantationMapper;
        this.plantationRepository = plantationRepository;
        this.calculationService = calculationService;
        this.planterRepository = planterRepository;
    }

    @Override
    public PlantationDTO save(PlantationDTO plantationDTO) {
        Plantation plantation = plantationMapper.toEntity(plantationDTO);

        // Calculer les valeurs des productions
        if (plantationDTO.getProductions() != null) {
            for (ProductionDTO productionDTO : plantationDTO.getProductions()) {
                calculationService.calculateProductionValues(productionDTO);
                // mustBePaid sera calculé après sauvegarde
            }
        }

        plantation = plantationRepository.save(plantation);

        // Mettre à jour la liste des plantations du planter
        Planter planter = planterRepository.getReferenceById(plantation.getPlanterId());
        planter.getPlantations().add(plantation);
        planterRepository.save(planter);

        return plantationMapper.toDTO(plantation);
    }

    @Override
    public PlantationDTO update(PlantationDTO plantationDTO) {
        if (Objects.isNull(plantationDTO.getId())) {throw new IllegalArgumentException("Id cannot be null");}
        if (Boolean.FALSE.equals(existsById(plantationDTO.getId()))) {throw new IllegalArgumentException("Plantation does not exist");}
        return save(plantationDTO);
    }

    @Override
    public void delete(Long id) {
        plantationRepository.deleteById(id);
    }

    @Override
    public PlantationDTO findOne(Long id) {
        Optional<Plantation> plantation = plantationRepository.findById(id);
        if (plantation.isPresent()) {
            return plantationMapper.toDTO(plantation.get());
        }
        throw new NullPointerException("Plantation does not exist");
    }

    @Override
    public PlantationDTO findByProductions_id(Long id) {
        Optional<Plantation> plantation = plantationRepository.findByProductions_id(id);
        if (plantation.isPresent()) {
            return plantationMapper.toDTO(plantation.get());
        }
        else throw new NullPointerException("Plantation does not exist");
    }

    @Override
    public List<PlantationDTO> findAll() {
        List<Plantation> plantations = plantationRepository.findAll();
        List<PlantationDTO> plantationDTOS = new ArrayList<>();
        for (Plantation plantation : plantations) {
            plantationDTOS.add(plantationMapper.toDTO(plantation));
        }
        return plantationDTOS;
    }

    @Override
    public PaginationResponseDTO<PlantationDTO> findAllPaged(Pageable pageable) {
        Page<Plantation> page =  plantationRepository.findAll(pageable);

        return getPlantationDTOPaginationResponseDTO(page);
    }

    @Override
    public PaginationResponseDTO<PlantationDTO> findAllPagedByParams(Pageable pageable, String params) {
        Page<Plantation> page =  plantationRepository.findPlantationsByNameContainingIgnoreCase(pageable, params);

        return getPlantationDTOPaginationResponseDTO(page);
    }

    private PaginationResponseDTO<PlantationDTO> getPlantationDTOPaginationResponseDTO(Page<Plantation> page) {
        final int currentPage = page.getNumber();
        final int totalPages = page.getTotalPages();
        final int totalElements = (int) page.getTotalElements();

        List<PlantationDTO> plantationDTOS = new ArrayList<>();
        for (Plantation plantation : page.getContent()) {
            plantationDTOS.add(plantationMapper.toDTO(plantation));
        }

        return new PaginationResponseDTO<>(currentPage, totalPages, totalElements, plantationDTOS);
    }

    @Override
    public PlantationDTO partialUpdate(PlantationDTO plantationDTO) {
        if (Objects.isNull(plantationDTO.getId())) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (Boolean.FALSE.equals(existsById(plantationDTO.getId()))) {
            throw new IllegalArgumentException("Plantation does not exist");
        }

        Optional<Plantation> plantationOptional = plantationRepository.findById(plantationDTO.getId());
        if (plantationOptional.isPresent()) {
            // Calculer les valeurs des nouvelles productions
            if (plantationDTO.getProductions() != null) {
                List<ProductionDTO> productionDTOS = new ArrayList<>();
                for (ProductionDTO production : plantationDTO.getProductions()) {
                    productionDTOS.add(calculationService.calculateProductionValues(production));
                }

                plantationDTO.setProductions(productionDTOS);
            }

            Plantation plantation = plantationMapper.partialUpdate(plantationOptional.get(), plantationDTO);
            plantation = plantationRepository.save(plantation);
            return plantationMapper.toDTO(plantation);
        }
        throw new NullPointerException("Plantation does not exist");
    }

    @Override
    public Boolean existsById(Long id) {
        return plantationRepository.existsById(id);
    }
}
