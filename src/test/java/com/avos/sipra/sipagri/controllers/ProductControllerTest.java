package com.avos.sipra.sipagri.controllers;

import com.avos.sipra.sipagri.services.cores.ProductService;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.ProductDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Test Product"); // adapte selon ton DTO
    }

    @Test
    void findProducts_ShouldReturnPagedData() throws Exception {
        PaginationResponseDTO<ProductDTO> response =
                new PaginationResponseDTO<>(0, 1, 1, List.of(productDTO));

        Mockito.when(productService.findAllPaged(any(Pageable.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/products?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1L));
    }

    @Test
    void findProducts_ShouldReturnNotFound_WhenDataIsNull() throws Exception {
        PaginationResponseDTO<ProductDTO> response =
                new PaginationResponseDTO<>(0, 0, 0, null);

        Mockito.when(productService.findAllPaged(any(Pageable.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAll_ShouldReturnList() throws Exception {
        Mockito.when(productService.findAll()).thenReturn(List.of(productDTO));

        mockMvc.perform(get("/api/v1/products/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void findAll_ShouldReturnNotFound_WhenNull() throws Exception {
        Mockito.when(productService.findAll()).thenReturn(null);

        mockMvc.perform(get("/api/v1/products/all"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_ShouldReturnProduct_WhenFound() throws Exception {
        Mockito.when(productService.findOne(1L)).thenReturn(productDTO);

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void findById_ShouldReturnNotFound_WhenNotExists() throws Exception {
        Mockito.when(productService.findOne(1L)).thenReturn(null);

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void save_ShouldReturnCreated() throws Exception {
        Mockito.when(productService.save(any(ProductDTO.class))).thenReturn(productDTO);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void save_ShouldReturnNotFound_WhenNull() throws Exception {
        Mockito.when(productService.save(any(ProductDTO.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_ShouldReturnAccepted() throws Exception {
        Mockito.when(productService.update(any(ProductDTO.class))).thenReturn(productDTO);

        mockMvc.perform(put("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void update_ShouldReturnNotFound_WhenNull() throws Exception {
        Mockito.when(productService.update(any(ProductDTO.class))).thenReturn(null);

        mockMvc.perform(put("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void patch_ShouldReturnAccepted() throws Exception {
        Mockito.when(productService.partialUpdate(any(ProductDTO.class))).thenReturn(productDTO);

        mockMvc.perform(patch("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void patch_ShouldReturnNotFound_WhenNull() throws Exception {
        Mockito.when(productService.partialUpdate(any(ProductDTO.class))).thenReturn(null);

        mockMvc.perform(patch("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_ShouldReturnOk_WhenExists() throws Exception {
        Mockito.when(productService.findOne(1L)).thenReturn(productDTO);
        doNothing().when(productService).delete(1L);

        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isOk());

        Mockito.verify(productService).delete(1L);
    }

    @Test
    void delete_ShouldReturnNotFound_WhenNotExists() throws Exception {
        Mockito.when(productService.findOne(1L)).thenReturn(null);

        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isNotFound());
    }
}
