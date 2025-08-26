
package com.avos.sipra.sipagri.controllers;

import com.avos.sipra.sipagri.security.TestSecurityConfig;
import com.avos.sipra.sipagri.services.cores.KitService;
import com.avos.sipra.sipagri.services.dtos.KitDTO;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(KitController.class)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@DisplayName("Tests du contrôleur Kit")
class KitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KitService kitService;

    @Autowired
    private ObjectMapper objectMapper;

    private KitDTO validKitDTO;
    private KitDTO invalidKitDTO;

    @BeforeEach
    void setUp() {
        // Kit valide pour les tests
        validKitDTO = KitDTO.builder()
                .id(1L)
                .name("Kit Céréales")
                .description("Kit complet pour la culture de céréales")
                .totalCost(35000.0)
                .build();

        // Kit invalide (sans nom requis)
        invalidKitDTO = KitDTO.builder()
                .id(2L)
                .description("Kit sans nom")
                .totalCost(0.0)
                .build();
    }

    @Nested
    @DisplayName("Tests de pagination")
    class PaginationTests {

        @Test
        @DisplayName("Doit retourner une page de kits avec succès")
        void findAllPaged_ShouldReturnPagedData_WhenDataExists() throws Exception {
            // Given
            List<KitDTO> kits = List.of(validKitDTO);
            PaginationResponseDTO<KitDTO> response = new PaginationResponseDTO<>(0, 1, 1, kits);
            when(kitService.findAllPaged(any(Pageable.class))).thenReturn(response);

            // When & Then
            mockMvc.perform(get("/api/v1/kits")
                            .param("page", "0")
                            .param("size", "10"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.currentPage").value(0))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].id").value(1L))
                    .andExpect(jsonPath("$.data[0].name").value("Kit Céréales"))
                    .andExpect(jsonPath("$.data[0].totalCost").value(35000));

            verify(kitService).findAllPaged(any(Pageable.class));
        }

        @Test
        @DisplayName("Doit retourner 404 quand aucune donnée paginée n'existe")
        void findAllPaged_ShouldReturnNotFound_WhenNoData() throws Exception {
            // Given
            PaginationResponseDTO<KitDTO> response = new PaginationResponseDTO<>(0, 0, 0, Collections.emptyList());
            when(kitService.findAllPaged(any(Pageable.class))).thenReturn(response);

            // When & Then
            mockMvc.perform(get("/api/v1/kits"))
                    .andExpect(status().isNotFound());

            verify(kitService).findAllPaged(any(Pageable.class));
        }

        @Test
        @DisplayName("Doit gérer les paramètres de pagination personnalisés")
        void findAllPaged_ShouldHandleCustomPaginationParams() throws Exception {
            // Given
            PaginationResponseDTO<KitDTO> response = new PaginationResponseDTO<>(2, 5, 15, List.of(validKitDTO));
            when(kitService.findAllPaged(any(Pageable.class))).thenReturn(response);

            // When & Then
            mockMvc.perform(get("/api/v1/kits")
                            .param("page", "2")
                            .param("size", "5")
                            .param("sort", "name,asc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.currentPage").value(2))
                    .andExpect(jsonPath("$.totalElements").value(15))
                    .andExpect(jsonPath("$.totalPages").value(5));
        }
    }

    @Nested
    @DisplayName("Tests de récupération de tous les kits")
    class GetAllTests {

        @Test
        @DisplayName("Doit retourner la liste complète des kits")
        void getAll_ShouldReturnList_WhenDataExists() throws Exception {
            // Given
            List<KitDTO> kits = List.of(validKitDTO,
                    KitDTO.builder().id(2L).name("Kit Bio").build());
            when(kitService.findAll()).thenReturn(kits);

            // When & Then
            mockMvc.perform(get("/api/v1/kits/all"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[0].name").value("Kit Céréales"))
                    .andExpect(jsonPath("$[1].id").value(2L))
                    .andExpect(jsonPath("$[1].name").value("Kit Bio"));

            verify(kitService).findAll();
        }

        @Test
        @DisplayName("Doit retourner 404 quand la liste est vide")
        void getAll_ShouldReturnNotFound_WhenEmpty() throws Exception {
            // Given
            when(kitService.findAll()).thenReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/api/v1/kits/all"))
                    .andExpect(status().isNotFound());

            verify(kitService).findAll();
        }

        @Test
        @DisplayName("Doit retourner 404 quand le service retourne une liste vide")
        void getAll_ShouldReturnNotFound_WhenNull() throws Exception {
            // Given
            when(kitService.findAll()).thenReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/api/v1/kits/all"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Tests de récupération par ID")
    class GetByIdTests {

        @Test
        @DisplayName("Doit retourner un kit existant")
        void getById_ShouldReturnKit_WhenExists() throws Exception {
            // Given
            Long kitId = 1L;
            when(kitService.findOne(kitId)).thenReturn(validKitDTO);

            // When & Then
            mockMvc.perform(get("/api/v1/kits/{id}", kitId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(kitId))
                    .andExpect(jsonPath("$.name").value("Kit Céréales"))
                    .andExpect(jsonPath("$.description").value("Kit complet pour la culture de céréales"))
                    .andExpect(jsonPath("$.totalCost").value(35000));

            verify(kitService).findOne(kitId);
        }

        @Test
        @DisplayName("Doit retourner 404 quand le kit n'existe pas")
        void getById_ShouldReturnNotFound_WhenNotExists() throws Exception {
            // Given
            Long nonExistentId = 999L;
            when(kitService.findOne(nonExistentId)).thenReturn(null);

            // When & Then
            mockMvc.perform(get("/api/v1/kits/{id}", nonExistentId))
                    .andExpect(status().isNotFound());

            verify(kitService).findOne(nonExistentId);
        }

        @Test
        @DisplayName("Doit gérer les IDs invalides")
        void getById_ShouldHandleInvalidIds() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/kits/invalid-id"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(kitService);
        }
    }

    @Nested
    @DisplayName("Tests de création")
    class CreateTests {

        @Test
        @DisplayName("Doit créer un kit valide avec succès")
        void save_ShouldReturnCreated_WhenValidData() throws Exception {
            // Given
            KitDTO newKit = KitDTO.builder()
                    .name("Nouveau Kit")
                    .description("Description du nouveau kit")
                    .totalCost(25000.0)
                    .build();

            KitDTO savedKit = KitDTO.builder()
                    .id(3L)
                    .name("Nouveau Kit")
                    .description("Description du nouveau kit")
                    .totalCost(25000.0)
                    .build();

            when(kitService.save(any(KitDTO.class))).thenReturn(savedKit);

            // When & Then
            mockMvc.perform(post("/api/v1/kits")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newKit)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(3L))
                    .andExpect(jsonPath("$.name").value("Nouveau Kit"))
                    .andExpect(jsonPath("$.totalCost").value(25000));

            verify(kitService).save(any(KitDTO.class));
        }

        @Test
        @DisplayName("Doit retourner 404 quand la sauvegarde échoue")
        void save_ShouldReturnNotFound_WhenSaveFails() throws Exception {
            // Given
            when(kitService.save(any(KitDTO.class))).thenReturn(null);

            // When & Then
            mockMvc.perform(post("/api/v1/kits")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validKitDTO)))
                    .andExpect(status().isNotFound());

            verify(kitService).save(any(KitDTO.class));
        }

        @Test
        @DisplayName("Doit rejeter les données JSON invalides")
        void save_ShouldReturnBadRequest_WhenInvalidJson() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/v1/kits")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("\"invalid json\""))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(kitService);
        }
    }

    @Nested
    @DisplayName("Tests de mise à jour complète")
    class UpdateTests {

        @Test
        @DisplayName("Doit mettre à jour un kit avec succès")
        void update_ShouldReturnAccepted_WhenValidData() throws Exception {
            // Given
            KitDTO updatedKit = KitDTO.builder()
                    .id(1L)
                    .name("Kit Mis à jour")
                    .description("Description mise à jour")
                    .totalCost(40000.0)
                    .build();

            when(kitService.update(any(KitDTO.class))).thenReturn(updatedKit);

            // When & Then
            mockMvc.perform(put("/api/v1/kits")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedKit)))
                    .andExpect(status().isAccepted())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Kit Mis à jour"))
                    .andExpect(jsonPath("$.totalCost").value(40000));

            verify(kitService).update(any(KitDTO.class));
        }

        @Test
        @DisplayName("Doit retourner 404 quand la mise à jour échoue")
        void update_ShouldReturnNotFound_WhenUpdateFails() throws Exception {
            // Given
            when(kitService.update(any(KitDTO.class))).thenReturn(null);

            // When & Then
            mockMvc.perform(put("/api/v1/kits")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validKitDTO)))
                    .andExpect(status().isNotFound());

            verify(kitService).update(any(KitDTO.class));
        }
    }

    @Nested
    @DisplayName("Tests de mise à jour partielle")
    class PatchTests {

        @Test
        @DisplayName("Doit effectuer une mise à jour partielle avec succès")
        void patch_ShouldReturnAccepted_WhenValidData() throws Exception {
            // Given
            KitDTO partialUpdate = KitDTO.builder()
                    .id(1L)
                    .name("Nouveau nom seulement")
                    .build();

            KitDTO updatedKit = KitDTO.builder()
                    .id(1L)
                    .name("Nouveau nom seulement")
                    .description("Description originale conservée")
                    .totalCost(35000.0)
                    .build();

            when(kitService.partialUpdate(any(KitDTO.class))).thenReturn(updatedKit);

            // When & Then
            mockMvc.perform(patch("/api/v1/kits")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(partialUpdate)))
                    .andExpect(status().isAccepted())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Nouveau nom seulement"));

            verify(kitService).partialUpdate(any(KitDTO.class));
        }

        @Test
        @DisplayName("Doit retourner 404 quand la mise à jour partielle échoue")
        void patch_ShouldReturnNotFound_WhenPatchFails() throws Exception {
            // Given
            when(kitService.partialUpdate(any(KitDTO.class))).thenReturn(null);

            // When & Then
            mockMvc.perform(patch("/api/v1/kits")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validKitDTO)))
                    .andExpect(status().isNotFound());

            verify(kitService).partialUpdate(any(KitDTO.class));
        }
    }

    @Nested
    @DisplayName("Tests de suppression")
    class DeleteTests {

        @Test
        @DisplayName("Doit supprimer un kit existant avec succès")
        void delete_ShouldReturnOk_WhenKitExists() throws Exception {
            // Given
            Long kitId = 1L;
            when(kitService.findOne(kitId)).thenReturn(validKitDTO);
            doNothing().when(kitService).delete(kitId);

            // When & Then
            mockMvc.perform(delete("/api/v1/kits/{id}", kitId))
                    .andExpect(status().isOk());

            verify(kitService).findOne(kitId);
            verify(kitService).delete(kitId);
        }

        @Test
        @DisplayName("Doit retourner 404 quand le kit à supprimer n'existe pas")
        void delete_ShouldReturnNotFound_WhenKitNotExists() throws Exception {
            // Given
            Long nonExistentId = 999L;
            when(kitService.findOne(nonExistentId)).thenReturn(null);

            // When & Then
            mockMvc.perform(delete("/api/v1/kits/{id}", nonExistentId))
                    .andExpect(status().isNotFound());

            verify(kitService).findOne(nonExistentId);
            verify(kitService, never()).delete(nonExistentId);
        }

        @Test
        @DisplayName("Doit gérer les erreurs lors de la suppression")
        void delete_ShouldHandleDeleteErrors() throws Exception {
            // Given
            Long kitId = 1L;
            when(kitService.findOne(kitId)).thenReturn(validKitDTO);
            doThrow(new RuntimeException("Erreur de suppression")).when(kitService).delete(kitId);

            // When & Then
            mockMvc.perform(delete("/api/v1/kits/{id}", kitId))
                    .andExpect(status().isInternalServerError());

            verify(kitService).findOne(kitId);
            verify(kitService).delete(kitId);
        }
    }

    @Nested
    @DisplayName("Tests de gestion des erreurs")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Doit valider le Content-Type")
        void shouldValidateContentType() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/v1/kits")
                            .contentType(MediaType.TEXT_PLAIN)
                            .content("invalid content"))
                    .andExpect(status().isUnsupportedMediaType());

            verifyNoInteractions(kitService);
        }
    }
}