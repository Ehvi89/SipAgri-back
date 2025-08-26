package com.avos.sipra.sipagri.controllers;

import com.avos.sipra.sipagri.security.TestSecurityConfig;
import com.avos.sipra.sipagri.services.cores.PlanterService;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.PlanterDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@WebMvcTest(PlanterController.class)
class PlanterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlanterService planterService;

    @Autowired
    private ObjectMapper objectMapper;

    private PlanterDTO planterDTO;

    @BeforeEach
    void setUp() {
        planterDTO = new PlanterDTO();
        planterDTO.setId(1L);
        planterDTO.setFirstname("Test Planter"); // adapte selon ton DTO
    }

    @Test
    void getPlanters_ShouldReturnPagedData() throws Exception {
        PaginationResponseDTO<PlanterDTO> response =
                new PaginationResponseDTO<>(0, 1, 1, List.of(planterDTO));

        Mockito.when(planterService.findAllPaged(any(Pageable.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/planters?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1L));
    }

    @Test
    void getPlanters_ShouldReturnNotFound_WhenDataIsNull() throws Exception {
        PaginationResponseDTO<PlanterDTO> response =
                new PaginationResponseDTO<>(0, 0, 0, null);

        Mockito.when(planterService.findAllPaged(any(Pageable.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/planters"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllPlanters_ShouldReturnList() throws Exception {
        Mockito.when(planterService.findAll()).thenReturn(List.of(planterDTO));

        mockMvc.perform(get("/api/v1/planters/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getAllPlanters_ShouldReturnNotFound_WhenEmpty() throws Exception {
        Mockito.when(planterService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/planters/all"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPlanterById_ShouldReturnPlanter_WhenFound() throws Exception {
        Mockito.when(planterService.findOne(1L)).thenReturn(planterDTO);

        mockMvc.perform(get("/api/v1/planters/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getPlanterById_ShouldReturnNotFound_WhenNotExists() throws Exception {
        Mockito.when(planterService.findOne(1L)).thenReturn(null);

        mockMvc.perform(get("/api/v1/planters/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createPlanter_ShouldReturnCreated() throws Exception {
        Mockito.when(planterService.save(any(PlanterDTO.class))).thenReturn(planterDTO);

        mockMvc.perform(post("/api/v1/planters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planterDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void createPlanter_ShouldReturnNotFound_WhenNull() throws Exception {
        Mockito.when(planterService.save(any(PlanterDTO.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/planters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planterDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePlanter_ShouldReturnAccepted() throws Exception {
        Mockito.when(planterService.update(any(PlanterDTO.class))).thenReturn(planterDTO);

        mockMvc.perform(put("/api/v1/planters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planterDTO)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updatePlanter_ShouldReturnNotFound_WhenNull() throws Exception {
        Mockito.when(planterService.update(any(PlanterDTO.class))).thenReturn(null);

        mockMvc.perform(put("/api/v1/planters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planterDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchPlanter_ShouldReturnAccepted() throws Exception {
        Mockito.when(planterService.update(any(PlanterDTO.class))).thenReturn(planterDTO);

        mockMvc.perform(patch("/api/v1/planters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planterDTO)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void patchPlanter_ShouldReturnNotFound_WhenNull() throws Exception {
        Mockito.when(planterService.update(any(PlanterDTO.class))).thenReturn(null);

        mockMvc.perform(patch("/api/v1/planters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planterDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePlanter_ShouldReturnOk_WhenExists() throws Exception {
        Mockito.when(planterService.findOne(1L)).thenReturn(planterDTO);
        doNothing().when(planterService).delete(1L);

        mockMvc.perform(delete("/api/v1/planters/1"))
                .andExpect(status().isOk());

        Mockito.verify(planterService).delete(1L);
    }

    @Test
    void deletePlanter_ShouldReturnNotFound_WhenNotExists() throws Exception {
        Mockito.when(planterService.findOne(1L)).thenReturn(null);

        mockMvc.perform(delete("/api/v1/planters/1"))
                .andExpect(status().isNotFound());
    }
}
