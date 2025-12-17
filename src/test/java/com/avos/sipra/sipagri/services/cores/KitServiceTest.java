package com.avos.sipra.sipagri.services.cores;

import com.avos.sipra.sipagri.entities.Kit;
import com.avos.sipra.sipagri.repositories.KitRepository;
import com.avos.sipra.sipagri.services.cores.impl.KitServiceImpl;
import com.avos.sipra.sipagri.services.dtos.*;
import com.avos.sipra.sipagri.services.mappers.KitMapper;
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

class KitServiceTest {

    @Mock
    private KitMapper kitMapper;

    @Mock
    private KitRepository kitRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private KitServiceImpl kitService;

    private Kit kit;
    private KitDTO kitDTO;
    private KitProductDTO kitProductDTO;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        productDTO = new ProductDTO();
        productDTO.setId(10L);
        productDTO.setName("Engrais NPK");
        productDTO.setPrice(100.0);

        kitProductDTO = new KitProductDTO();
        kitProductDTO.setId(1L);
        kitProductDTO.setQuantity(5);
        kitProductDTO.setProduct(productDTO);
        kitProductDTO.setTotalCost(null); // forcera le recalcul

        kitDTO = new KitDTO();
        kitDTO.setId(1L);
        kitDTO.setName("Kit Semis");
        kitDTO.setKitProducts(List.of(kitProductDTO));

        kit = new Kit();
        kit.setId(1L);
        kit.setName("Kit Semis");
        kit.setTotalCost(500.0);
    }

    // ---------- SAVE ----------
    @Test
    void save_shouldCalculateTotalCost() {
        when(productService.findOne(productDTO.getId())).thenReturn(productDTO);
        when(kitMapper.toEntity(kitDTO)).thenReturn(kit);
        when(kitRepository.save(kit)).thenReturn(kit);
        when(kitMapper.toDTO(kit)).thenReturn(kitDTO);

        KitDTO result = kitService.save(kitDTO);

        assertNotNull(result);
        assertEquals(500.0, kitProductDTO.getTotalCost());
        verify(productService, times(1)).findOne(productDTO.getId());
        verify(kitRepository, times(1)).save(kit);
    }

    // ---------- UPDATE ----------
    @Test
    void update_ok() {
        when(kitRepository.existsById(1L)).thenReturn(true);
        when(productService.findOne(productDTO.getId())).thenReturn(productDTO);
        when(kitMapper.toEntity(kitDTO)).thenReturn(kit);
        when(kitRepository.save(kit)).thenReturn(kit);
        when(kitMapper.toDTO(kit)).thenReturn(kitDTO);

        KitDTO result = kitService.update(kitDTO);

        assertNotNull(result);
        verify(kitRepository, times(1)).existsById(1L);
    }

    @Test
    void update_shouldThrow_whenIdNull() {
        kitDTO.setId(null);
        assertThrows(IllegalArgumentException.class, () -> kitService.update(kitDTO));
    }

    @Test
    void update_shouldThrow_whenNotExists() {
        when(kitRepository.existsById(1L)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> kitService.update(kitDTO));
    }

    // ---------- DELETE ----------
    @Test
    void delete_ok() {
        kitService.delete(1L);
        verify(kitRepository, times(1)).deleteById(1L);
    }

    // ---------- FIND ONE ----------
    @Test
    void findOne_found() {
        when(kitRepository.findById(1L)).thenReturn(Optional.of(kit));
        when(kitMapper.toDTO(kit)).thenReturn(kitDTO);

        KitDTO result = kitService.findOne(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findOne_shouldThrow_whenNotFound() {
        when(kitRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> kitService.findOne(1L));
    }

    // ---------- FIND ALL ----------
    @Test
    void findAll_ok() {
        when(kitRepository.findAll()).thenReturn(List.of(kit));
        when(kitMapper.toDTO(kit)).thenReturn(kitDTO);

        List<KitDTO> result = kitService.findAll();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    // ---------- FIND ALL PAGED ----------
    @Test
    void findAllPaged_ok() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Kit> page = new PageImpl<>(List.of(kit), pageable, 1);

        when(kitRepository.findAll(pageable)).thenReturn(page);
        when(kitMapper.toDTO(kit)).thenReturn(kitDTO);

        PaginationResponseDTO<KitDTO> result = kitService.findAllPaged(pageable);

        assertEquals(1, result.getData().size());
        assertEquals(1, result.getTotalElements());
    }

    // ---------- PARTIAL UPDATE ----------
    @Test
    void partialUpdate_ok() {
        when(kitRepository.existsById(1L)).thenReturn(true);
        when(kitRepository.findById(1L)).thenReturn(Optional.of(kit));
        when(kitMapper.partialUpdate(kit, kitDTO)).thenReturn(kit);
        when(kitRepository.save(kit)).thenReturn(kit);
        when(kitMapper.toDTO(kit)).thenReturn(kitDTO);

        KitDTO result = kitService.partialUpdate(kitDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void partialUpdate_shouldThrow_whenIdNull() {
        kitDTO.setId(null);
        assertThrows(IllegalArgumentException.class, () -> kitService.partialUpdate(kitDTO));
    }

    @Test
    void partialUpdate_shouldThrow_whenNotExists() {
        when(kitRepository.existsById(1L)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> kitService.partialUpdate(kitDTO));
    }

    // ---------- EXISTS ----------
    @Test
    void existsById_ok() {
        when(kitRepository.existsById(1L)).thenReturn(true);
        assertTrue(kitService.existsById(1L));
    }
}
