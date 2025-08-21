package com.avos.sipra.sipagri.services.cores.impl;

import com.avos.sipra.sipagri.entities.Production;
import com.avos.sipra.sipagri.repositories.ProductionRepository;
import com.avos.sipra.sipagri.services.cores.CalculationService;
import com.avos.sipra.sipagri.services.dtos.ProductionDTO;
import com.avos.sipra.sipagri.services.mappers.ProductionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductionServiceImplTest {

    private ProductionRepository productionRepository;
    private ProductionMapper productionMapper;
    private CalculationService calculationService;
    private ProductionServiceImpl productionService;

    @BeforeEach
    void setUp() {
        productionRepository = mock(ProductionRepository.class);
        productionMapper = mock(ProductionMapper.class);
        calculationService = mock(CalculationService.class);
        productionService = new ProductionServiceImpl(productionMapper, productionRepository, calculationService);
    }

    @Test
    void testSaveWithPlantationAndMustBePaid() {
        ProductionDTO dto = new ProductionDTO();
        dto.setPlantationId(1L);
        dto.setProductionInKg(10.0);

        Production entity = new Production();
        entity.setId(1L);

        ProductionDTO calculatedDTO = new ProductionDTO();
        calculatedDTO.setProductionInKg(10.0);
        calculatedDTO.setMustBePaid(true);

        when(calculationService.calculateProductionValues(dto)).thenReturn(calculatedDTO);
        when(productionMapper.toEntity(calculatedDTO)).thenReturn(entity);
        when(productionRepository.save(entity)).thenReturn(entity);
        when(productionMapper.toDTO(entity)).thenReturn(calculatedDTO);
        when(calculationService.calculateMustBePaid(calculatedDTO, 1L)).thenReturn(calculatedDTO);

        ProductionDTO result = productionService.save(dto);

        assertTrue(result.getMustBePaid());
        verify(productionRepository, times(1)).save(entity); // saved twice: initial + mustBePaid update
    }

    @Test
    void testUpdateExistingProduction() {
        ProductionDTO dto = new ProductionDTO();
        dto.setId(1L);
        dto.setPlantationId(1L);
        dto.setProductionInKg(20.0);

        when(productionService.existsById(1L)).thenReturn(true);
        when(calculationService.calculateMustBePaid(dto, 1L)).thenReturn(dto);

        Production entity = new Production();
        entity.setId(1L);

        when(productionMapper.toEntity(dto)).thenReturn(entity);
        when(productionRepository.save(entity)).thenReturn(entity);
        when(productionMapper.toDTO(entity)).thenReturn(dto);

        ProductionDTO result = productionService.update(dto);

        assertEquals(dto, result);
        verify(productionRepository).save(entity);
    }

    @Test
    void testPartialUpdateWithChangeInKg() {
        ProductionDTO dto = new ProductionDTO();
        dto.setId(1L);
        dto.setProductionInKg(15.0);

        Production existing = new Production();
        existing.setId(1L);

        when(productionService.existsById(1L)).thenReturn(true);
        when(productionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(calculationService.calculateProductionValues(dto)).thenReturn(dto);

        Production updated = new Production();
        updated.setId(1L);

        when(productionMapper.partialUpdate(existing, dto)).thenReturn(updated);
        when(productionRepository.save(updated)).thenReturn(updated);
        when(productionMapper.toDTO(updated)).thenReturn(dto);

        ProductionDTO result = productionService.partialUpdate(dto);

        assertEquals(dto, result);
        verify(productionRepository).save(updated);
    }

    @Test
    void testFindOneExisting() {
        Production existing = new Production();
        existing.setId(1L);
        ProductionDTO dto = new ProductionDTO();
        dto.setId(1L);

        when(productionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productionMapper.toDTO(existing)).thenReturn(dto);

        ProductionDTO result = productionService.findOne(1L);
        assertEquals(dto, result);
    }

    @Test
    void testFindAll() {
        Production p1 = new Production();
        Production p2 = new Production();
        ProductionDTO dto1 = new ProductionDTO();
        ProductionDTO dto2 = new ProductionDTO();

        when(productionRepository.findAll()).thenReturn(Arrays.asList(p1, p2));
        when(productionMapper.toDTO(p1)).thenReturn(dto1);
        when(productionMapper.toDTO(p2)).thenReturn(dto2);

        List<ProductionDTO> results = productionService.findAll();
        assertEquals(2, results.size());
        assertTrue(results.contains(dto1) && results.contains(dto2));
    }

    @Test
    void testFindAllPaged() {
        Production p1 = new Production();
        Production p2 = new Production();
        ProductionDTO dto1 = new ProductionDTO();
        ProductionDTO dto2 = new ProductionDTO();

        Page<Production> page = new PageImpl<>(Arrays.asList(p1, p2));

        when(productionRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(productionMapper.toDTO(p1)).thenReturn(dto1);
        when(productionMapper.toDTO(p2)).thenReturn(dto2);

        var result = productionService.findAllPaged(Pageable.unpaged());
        assertEquals(2, result.getData().size());
        assertTrue(result.getData().contains(dto1));
        assertTrue(result.getData().contains(dto2));
    }

    @Test
    void testDelete() {
        productionService.delete(1L);
        verify(productionRepository).deleteById(1L);
    }

    @Test
    void testExistsById() {
        when(productionRepository.existsById(1L)).thenReturn(true);
        assertTrue(productionService.existsById(1L));
    }

}
