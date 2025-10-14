package com.avos.sipra.sipagri.services.cores.impl;

import com.avos.sipra.sipagri.entities.Supervisor;
import com.avos.sipra.sipagri.enums.SupervisorProfile;
import com.avos.sipra.sipagri.repositories.SupervisorRepository;
import com.avos.sipra.sipagri.services.cores.SupervisorService;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.SupervisorDTO;
import com.avos.sipra.sipagri.services.mappers.SupervisorMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SupervisorServiceImpl implements SupervisorService {
    private final SupervisorMapper supervisorMapper;
    private final SupervisorRepository supervisorRepository;

    public SupervisorServiceImpl(SupervisorMapper supervisorMapper, SupervisorRepository supervisorRepository) {
        this.supervisorRepository = supervisorRepository;
        this.supervisorMapper = supervisorMapper;
    }

    @Override
    public SupervisorDTO save(SupervisorDTO supervisorDTO) {
        Supervisor supervisor = supervisorMapper.toEntity(supervisorDTO);
        supervisor = supervisorRepository.save(supervisor);
        return supervisorMapper.toDTO(supervisor);
    }

    @Override
    public SupervisorDTO update(SupervisorDTO supervisorDTO) {
        if (Objects.isNull(supervisorDTO)) {throw new IllegalArgumentException("SupervisorDTO is null");}
        if (Boolean.FALSE.equals(existsById(supervisorDTO.getId()))) {throw new NullPointerException("ID cannot be null");}
        return save(supervisorDTO);
    }

    @Override
    public void delete(Long id) {
        supervisorRepository.deleteById(id);
    }

    @Override
    public SupervisorDTO findOne(Long id) {
        Optional<Supervisor> supervisor = supervisorRepository.findById(id);
        return supervisor.map(supervisorMapper::toDTO).orElse(null);
    }

    @Override
    public SupervisorDTO findByEmail(String email) {
        Optional<Supervisor> supervisor = supervisorRepository.findByEmail(email);
        return supervisor.map(supervisorMapper::toDTO).orElse(null);
    }

    @Override
    public List<SupervisorDTO> findAll() {
        List<Supervisor> supervisors = supervisorRepository.findAll();
        List<SupervisorDTO> supervisorDTOS = new ArrayList<>();
        for (Supervisor supervisor : supervisors) {
            supervisorDTOS.add(supervisorMapper.toDTO(supervisor));
        }
        return supervisorDTOS;
    }

    @Override
    public PaginationResponseDTO<SupervisorDTO> findAllPaged(Pageable pageable) {
        final Page<Supervisor> page = supervisorRepository.findAll(pageable);

        return getSupervisorDTOPaginationResponseDTO(page);
    }

    @Override
    public PaginationResponseDTO<SupervisorDTO> findAllPagedByParams(Pageable pageable, String params) {
        final Page<Supervisor> page = supervisorRepository.searchSupervisors(pageable, params, params, null);

        return getSupervisorDTOPaginationResponseDTO(page);
    }

    @Override
    public PaginationResponseDTO<SupervisorDTO> findAllPagedByParams(Pageable pageable, String params, SupervisorProfile profile) {
        final Page<Supervisor> page = supervisorRepository.searchSupervisors(pageable, params, params, profile);

        return getSupervisorDTOPaginationResponseDTO(page);
    }

    private PaginationResponseDTO<SupervisorDTO> getSupervisorDTOPaginationResponseDTO(Page<Supervisor> page) {
        final int currentPage = page.getNumber();
        final int totalPage = page.getTotalPages();
        final int totalElements = (int) page.getTotalElements();

        List<SupervisorDTO> supervisorDTOS = new ArrayList<>();
        for (Supervisor supervisor : page.getContent()) {
            supervisorDTOS.add(supervisorMapper.toDTO(supervisor));
        }

        return new PaginationResponseDTO<>(currentPage, totalPage, totalElements, supervisorDTOS);
    }

    @Override
    public SupervisorDTO partialUpdate(SupervisorDTO supervisorDTO) {
        if (Objects.isNull(supervisorDTO)) {throw new IllegalArgumentException("SupervisorDTO is null");}
        if (Boolean.FALSE.equals(existsById(supervisorDTO.getId()))) {throw new NullPointerException("SupervisorDTO not found");}
        Optional<Supervisor> supervisorOptional = supervisorRepository.findById(supervisorDTO.getId());
        if (supervisorOptional.isPresent()) {
            Supervisor supervisor = supervisorMapper.partialUpdate(supervisorOptional.get(), supervisorDTO);
            supervisor = supervisorRepository.save(supervisor);
            return supervisorMapper.toDTO(supervisor);
        }
        return null;
    }

    @Override
    public Boolean existsById(Long id) {
        return supervisorRepository.existsById(id);
    }
}
