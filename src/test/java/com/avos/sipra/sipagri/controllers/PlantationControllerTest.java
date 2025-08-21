package com.avos.sipra.sipagri.controllers;

import com.avos.sipra.sipagri.services.cores.PlantationService;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.PlantationDTO;
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

@WebMvcTest(PlantationController.class)
class PlantationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlantationService plantationService;

    @Autowired
    private ObjectMapper objectMapper;

    private PlantationDTO plantationDTO;

    @BeforeEach
    void setUp() {
        plantationDTO = new PlantationDTO();
        plantationDTO.setId(1L);
        plantationDTO.setFarmedArea(2.6); // adapte selon ton DTO
    }

    @Test
    void getAll_ShouldReturnPagedData() throws Exception {
        PaginationResponseDTO<PlantationDTO> response =
                new PaginationResponseDTO<>(0, 1, 1, List.of(plantationDTO));

        Mockito.when(plantationService.findAllPaged(any(Pageable.class)))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/plantations?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1L));
    }

    @Test
    void getAll_ShouldReturnNotFound_WhenDataIsNull() throws Exception {
        PaginationResponseDTO<PlantationDTO> response =
                new PaginationResponseDTO<>(0, 0, 0, null);

        Mockito.when(plantationService.findAllPaged(any(Pageable.class)))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/plantations"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPlantation_ShouldReturnPlantation_WhenFound() throws Exception {
        Mockito.when(plantationService.findOne(1L)).thenReturn(plantationDTO);

        mockMvc.perform(get("/api/v1/plantations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getPlantation_ShouldReturnNotFound_WhenNotExists() throws Exception {
        Mockito.when(plantationService.findOne(1L)).thenReturn(null);

        mockMvc.perform(get("/api/v1/plantations/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllPlantations_ShouldReturnList() throws Exception {
        Mockito.when(plantationService.findAll()).thenReturn(List.of(plantationDTO));

        mockMvc.perform(get("/api/v1/plantations/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getAllPlantations_ShouldReturnNotFound_WhenNull() throws Exception {
        Mockito.when(plantationService.findAll()).thenReturn(null);

        mockMvc.perform(get("/api/v1/plantations/all"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createPlantation_ShouldReturnCreated() throws Exception {
        Mockito.when(plantationService.save(any(PlantationDTO.class))).thenReturn(plantationDTO);

        mockMvc.perform(post("/api/v1/plantations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plantationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void createPlantation_ShouldReturnNotFound_WhenNull() throws Exception {
        Mockito.when(plantationService.save(any(PlantationDTO.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/plantations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plantationDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePlantation_ShouldReturnAccepted() throws Exception {
        Mockito.when(plantationService.update(any(PlantationDTO.class))).thenReturn(plantationDTO);

        mockMvc.perform(put("/api/v1/plantations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plantationDTO)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updatePlantation_ShouldReturnNotFound_WhenNull() throws Exception {
        Mockito.when(plantationService.update(any(PlantationDTO.class))).thenReturn(null);

        mockMvc.perform(put("/api/v1/plantations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plantationDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchPlantation_ShouldReturnAccepted() throws Exception {
        Mockito.when(plantationService.partialUpdate(any(PlantationDTO.class))).thenReturn(plantationDTO);

        mockMvc.perform(patch("/api/v1/plantations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plantationDTO)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void patchPlantation_ShouldReturnNotFound_WhenNull() throws Exception {
        Mockito.when(plantationService.partialUpdate(any(PlantationDTO.class))).thenReturn(null);

        mockMvc.perform(patch("/api/v1/plantations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plantationDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePlantation_ShouldReturnOk_WhenExists() throws Exception {
        Mockito.when(plantationService.findOne(1L)).thenReturn(plantationDTO);
        doNothing().when(plantationService).delete(1L);

        mockMvc.perform(delete("/api/v1/plantations?id=1"))
                .andExpect(status().isOk());

        Mockito.verify(plantationService).delete(1L);
    }

    @Test
    void deletePlantation_ShouldReturnNotFound_WhenNotExists() throws Exception {
        Mockito.when(plantationService.findOne(1L)).thenReturn(null);

        mockMvc.perform(delete("/api/v1/plantations?id=1"))
                .andExpect(status().isNotFound());
    }
}
