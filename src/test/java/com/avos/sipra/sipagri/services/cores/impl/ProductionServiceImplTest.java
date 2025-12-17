package com.avos.sipra.sipagri.services.cores.impl;

import com.avos.sipra.sipagri.entities.Plantation;
import com.avos.sipra.sipagri.entities.Production;
import com.avos.sipra.sipagri.repositories.PlantationRepository;
import com.avos.sipra.sipagri.repositories.ProductionRepository;
import com.avos.sipra.sipagri.services.cores.CalculationService;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.ProductionDTO;
import com.avos.sipra.sipagri.services.mappers.ProductionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ProductionServiceImplTest {

    @Mock
    private ProductionMapper productionMapper;

    @Mock
    private ProductionRepository productionRepository;

    @Mock
    private CalculationService calculationService;

    @Mock
    private PlantationRepository plantationRepository;

    @InjectMocks
    private ProductionServiceImpl productionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_ShouldSaveProductionAndUpdatePlantation_WhenValidProductionDTO() {
        ProductionDTO productionDTO = ProductionDTO.builder().plantationId(1L).build();
        ProductionDTO calculatedProductionDTO = ProductionDTO.builder().plantationId(1L).build();
        Production production = new Production();
        Production savedProduction = new Production();
        Plantation plantation = new Plantation();
        plantation.setId(1L);
        plantation.setProductions(new ArrayList<>());
        savedProduction.setPlantation(plantation);
        production.setPlantation(plantation);

        when(calculationService.calculateProductionValues(productionDTO)).thenReturn(calculatedProductionDTO);
        when(productionMapper.toEntity(calculatedProductionDTO)).thenReturn(production);
        when(productionRepository.save(production)).thenReturn(savedProduction);
        when(productionMapper.toDTO(savedProduction)).thenReturn(calculatedProductionDTO);
        when(calculationService.calculateMustBePaid(calculatedProductionDTO, 1L)).thenReturn(calculatedProductionDTO);
        when(plantationRepository.getReferenceById(anyLong())).thenReturn(plantation);

        ProductionDTO result = productionService.save(productionDTO);

        ArgumentCaptor<Production> productionCaptor = ArgumentCaptor.forClass(Production.class);
        verify(productionRepository, times(1)).save(productionCaptor.capture());
        verify(plantationRepository).save(plantation);

        assertEquals(calculatedProductionDTO, result);
        assertEquals(1, plantation.getProductions().size());
    }

    @Test
    void save_ShouldNotUpdatePlantation_WhenProductionHasNoPlantationId() {
        ProductionDTO productionDTO = ProductionDTO.builder().build();
        ProductionDTO calculatedProductionDTO = ProductionDTO.builder().build();
        Production production = new Production();
        Production savedProduction = new Production();

        when(calculationService.calculateProductionValues(productionDTO)).thenReturn(calculatedProductionDTO);
        when(productionMapper.toEntity(calculatedProductionDTO)).thenReturn(production);
        when(productionRepository.save(production)).thenReturn(savedProduction);
        when(productionMapper.toDTO(savedProduction)).thenReturn(calculatedProductionDTO);

        ProductionDTO result = productionService.save(productionDTO);

        verify(plantationRepository, never()).getReferenceById(anyLong());
        verify(plantationRepository, never()).save(any(Plantation.class));
        assertEquals(calculatedProductionDTO, result);
    }

    @Test
    void update_ShouldThrowException_WhenIdIsNull() {
        ProductionDTO productionDTO = ProductionDTO.builder().id(null).build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> productionService.update(productionDTO));

        assertEquals("Id cannot be null", exception.getMessage());
    }

    @Test
    void update_ShouldThrowException_WhenProductionDoesNotExist() {
        ProductionDTO productionDTO = ProductionDTO.builder().id(1L).build();

        when(productionRepository.existsById(1L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> productionService.update(productionDTO));

        assertEquals("Production does not exist", exception.getMessage());
    }

    @Test
    void update_ShouldUpdateProduction_WhenValid() {
        ProductionDTO productionDTO = ProductionDTO.builder().id(1L).build();
        Production entity = new Production();
        Production savedEntity = new Production();
        ProductionDTO updatedProductionDTO = ProductionDTO.builder().id(1L).build();

        when(productionRepository.existsById(1L)).thenReturn(true);
        when(calculationService.calculateProductionValues(productionDTO)).thenReturn(productionDTO);
        when(productionMapper.toEntity(productionDTO)).thenReturn(entity);
        when(productionRepository.save(entity)).thenReturn(savedEntity);
        when(productionMapper.toDTO(savedEntity)).thenReturn(updatedProductionDTO);

        ProductionDTO result = productionService.update(productionDTO);

        assertEquals(updatedProductionDTO, result);
    }

    @Test
    void delete_ShouldDeleteProduction() {
        productionService.delete(1L);

        verify(productionRepository).deleteById(1L);
    }

    @Test
    void findOne_ShouldReturnProduction_WhenProductionExists() {
        Production production = new Production();
        ProductionDTO productionDTO = new ProductionDTO();

        when(productionRepository.findById(1L)).thenReturn(Optional.of(production));
        when(productionMapper.toDTO(production)).thenReturn(productionDTO);

        ProductionDTO result = productionService.findOne(1L);

        assertEquals(productionDTO, result);
    }

    @Test
    void findOne_ShouldThrowException_WhenProductionDoesNotExist() {
        when(productionRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> productionService.findOne(1L));

        assertEquals("Production with ID 1 does not exist", exception.getMessage());
    }

    @Test
    void findAll_ShouldReturnListOfProductionDTOs() {
        List<Production> productions = List.of(new Production());
        List<ProductionDTO> productionDTOS = List.of(new ProductionDTO());

        when(productionRepository.findAll()).thenReturn(productions);
        when(productionMapper.toDTO(any())).thenReturn(productionDTOS.get(0));

        List<ProductionDTO> result = productionService.findAll();

        assertEquals(productionDTOS, result);
    }

    @Test
    void findAllPaged_ShouldReturnPaginationResponse() {
        Page<Production> page = new PageImpl<>(List.of(new Production()));
        Pageable pageable = mock(Pageable.class);
        List<ProductionDTO> productionDTOs = Collections.singletonList(new ProductionDTO());
        PaginationResponseDTO<ProductionDTO> response = new PaginationResponseDTO<>(0, 1, 1, productionDTOs);

        when(productionRepository.findAll(pageable)).thenReturn(page);
        when(productionMapper.toDTO(any())).thenReturn(productionDTOs.get(0));

        PaginationResponseDTO<ProductionDTO> result = productionService.findAllPaged(pageable);

        assertEquals(response.getData().size(), result.getData().size());
    }

    @Test
    void existsById_ShouldReturnTrue_WhenIdExists() {
        when(productionRepository.existsById(1L)).thenReturn(true);

        assertTrue(productionService.existsById(1L));
    }

    @Test
    void existsById_ShouldReturnFalse_WhenIdDoesNotExist() {
        when(productionRepository.existsById(1L)).thenReturn(false);

        assertFalse(productionService.existsById(1L));
    }

    @Test
    void partialUpdate_ShouldThrowException_WhenIdIsNull() {
        ProductionDTO productionDTO = ProductionDTO.builder().id(null).build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> productionService.partialUpdate(productionDTO));

        assertEquals("Id cannot be null", exception.getMessage());
    }

    @Test
    void partialUpdate_ShouldThrowException_WhenProductionDoesNotExist() {
        ProductionDTO productionDTO = ProductionDTO.builder().id(1L).build();

        when(productionRepository.existsById(1L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> productionService.partialUpdate(productionDTO));

        assertEquals("Production does not exist", exception.getMessage());
    }

    @Test
    void partialUpdate_ShouldUpdateProductionFields_WhenValid() {
        ProductionDTO productionDTO = ProductionDTO.builder().id(1L).build();
        Production existingProduction = new Production();
        Production updatedProduction = new Production();
        ProductionDTO updatedProductionDTO = new ProductionDTO();

        when(productionService.existsById(anyLong())).thenReturn(true);
        when(productionRepository.findById(1L)).thenReturn(Optional.of(existingProduction));
        when(productionMapper.partialUpdate(existingProduction, productionDTO)).thenReturn(updatedProduction);
        when(productionRepository.save(updatedProduction)).thenReturn(updatedProduction);
        when(productionMapper.toDTO(updatedProduction)).thenReturn(updatedProductionDTO);

        ProductionDTO result = productionService.partialUpdate(productionDTO);

        assertEquals(updatedProductionDTO, result);
    }
}