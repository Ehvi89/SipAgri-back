package com.avos.sipra.sipagri.services.cores.impl;

import com.avos.sipra.sipagri.entities.Product;
import com.avos.sipra.sipagri.repositories.ProductRepository;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.ProductDTO;
import com.avos.sipra.sipagri.services.mappers.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    void setup() {
        product = new Product();
        product.setId(1L);
        product.setName("Engrais NPK");
        product.setPrice(15000.0);

        productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Engrais NPK");
        productDTO.setPrice(15000.0);
    }

    @Test
    void shouldSave() {
        when(productMapper.toEntity(productDTO)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        ProductDTO result = productService.save(productDTO);

        assertNotNull(result);
        assertEquals(productDTO.getId(), result.getId());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void shouldUpdate() {
        when(productRepository.existsById(productDTO.getId())).thenReturn(true);
        when(productMapper.toEntity(productDTO)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        ProductDTO result = productService.update(productDTO);

        assertEquals(productDTO.getId(), result.getId());
        verify(productRepository, times(1)).existsById(productDTO.getId());
    }

    @Test
    void update_shouldThrow_whenIdIsNull() {
        ProductDTO dto = new ProductDTO();
        dto.setId(null);

        assertThrows(IllegalArgumentException.class, () -> productService.update(dto));
    }

    @Test
    void update_shouldThrow_whenProductNotFound() {
        when(productRepository.existsById(productDTO.getId())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> productService.update(productDTO));
    }

    @Test
    void delete() {
        productService.delete(1L);
        Optional<Product> product1 = productRepository.findById(1L);
        assertFalse(product1.isPresent());
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldFindOne() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        ProductDTO result = productService.findOne(1L);

        assertNotNull(result);
        assertEquals("Engrais NPK", result.getName());
    }

    @Test
    void shouldNotFindOne() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ProductDTO result = productService.findOne(1L);

        assertNull(result);
    }

    @Test
    void shouldFindAll() {
        when(productRepository.findAll()).thenReturn(Collections.singletonList(product));
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        List<ProductDTO> result = productService.findAll();

        assertEquals(1, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void shouldFindAllPaged() {
        Page<Product> page = new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1);
        when(productRepository.findAll(any(PageRequest.class))).thenReturn(page);
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        PaginationResponseDTO<ProductDTO> response = productService.findAllPaged(PageRequest.of(0, 10));

        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getData().size());
    }

    @Test
    void shouldPartialUpdate() {
        when(productRepository.existsById(productDTO.getId())).thenReturn(true);
        when(productRepository.findById(productDTO.getId())).thenReturn(Optional.of(product));
        when(productMapper.partialUpdate(product, productDTO)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        ProductDTO result = productService.partialUpdate(productDTO);

        assertNotNull(result);
        assertEquals(productDTO.getId(), result.getId());
    }

    @Test
    void partialUpdate_shouldThrow_whenIdIsNull() {
        ProductDTO dto = new ProductDTO();
        dto.setId(null);

        assertThrows(IllegalArgumentException.class, () -> productService.partialUpdate(dto));
    }

    @Test
    void partialUpdate_shouldThrow_whenProductNotFound() {
        when(productRepository.existsById(productDTO.getId())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> productService.partialUpdate(productDTO));
    }

    @Test
    void existsById_ok() {
        when(productRepository.existsById(1L)).thenReturn(true);

        assertTrue(productService.existsById(1L));
    }
}
