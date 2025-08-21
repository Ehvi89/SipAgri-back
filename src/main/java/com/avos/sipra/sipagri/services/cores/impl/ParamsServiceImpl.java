package com.avos.sipra.sipagri.services.cores.impl;

import com.avos.sipra.sipagri.entities.Params;
import com.avos.sipra.sipagri.repositories.ParamsRepository;
import com.avos.sipra.sipagri.services.cores.ParamsService;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.ParamsDTO;
import com.avos.sipra.sipagri.services.mappers.ParamsMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ParamsServiceImpl implements ParamsService {
    private final ParamsMapper paramsMapper;
    private final ParamsRepository paramsRepository;

    public ParamsServiceImpl(ParamsMapper paramsMapper, ParamsRepository paramsRepository) {
        this.paramsMapper = paramsMapper;
        this.paramsRepository = paramsRepository;
    }

    @Override
    public ParamsDTO save(ParamsDTO paramsDTO) {
        Params params = paramsMapper.toEntity(paramsDTO);
        params = paramsRepository.save(params);
        return paramsMapper.toDTO(params);
    }

    @Override
    public ParamsDTO update(ParamsDTO paramsDTO) {
        if (Objects.isNull(paramsDTO)) {throw new IllegalArgumentException("Params cannot be null");}
        if (!existsById(paramsDTO.getId())) {throw new NullPointerException("Params not found");}
        return save(paramsDTO);
    }

    @Override
    public void delete(Long id) {
        paramsRepository.deleteById(id);
    }

    @Override
    public ParamsDTO findOne(Long id) {
        Optional<Params> params = paramsRepository.findById(id);
        return params.map(paramsMapper::toDTO).orElse(null);
    }

    public ParamsDTO findByName(String name) {
        Optional<Params> params = paramsRepository.findByName(name);
        return params.map(paramsMapper::toDTO).orElse(null);
    }

    @Override
    public List<ParamsDTO> findAll() {
        List<Params> params = paramsRepository.findAll();
        List<ParamsDTO> paramsDTOS = new ArrayList<>();
        for (Params param : params) {
            paramsDTOS.add(paramsMapper.toDTO(param));
        }
        return paramsDTOS;
    }

    @Override
    public List<ParamsDTO> findByCode(String code) {
        List<Params> params = paramsRepository.findByCodeParams(code);
        List<ParamsDTO> paramsDTOS = new ArrayList<>();
        for (Params param : params) {
            paramsDTOS.add(paramsMapper.toDTO(param));
        }
        return paramsDTOS;
    }

    @Override
    public ParamsDTO partialUpdate(ParamsDTO paramsDTO) {
        if (Objects.isNull(paramsDTO)) {throw new IllegalArgumentException("Params cannot be null");}
        if (!existsById(paramsDTO.getId())) {throw new NullPointerException("Params not found");}
        Optional<Params> paramsOptional = paramsRepository.findById(paramsDTO.getId());
        if (paramsOptional.isPresent()) {
            Params params = paramsMapper.partialUpdate(paramsOptional.get(), paramsDTO);
            params = paramsRepository.save(params);
            return paramsMapper.toDTO(params);
        }
        return null;
    }

    @Override
    public Boolean existsById(Long id) {
        return paramsRepository.existsById(id);
    }

    @Override
    public PaginationResponseDTO<ParamsDTO> findAllPaged(Pageable pageable) {
        Page<Params> page = paramsRepository.findAll(pageable);

        final int currentPage = page.getNumber();
        final int totalPage = page.getTotalPages();
        final int totalElements = (int) page.getTotalElements();

        List<ParamsDTO> paramsDTOS = new ArrayList<>();
        for (Params params : page.getContent()) {
            paramsDTOS.add(paramsMapper.toDTO(params));
        }

        return new PaginationResponseDTO<>(currentPage, totalPage, totalElements, paramsDTOS);
    }
}
