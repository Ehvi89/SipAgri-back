package com.avos.sipra.sipagri.services.cores.impl;

import com.avos.sipra.sipagri.entities.Supervisor;
import com.avos.sipra.sipagri.repositories.SupervisorRepository;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.SupervisorDTO;
import com.avos.sipra.sipagri.services.mappers.SupervisorMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SupervisorServiceImplTest {

    @Mock
    private SupervisorRepository supervisorRepository;

    @Mock
    private SupervisorMapper supervisorMapper;

    @InjectMocks
    private SupervisorServiceImpl supervisorService;

    private Supervisor supervisor;
    private SupervisorDTO supervisorDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        supervisor = new Supervisor();
        supervisor.setId(1L);
        supervisorDTO = new SupervisorDTO();
        supervisorDTO.setId(1L);
    }

    @Test
    void save_ShouldReturnSavedSupervisorDTO() {
        when(supervisorMapper.toEntity(supervisorDTO)).thenReturn(supervisor);
        when(supervisorRepository.save(supervisor)).thenReturn(supervisor);
        when(supervisorMapper.toDTO(supervisor)).thenReturn(supervisorDTO);

        SupervisorDTO result = supervisorService.save(supervisorDTO);

        assertNotNull(result);
        assertEquals(supervisorDTO.getId(), result.getId());
        verify(supervisorRepository).save(supervisor);
    }

    @Test
    void update_ShouldThrowException_WhenDTOIsNull() {
        assertThrows(IllegalArgumentException.class, () -> supervisorService.update(null));
    }

    @Test
    void update_ShouldThrowException_WhenIdDoesNotExist() {
        when(supervisorRepository.existsById(1L)).thenReturn(false);
        assertThrows(NullPointerException.class, () -> supervisorService.update(supervisorDTO));
    }

    @Test
    void update_ShouldCallSave_WhenDTOIsValid() {
        when(supervisorRepository.existsById(1L)).thenReturn(true);
        when(supervisorMapper.toEntity(supervisorDTO)).thenReturn(supervisor);
        when(supervisorRepository.save(supervisor)).thenReturn(supervisor);
        when(supervisorMapper.toDTO(supervisor)).thenReturn(supervisorDTO);

        SupervisorDTO result = supervisorService.update(supervisorDTO);

        assertNotNull(result);
        verify(supervisorRepository).save(supervisor);
    }

    @Test
    void delete_ShouldCallRepositoryDelete() {
        supervisorService.delete(1L);
        verify(supervisorRepository).deleteById(1L);
    }

    @Test
    void findOne_ShouldReturnDTO_WhenFound() {
        when(supervisorRepository.findById(1L)).thenReturn(Optional.of(supervisor));
        when(supervisorMapper.toDTO(supervisor)).thenReturn(supervisorDTO);

        SupervisorDTO result = supervisorService.findOne(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findOne_ShouldReturnNull_WhenNotFound() {
        when(supervisorRepository.findById(1L)).thenReturn(Optional.empty());

        SupervisorDTO result = supervisorService.findOne(1L);

        assertNull(result);
    }

    @Test
    void findAll_ShouldReturnAllDTOs() {
        List<Supervisor> supervisors = Collections.singletonList(supervisor);
        when(supervisorRepository.findAll()).thenReturn(supervisors);
        when(supervisorMapper.toDTO(supervisor)).thenReturn(supervisorDTO);

        List<SupervisorDTO> result = supervisorService.findAll();

        assertEquals(1, result.size());
        verify(supervisorRepository).findAll();
    }

    @Test
    void findAllPaged_ShouldReturnPaginationDTO() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Supervisor> page = new PageImpl<>(List.of(supervisor), pageable, 1);
        when(supervisorRepository.findAll(pageable)).thenReturn(page);
        when(supervisorMapper.toDTO(supervisor)).thenReturn(supervisorDTO);

        PaginationResponseDTO<SupervisorDTO> result = supervisorService.findAllPaged(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getData().size());
    }

    @Test
    void partialUpdate_ShouldThrowException_WhenDTOIsNull() {
        assertThrows(IllegalArgumentException.class, () -> supervisorService.partialUpdate(null));
    }

    @Test
    void partialUpdate_ShouldThrowException_WhenIdNotFound() {
        when(supervisorRepository.existsById(1L)).thenReturn(false);
        assertThrows(NullPointerException.class, () -> supervisorService.partialUpdate(supervisorDTO));
    }

    @Test
    void partialUpdate_ShouldReturnUpdatedDTO_WhenExists() {
        when(supervisorRepository.existsById(1L)).thenReturn(true);
        when(supervisorRepository.findById(1L)).thenReturn(Optional.of(supervisor));
        when(supervisorMapper.partialUpdate(supervisor, supervisorDTO)).thenReturn(supervisor);
        when(supervisorRepository.save(supervisor)).thenReturn(supervisor);
        when(supervisorMapper.toDTO(supervisor)).thenReturn(supervisorDTO);

        SupervisorDTO result = supervisorService.partialUpdate(supervisorDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void existsById_ShouldReturnTrue_WhenExists() {
        when(supervisorRepository.existsById(1L)).thenReturn(true);

        Boolean result = supervisorService.existsById(1L);

        assertTrue(result);
    }

    @Test
    void existsById_ShouldReturnFalse_WhenNotExists() {
        when(supervisorRepository.existsById(1L)).thenReturn(false);

        Boolean result = supervisorService.existsById(1L);

        assertFalse(result);
    }
}
