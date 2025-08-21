package com.avos.sipra.sipagri.controllers;

import com.avos.sipra.sipagri.services.cores.SupervisorService;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.SupervisorDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SupervisorController.class)
class SupervisorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SupervisorService supervisorService;

    @Autowired
    private ObjectMapper objectMapper;

    private SupervisorDTO supervisorDTO;

    @BeforeEach
    void setUp() {
        supervisorDTO = new SupervisorDTO();
        supervisorDTO.setId(1L);
        supervisorDTO.setFirstname("Supervisor Test"); // adapte selon ton DTO
    }

    @Test
    void findSupervisors_ShouldReturnPagedData() throws Exception {
        PaginationResponseDTO<SupervisorDTO> response =
                new PaginationResponseDTO<>(0, 1, 1, List.of(supervisorDTO));

        Mockito.when(supervisorService.findAllPaged(any(Pageable.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/supervisors?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1L));
    }

    @Test
    void findSupervisors_ShouldReturnNotFound_WhenDataIsNull() throws Exception {
        PaginationResponseDTO<SupervisorDTO> response =
                new PaginationResponseDTO<>(0, 0, 0, null);

        Mockito.when(supervisorService.findAllPaged(any(Pageable.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/supervisors"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAll_ShouldReturnList() throws Exception {
        Mockito.when(supervisorService.findAll()).thenReturn(List.of(supervisorDTO));

        mockMvc.perform(get("/api/v1/supervisors/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void findAll_ShouldReturnNotFound_WhenNull() throws Exception {
        Mockito.when(supervisorService.findAll()).thenReturn(null);

        mockMvc.perform(get("/api/v1/supervisors/all"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_ShouldReturnSupervisor_WhenFound() throws Exception {
        Mockito.when(supervisorService.findOne(1L)).thenReturn(supervisorDTO);

        mockMvc.perform(get("/api/v1/supervisors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void findById_ShouldReturnNotFound_WhenNotExists() throws Exception {
        Mockito.when(supervisorService.findOne(1L)).thenReturn(null);

        mockMvc.perform(get("/api/v1/supervisors/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void save_ShouldReturnCreated() throws Exception {
        Mockito.when(supervisorService.save(any(SupervisorDTO.class))).thenReturn(supervisorDTO);

        mockMvc.perform(post("/api/v1/supervisors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supervisorDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void save_ShouldReturnNotFound_WhenNull() throws Exception {
        Mockito.when(supervisorService.save(any(SupervisorDTO.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/supervisors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supervisorDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_ShouldReturnAccepted() throws Exception {
        Mockito.when(supervisorService.update(any(SupervisorDTO.class))).thenReturn(supervisorDTO);

        mockMvc.perform(put("/api/v1/supervisors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supervisorDTO)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void update_ShouldReturnNotFound_WhenNull() throws Exception {
        Mockito.when(supervisorService.update(any(SupervisorDTO.class))).thenReturn(null);

        mockMvc.perform(put("/api/v1/supervisors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supervisorDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void patch_ShouldReturnAccepted() throws Exception {
        Mockito.when(supervisorService.partialUpdate(any(SupervisorDTO.class))).thenReturn(supervisorDTO);

        mockMvc.perform(patch("/api/v1/supervisors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supervisorDTO)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void patch_ShouldReturnNotFound_WhenNull() throws Exception {
        Mockito.when(supervisorService.partialUpdate(any(SupervisorDTO.class))).thenReturn(null);

        mockMvc.perform(patch("/api/v1/supervisors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supervisorDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_ShouldReturnOk_WhenExists() throws Exception {
        Mockito.when(supervisorService.findOne(1L)).thenReturn(supervisorDTO);
        doNothing().when(supervisorService).delete(1L);

        mockMvc.perform(delete("/api/v1/supervisors/1"))
                .andExpect(status().isOk());

        Mockito.verify(supervisorService).delete(1L);
    }

    @Test
    void delete_ShouldReturnNotFound_WhenNotExists() throws Exception {
        Mockito.when(supervisorService.findOne(1L)).thenReturn(null);

        mockMvc.perform(delete("/api/v1/supervisors/1"))
                .andExpect(status().isNotFound());
    }
}
