package com.avos.sipra.sipagri.services.cores.impl;

import com.avos.sipra.sipagri.entities.Planter;
import com.avos.sipra.sipagri.repositories.PlanterRepository;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.PlanterDTO;
import com.avos.sipra.sipagri.services.mappers.PlanterMapper;
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

class PlanterServiceImplTest {

    @Mock
    private PlanterMapper planterMapper;

    @Mock
    private PlanterRepository planterRepository;

    @InjectMocks
    private PlanterServiceImpl planterService;

    private Planter planter;
    private PlanterDTO planterDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        planter = new Planter();
        planter.setId(1L);
        planter.setFirstname("Planter A");

        planterDTO = new PlanterDTO();
        planterDTO.setId(1L);
        planterDTO.setFirstname("Planter A");
    }

    // ---------- SAVE ----------
    @Test
    void save_shouldReturnDTO() {
        when(planterMapper.toEntity(planterDTO)).thenReturn(planter);
        when(planterRepository.save(planter)).thenReturn(planter);
        when(planterMapper.toDTO(planter)).thenReturn(planterDTO);

        PlanterDTO result = planterService.save(planterDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(planterRepository, times(1)).save(planter);
    }

    // ---------- UPDATE ----------
    @Test
    void update_ok() {
        when(planterRepository.existsById(1L)).thenReturn(true);
        when(planterMapper.toEntity(planterDTO)).thenReturn(planter);
        when(planterRepository.save(planter)).thenReturn(planter);
        when(planterMapper.toDTO(planter)).thenReturn(planterDTO);

        PlanterDTO result = planterService.update(planterDTO);

        assertNotNull(result);
        verify(planterRepository, times(1)).existsById(1L);
        verify(planterRepository, times(1)).save(planter);
    }

    @Test
    void update_shouldThrow_whenIdNull() {
        planterDTO.setId(null);
        assertThrows(IllegalArgumentException.class, () -> planterService.update(planterDTO));
    }

    @Test
    void update_shouldThrow_whenNotExists() {
        when(planterRepository.existsById(1L)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> planterService.update(planterDTO));
    }

    // ---------- DELETE ----------
    @Test
    void delete_ok() {
        planterService.delete(1L);
        verify(planterRepository, times(1)).deleteById(1L);
    }

    // ---------- FIND ONE ----------
    @Test
    void findOne_found() {
        when(planterRepository.findById(1L)).thenReturn(Optional.of(planter));
        when(planterMapper.toDTO(planter)).thenReturn(planterDTO);

        PlanterDTO result = planterService.findOne(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findOne_shouldThrow_whenNotFound() {
        when(planterRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NullPointerException.class, () -> planterService.findOne(1L));
    }

    // ---------- FIND ALL ----------
    @Test
    void findAll_ok() {
        when(planterRepository.findAll()).thenReturn(List.of(planter));
        when(planterMapper.toDTO(planter)).thenReturn(planterDTO);

        List<PlanterDTO> result = planterService.findAll();

        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getId());
    }

    // ---------- FIND ALL PAGED ----------
    @Test
    void findAllPaged_ok() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Planter> page = new PageImpl<>(List.of(planter), pageable, 1);

        when(planterRepository.findAll(pageable)).thenReturn(page);
        when(planterMapper.toDTO(planter)).thenReturn(planterDTO);

        PaginationResponseDTO<PlanterDTO> result = planterService.findAllPaged(pageable);

        assertEquals(1, result.getData().size());
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getCurrentPage());
        assertEquals(1, result.getTotalPages());
    }

    // ---------- PARTIAL UPDATE ----------
    @Test
    void partialUpdate_ok() {
        when(planterRepository.existsById(1L)).thenReturn(true);
        when(planterRepository.findById(1L)).thenReturn(Optional.of(planter));
        when(planterMapper.partialUpdate(planter, planterDTO)).thenReturn(planter);
        when(planterRepository.save(planter)).thenReturn(planter);
        when(planterMapper.toDTO(planter)).thenReturn(planterDTO);

        PlanterDTO result = planterService.partialUpdate(planterDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void partialUpdate_shouldThrow_whenIdNull() {
        planterDTO.setId(null);
        assertThrows(IllegalArgumentException.class, () -> planterService.partialUpdate(planterDTO));
    }

    @Test
    void partialUpdate_shouldThrow_whenNotExists() {
        when(planterRepository.existsById(1L)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> planterService.partialUpdate(planterDTO));
    }

    // ---------- EXISTS ----------
    @Test
    void existsById_ok() {
        when(planterRepository.existsById(1L)).thenReturn(true);
        assertTrue(planterService.existsById(1L));
    }
}
