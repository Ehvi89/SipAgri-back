package com.avos.sipra.sipagri.controllers;

import com.avos.sipra.sipagri.services.cores.ProductionService;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.ProductionDTO;
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

@WebMvcTest(ProductionController.class)
class ProductionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductionService productionService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductionDTO productionDTO;

    @BeforeEach
    void setUp() {
        productionDTO = new ProductionDTO();
        productionDTO.setId(1L);
        productionDTO.setProductionInKg(10.5); // adapte selon ton DTO
    }

    @Test
    void findProductions_ShouldReturnPagedData() throws Exception {
        PaginationResponseDTO<ProductionDTO> response =
                new PaginationResponseDTO<>(0, 1, 1, List.of(productionDTO));

        Mockito.when(productionService.findAllPaged(any(Pageable.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/productions?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1L));
    }

    @Test
    void findProductions_ShouldReturnNotFound_WhenDataIsNull() throws Exception {
        PaginationResponseDTO<ProductionDTO> response =
                new PaginationResponseDTO<>(0, 0, 0, null);

        Mockito.when(productionService.findAllPaged(any(Pageable.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/productions"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAll_ShouldReturnList() throws Exception {
        Mockito.when(productionService.findAll()).thenReturn(List.of(productionDTO));

        mockMvc.perform(get("/api/v1/productions/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void findAll_ShouldReturnNotFound_WhenNull() throws Exception {
        Mockito.when(productionService.findAll()).thenReturn(null);

        mockMvc.perform(get("/api/v1/productions/all"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_ShouldReturnProduction_WhenFound() throws Exception {
        Mockito.when(productionService.findOne(1L)).thenReturn(productionDTO);

        mockMvc.perform(get("/api/v1/productions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void findById_ShouldReturnNotFound_WhenNotExists() throws Exception {
        Mockito.when(productionService.findOne(1L)).thenReturn(null);

        mockMvc.perform(get("/api/v1/productions/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void save_ShouldReturnCreated() throws Exception {
        Mockito.when(productionService.save(any(ProductionDTO.class))).thenReturn(productionDTO);

        mockMvc.perform(post("/api/v1/productions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productionDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void save_ShouldReturnNotFound_WhenNull() throws Exception {
        Mockito.when(productionService.save(any(ProductionDTO.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/productions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productionDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_ShouldReturnAccepted() throws Exception {
        Mockito.when(productionService.update(any(ProductionDTO.class))).thenReturn(productionDTO);

        mockMvc.perform(put("/api/v1/productions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productionDTO)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void update_ShouldReturnNotFound_WhenNull() throws Exception {
        Mockito.when(productionService.update(any(ProductionDTO.class))).thenReturn(null);

        mockMvc.perform(put("/api/v1/productions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productionDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void patch_ShouldReturnAccepted() throws Exception {
        Mockito.when(productionService.partialUpdate(any(ProductionDTO.class))).thenReturn(productionDTO);

        mockMvc.perform(patch("/api/v1/productions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productionDTO)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void patch_ShouldReturnNotFound_WhenNull() throws Exception {
        Mockito.when(productionService.partialUpdate(any(ProductionDTO.class))).thenReturn(null);

        mockMvc.perform(patch("/api/v1/productions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productionDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_ShouldReturnOk_WhenExists() throws Exception {
        Mockito.when(productionService.findOne(1L)).thenReturn(productionDTO);
        doNothing().when(productionService).delete(1L);

        mockMvc.perform(delete("/api/v1/productions/1"))
                .andExpect(status().isOk());

        Mockito.verify(productionService).delete(1L);
    }

    @Test
    void delete_ShouldReturnNotFound_WhenNotExists() throws Exception {
        Mockito.when(productionService.findOne(1L)).thenReturn(null);

        mockMvc.perform(delete("/api/v1/productions/1"))
                .andExpect(status().isNotFound());
    }
}
