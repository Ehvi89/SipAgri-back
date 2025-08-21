package com.avos.sipra.sipagri.services.cores.impl;

import com.avos.sipra.sipagri.entities.KitProduct;
import com.avos.sipra.sipagri.entities.Product;
import com.avos.sipra.sipagri.repositories.KitProductRepository;
import com.avos.sipra.sipagri.services.cores.ProductService;
import com.avos.sipra.sipagri.services.dtos.KitProductDTO;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.ProductDTO;
import com.avos.sipra.sipagri.services.mappers.KitProductMapper;
import com.avos.sipra.sipagri.services.mappers.ProductMapper;
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

class KitProductServiceImplTest {

    @Mock
    private KitProductMapper kitProductMapper;

    @Mock
    private KitProductRepository kitProductRepository;

    @Mock
    private ProductService productService;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private KitProductServiceImpl kitProductService;

    private KitProduct kitProduct;
    private KitProductDTO kitProductDTO;
    private ProductDTO productDTO;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        productDTO = new ProductDTO();
        productDTO.setId(10L);
        productDTO.setName("Engrais NPK");
        productDTO.setPrice(100.0);

        product = new Product();
        product.setId(10L);
        product.setName("Engrais NPK");
        product.setPrice(100.0);

        kitProduct = new KitProduct();
        kitProduct.setId(1L);
        kitProduct.setQuantity(5);
        kitProduct.setTotalCost(500.0);
        kitProduct.setProduct(product);

        kitProductDTO = new KitProductDTO();
        kitProductDTO.setId(1L);
        kitProductDTO.setQuantity(5);
        kitProductDTO.setProduct(productDTO);
        kitProductDTO.setTotalCost(500.0);
    }

    // ---------- SAVE ----------
    @Test
    void save_shouldCalculateTotalCost_whenNull() {
        kitProduct.setTotalCost(null);
        kitProductDTO.setTotalCost(null);

        when(kitProductMapper.toEntity(kitProductDTO)).thenReturn(kitProduct);
        when(productService.findOne(productDTO.getId())).thenReturn(productDTO);
        when(kitProductRepository.save(any())).thenReturn(kitProduct);
        when(kitProductMapper.toDTO(kitProduct)).thenReturn(kitProductDTO);

        KitProductDTO result = kitProductService.save(kitProductDTO);

        assertNotNull(result);
        assertEquals(500.0, result.getTotalCost());
        verify(productService, times(1)).findOne(productDTO.getId());
        verify(kitProductRepository, times(1)).save(any());
    }

    @Test
    void save_shouldThrow_whenProductIsNullOrQuantityIsNull() {
        kitProduct.setTotalCost(null);
        kitProductDTO.setTotalCost(null);
        kitProductDTO.setQuantity(null); // pas de quantité

        when(kitProductMapper.toEntity(kitProductDTO)).thenReturn(kitProduct);
        when(productService.findOne(productDTO.getId())).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> kitProductService.save(kitProductDTO));
    }

    // ---------- UPDATE ----------
    @Test
    void update_ok() {
        when(kitProductRepository.existsById(kitProductDTO.getId())).thenReturn(true);
        when(productMapper.toEntity(productDTO)).thenReturn(product);
        when(kitProductMapper.toEntity(kitProductDTO)).thenReturn(kitProduct);
        when(kitProductRepository.save(any())).thenReturn(kitProduct);
        when(kitProductMapper.toDTO(any())).thenReturn(kitProductDTO);

        KitProductDTO result = kitProductService.update(kitProductDTO);

        assertNotNull(result);
        assertEquals(kitProductDTO.getId(), result.getId());
        verify(kitProductRepository, times(1)).existsById(kitProductDTO.getId());
    }

    @Test
    void update_shouldThrow_whenDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> kitProductService.update(null));
    }

    @Test
    void update_shouldThrow_whenNotExists() {
        when(kitProductRepository.existsById(1L)).thenReturn(false);
        assertThrows(NullPointerException.class, () -> kitProductService.update(kitProductDTO));
    }

    // ---------- DELETE ----------
    @Test
    void delete_ok() {
        kitProductService.delete(1L);
        verify(kitProductRepository, times(1)).deleteById(1L);
    }

    // ---------- FIND ONE ----------
    @Test
    void findOne_found() {
        when(kitProductRepository.findById(1L)).thenReturn(Optional.of(kitProduct));
        when(kitProductMapper.toDTO(kitProduct)).thenReturn(kitProductDTO);

        KitProductDTO result = kitProductService.findOne(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findOne_notFound() {
        when(kitProductRepository.findById(1L)).thenReturn(Optional.empty());
        KitProductDTO result = kitProductService.findOne(1L);
        assertNull(result);
    }

    // ---------- FIND ALL ----------
    @Test
    void findAll_ok() {
        when(kitProductRepository.findAll()).thenReturn(List.of(kitProduct));
        when(kitProductMapper.toDTO(kitProduct)).thenReturn(kitProductDTO);

        List<KitProductDTO> result = kitProductService.findAll();

        assertEquals(1, result.size());
        assertEquals(kitProductDTO.getId(), result.getFirst().getId());
    }

    // ---------- FIND ALL PAGED ----------
    @Test
    void findAllPaged_ok() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<KitProduct> page = new PageImpl<>(List.of(kitProduct), pageable, 1);

        when(kitProductRepository.findAll(pageable)).thenReturn(page);
        when(kitProductMapper.toDTO(kitProduct)).thenReturn(kitProductDTO);

        PaginationResponseDTO<KitProductDTO> result = kitProductService.findAllPaged(pageable);

        assertEquals(1, result.getData().size());
        assertEquals(1, result.getTotalElements());
    }

    // ---------- PARTIAL UPDATE ----------
    @Test
    void partialUpdate_ok() {
        when(kitProductRepository.existsById(1L)).thenReturn(true);
        when(kitProductRepository.findById(1L)).thenReturn(Optional.of(kitProduct));
        when(kitProductMapper.partialUpdate(kitProduct, kitProductDTO)).thenReturn(kitProduct);
        when(kitProductRepository.save(any())).thenReturn(kitProduct);
        when(kitProductMapper.toDTO(kitProduct)).thenReturn(kitProductDTO);

        KitProductDTO result = kitProductService.partialUpdate(kitProductDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void partialUpdate_shouldThrow_whenDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> kitProductService.partialUpdate(null));
    }

    @Test
    void partialUpdate_shouldThrow_whenNotExists() {
        when(kitProductRepository.existsById(1L)).thenReturn(false);
        assertThrows(NullPointerException.class, () -> kitProductService.partialUpdate(kitProductDTO));
    }

    // ---------- EXISTS ----------
    @Test
    void existsById_ok() {
        when(kitProductRepository.existsById(1L)).thenReturn(true);
        assertTrue(kitProductService.existsById(1L));
    }
}
