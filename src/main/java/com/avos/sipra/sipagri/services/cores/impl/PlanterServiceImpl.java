package com.avos.sipra.sipagri.services.cores.impl;

import com.avos.sipra.sipagri.entities.Planter;
import com.avos.sipra.sipagri.repositories.PlanterRepository;
import com.avos.sipra.sipagri.services.cores.PlanterService;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.PlanterDTO;
import com.avos.sipra.sipagri.services.mappers.PlanterMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PlanterServiceImpl implements PlanterService {
    private final PlanterMapper planterMapper;
    private final PlanterRepository planterRepository;

    public PlanterServiceImpl(PlanterMapper planterMapper, PlanterRepository planterRepository) {
        this.planterMapper = planterMapper;
        this.planterRepository = planterRepository;
    }

    @Override
    public PlanterDTO save(PlanterDTO planterDTO) {
        Planter planter = planterMapper.toEntity(planterDTO);
        planter = planterRepository.save(planter);
        return planterMapper.toDTO(planter);
    }

    @Override
    public PlanterDTO update(PlanterDTO planterDTO) {
        if(Objects.isNull(planterDTO.getId())) {throw new IllegalArgumentException("Id cannot be null");}
        if(Boolean.FALSE.equals(existsById(planterDTO.getId()))) {throw new IllegalArgumentException("Planter cannot be null");}
        return save(planterDTO);
    }

    @Override
    public void delete(Long id) {
        planterRepository.deleteById(id);
    }

    @Override
    public PlanterDTO findOne(Long id) {
        Optional<Planter> planterOptional = planterRepository.findById(id);
        if(planterOptional.isPresent()) {
            return planterMapper.toDTO(planterOptional.get());
        }
        throw new NullPointerException("Planter does not exist");
    }

    @Override
    public List<PlanterDTO> findAll() {
        List<Planter> planterList = planterRepository.findAll();
        List<PlanterDTO> planterDTOList = new ArrayList<>();
        for(Planter planter : planterList) {
            planterDTOList.add(planterMapper.toDTO(planter));
        }
        return planterDTOList;
    }

    @Override
    public PaginationResponseDTO<PlanterDTO> findAllPaged(Pageable pageable) {
        final Page<Planter> page =  planterRepository.findAll(pageable);

        return getPlanterDTOPaginationResponseDTO(page);
    }

    @Override
    public PaginationResponseDTO<PlanterDTO> findAllPagedByParams(Pageable pageable, String search) {
        final Page<Planter> page = planterRepository.findPlanterByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(pageable, search, search);

        return getPlanterDTOPaginationResponseDTO(page);
    }

    private PaginationResponseDTO<PlanterDTO> getPlanterDTOPaginationResponseDTO(Page<Planter> page) {
        final int currentPage = page.getNumber();
        final int totalPages = page.getTotalPages();
        final int totalElements = (int) page.getTotalElements();

        List<PlanterDTO> planterDTOList = new ArrayList<>();
        for (Planter planter : page.getContent()) {
            planterDTOList.add(planterMapper.toDTO(planter));
        }

        return new PaginationResponseDTO<>(currentPage, totalPages, totalElements, planterDTOList);
    }

    @Override
    public PlanterDTO partialUpdate(PlanterDTO planterDTO) {
        if (Objects.isNull(planterDTO.getId())) {throw new IllegalArgumentException("Id cannot be null");}
        if (Boolean.FALSE.equals(existsById(planterDTO.getId()))) {throw new IllegalArgumentException("Planter cannot be null");}
        Optional<Planter> planterOptional = planterRepository.findById(planterDTO.getId());
        if(planterOptional.isPresent()) {
            Planter planter = planterMapper.partialUpdate(planterOptional.get(), planterDTO);
            planter = planterRepository.save(planter);
            return planterMapper.toDTO(planter);
        }
        throw new NullPointerException("Planter does not exist");
    }

    @Override
    public Boolean existsById(Long id) {
        return planterRepository.existsById(id);
    }
}
