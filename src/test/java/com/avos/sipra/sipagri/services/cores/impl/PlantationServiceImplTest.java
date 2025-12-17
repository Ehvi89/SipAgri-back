package com.avos.sipra.sipagri.services.cores.impl;

import com.avos.sipra.sipagri.entities.Plantation;
import com.avos.sipra.sipagri.services.cores.CalculationService;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.PlantationDTO;
import com.avos.sipra.sipagri.services.dtos.ProductionDTO;
import com.avos.sipra.sipagri.services.mappers.PlantationMapper;
import com.avos.sipra.sipagri.repositories.PlantationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlantationServiceImplTest {

    @Mock
    private PlantationMapper plantationMapper;

    @Mock
    private PlantationRepository plantationRepository;

    @Mock
    private CalculationService calculationService;

    @InjectMocks
    private PlantationServiceImpl plantationService;

    private Plantation plantation;
    private PlantationDTO plantationDTO;
    private ProductionDTO productionDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        productionDTO = new ProductionDTO();
        productionDTO.setId(1L);
        productionDTO.setProductionInKg(10.0);

        plantationDTO = new PlantationDTO();
        plantationDTO.setId(1L);
        plantationDTO.setProductions(List.of(productionDTO));

        plantation = new Plantation();
        plantation.setId(1L);
    }

    // ---------- SAVE ----------
    @Test
    void save_shouldCallCalculationServiceAndSave() {
        when(plantationMapper.toEntity(plantationDTO)).thenReturn(plantation);
        when(calculationService.calculateProductionValues(productionDTO)).thenReturn(productionDTO);
        when(plantationRepository.save(plantation)).thenReturn(plantation);
        when(plantationMapper.toDTO(plantation)).thenReturn(plantationDTO);

        PlantationDTO result = plantationService.save(plantationDTO);

        assertNotNull(result);
        verify(calculationService, times(1)).calculateProductionValues(productionDTO);
        verify(plantationRepository, times(1)).save(plantation);
        verify(plantationMapper, times(1)).toDTO(plantation);
    }

    // ---------- UPDATE ----------
    @Test
    void update_ok() {
        when(plantationRepository.existsById(1L)).thenReturn(true);
        when(plantationMapper.toEntity(plantationDTO)).thenReturn(plantation);
        when(calculationService.calculateProductionValues(productionDTO)).thenReturn(productionDTO);
        when(plantationRepository.save(plantation)).thenReturn(plantation);
        when(plantationMapper.toDTO(plantation)).thenReturn(plantationDTO);

        PlantationDTO result = plantationService.update(plantationDTO);

        assertNotNull(result);
        verify(plantationRepository, times(1)).existsById(1L);
    }

    @Test
    void update_shouldThrow_whenIdNull() {
        plantationDTO.setId(null);
        assertThrows(IllegalArgumentException.class, () -> plantationService.update(plantationDTO));
    }

    @Test
    void update_shouldThrow_whenNotExists() {
        when(plantationRepository.existsById(1L)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> plantationService.update(plantationDTO));
    }

    // ---------- DELETE ----------
    @Test
    void delete_ok() {
        plantationService.delete(1L);
        verify(plantationRepository, times(1)).deleteById(1L);
    }

    // ---------- FIND ONE ----------
    @Test
    void findOne_found() {
        when(plantationRepository.findById(1L)).thenReturn(Optional.of(plantation));
        when(plantationMapper.toDTO(plantation)).thenReturn(plantationDTO);

        PlantationDTO result = plantationService.findOne(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findOne_shouldThrow_whenNotFound() {
        when(plantationRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NullPointerException.class, () -> plantationService.findOne(1L));
    }

    // ---------- FIND BY PRODUCTIONS ID ----------
    @Test
    void findByProductionsId_found() {
        when(plantationRepository.findByProductions_id(1L)).thenReturn(Optional.of(plantation));
        when(plantationMapper.toDTO(plantation)).thenReturn(plantationDTO);

        PlantationDTO result = plantationService.findByProductionsId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findByProductionsId_shouldThrow_whenNotFound() {
        when(plantationRepository.findByProductions_id(1L)).thenReturn(Optional.empty());
        assertThrows(NullPointerException.class, () -> plantationService.findByProductionsId(1L));
    }

    // ---------- FIND ALL ----------
    @Test
    void findAll_ok() {
        when(plantationRepository.findAll()).thenReturn(List.of(plantation));
        when(plantationMapper.toDTO(plantation)).thenReturn(plantationDTO);

        List<PlantationDTO> result = plantationService.findAll();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    // ---------- FIND ALL PAGED ----------
    @Test
    void findAllPaged_ok() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Plantation> page = new PageImpl<>(List.of(plantation), pageable, 1);

        when(plantationRepository.findAll(pageable)).thenReturn(page);
        when(plantationMapper.toDTO(plantation)).thenReturn(plantationDTO);

        PaginationResponseDTO<PlantationDTO> result = plantationService.findAllPaged(pageable);

        assertEquals(1, result.getData().size());
        assertEquals(1, result.getTotalElements());
    }

    // ---------- PARTIAL UPDATE ----------
    @Test
    void partialUpdate_ok() {
        when(plantationRepository.existsById(1L)).thenReturn(true);
        when(plantationRepository.findById(1L)).thenReturn(Optional.of(plantation));
        when(calculationService.calculateProductionValues(productionDTO)).thenReturn(productionDTO);
        when(plantationMapper.partialUpdate(plantation, plantationDTO)).thenReturn(plantation);
        when(plantationRepository.save(plantation)).thenReturn(plantation);
        when(plantationMapper.toDTO(plantation)).thenReturn(plantationDTO);

        PlantationDTO result = plantationService.partialUpdate(plantationDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void partialUpdate_shouldThrow_whenIdNull() {
        plantationDTO.setId(null);
        assertThrows(IllegalArgumentException.class, () -> plantationService.partialUpdate(plantationDTO));
    }

    @Test
    void partialUpdate_shouldThrow_whenNotExists() {
        when(plantationRepository.existsById(1L)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> plantationService.partialUpdate(plantationDTO));
    }

    // ---------- EXISTS ----------
    @Test
    void existsById_ok() {
        when(plantationRepository.existsById(1L)).thenReturn(true);
        assertTrue(plantationService.existsById(1L));
    }
}
