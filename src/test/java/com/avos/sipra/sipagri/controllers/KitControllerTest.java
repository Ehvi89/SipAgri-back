package com.avos.sipra.sipagri.controllers;

import com.avos.sipra.sipagri.services.cores.KitService;
import com.avos.sipra.sipagri.services.dtos.KitDTO;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
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

@WebMvcTest(KitController.class)
class KitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KitService kitService;

    @Autowired
    private ObjectMapper objectMapper;

    private KitDTO kitDTO;

    @BeforeEach
    void setUp() {
        kitDTO = new KitDTO();
        kitDTO.setId(1L);
        kitDTO.setName("Test Kit"); // adapte selon ton DTO
    }

    @Test
    void findAllPaged_ShouldReturnPagedData() throws Exception {
        PaginationResponseDTO<KitDTO> response =
                new PaginationResponseDTO<>(0, 1, 1, List.of(kitDTO));

        Mockito.when(kitService.findAllPaged(any(Pageable.class)))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/kits?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1L));
    }

    @Test
    void findAllPaged_ShouldReturnNotFound_WhenDataIsNull() throws Exception {
        PaginationResponseDTO<KitDTO> response =
                new PaginationResponseDTO<>(0, 0, 0, null);

        Mockito.when(kitService.findAllPaged(any(Pageable.class)))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/kits"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_ShouldReturnList() throws Exception {
        Mockito.when(kitService.findAll()).thenReturn(List.of(kitDTO));

        mockMvc.perform(get("/api/v1/kits/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getAll_ShouldReturnNotFound_WhenEmpty() throws Exception {
        Mockito.when(kitService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/kits/all"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById_ShouldReturnKit() throws Exception {
        Mockito.when(kitService.findOne(1L)).thenReturn(kitDTO);

        mockMvc.perform(get("/api/v1/kits/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getById_ShouldReturnNotFound() throws Exception {
        Mockito.when(kitService.findOne(1L)).thenReturn(null);

        mockMvc.perform(get("/api/v1/kits/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void save_ShouldReturnCreated() throws Exception {
        Mockito.when(kitService.save(any(KitDTO.class))).thenReturn(kitDTO);

        mockMvc.perform(post("/api/v1/kits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(kitDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void save_ShouldReturnNotFound_WhenNull() throws Exception {
        Mockito.when(kitService.save(any(KitDTO.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/kits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(kitDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_ShouldReturnAccepted() throws Exception {
        Mockito.when(kitService.update(any(KitDTO.class))).thenReturn(kitDTO);

        mockMvc.perform(put("/api/v1/kits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(kitDTO)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void update_ShouldReturnNotFound_WhenNull() throws Exception {
        Mockito.when(kitService.update(any(KitDTO.class))).thenReturn(null);

        mockMvc.perform(put("/api/v1/kits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(kitDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void patch_ShouldReturnAccepted() throws Exception {
        Mockito.when(kitService.partialUpdate(any(KitDTO.class))).thenReturn(kitDTO);

        mockMvc.perform(patch("/api/v1/kits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(kitDTO)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void patch_ShouldReturnNotFound_WhenNull() throws Exception {
        Mockito.when(kitService.partialUpdate(any(KitDTO.class))).thenReturn(null);

        mockMvc.perform(patch("/api/v1/kits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(kitDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_ShouldReturnOk_WhenExists() throws Exception {
        Mockito.when(kitService.findOne(1L)).thenReturn(kitDTO);
        doNothing().when(kitService).delete(1L);

        mockMvc.perform(delete("/api/v1/kits/1"))
                .andExpect(status().isOk());

        Mockito.verify(kitService).delete(1L);
    }

    @Test
    void delete_ShouldReturnNotFound_WhenNotExists() throws Exception {
        Mockito.when(kitService.findOne(1L)).thenReturn(null);

        mockMvc.perform(delete("/api/v1/kits/1"))
                .andExpect(status().isNotFound());
    }
}
