package com.avos.sipra.sipagri.services.cores;

import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.PlantationDTO;
import com.avos.sipra.sipagri.enums.PlantationStatus;
import org.springframework.data.domain.Pageable;

public interface PlantationService extends CrudService<PlantationDTO, Long> {
    PlantationDTO findByProductionsId(Long productionsId);

    PaginationResponseDTO<PlantationDTO> findAllPagedPlantationByPlanterSupervisor(Pageable pageable, Long supervisorId);

    /**
     * Retrieves a paginated list of plantations filtered by a specific village.
     *
     * @param pageable the pagination and sorting information such as page number, size, and sort order
     * @param village   the filtering criteria or parameters to identify plantations by village
     * @return a {@code PaginationResponseDTO} containing the paginated data including the list of plantations
     *         in the specified village, along with pagination metadata such as current page, total pages,
     *         and total elements
     */
    PaginationResponseDTO<PlantationDTO> findAllPagedByVillage(Pageable pageable, String village);

    PaginationResponseDTO<PlantationDTO> findAllPagedByVillage(Pageable pageable, String village, Long supervisorId);

    /**
     * Retrieves a paginated list of plantations filtered by a specific status.
     *
     * @param pageable the pagination and sorting information such as page number, size, and sort order
     * @param status the filtering criteria or parameters to identify plantations by status
     * @return a {@code PaginationResponseDTO} containing the paginated data including the list of plantations
     *         with the specified status, along with pagination metadata such as current page, total pages,
     *         and total elements
     */
    PaginationResponseDTO<PlantationDTO> findAllPagedByStatus(Pageable pageable, PlantationStatus status);

    PaginationResponseDTO<PlantationDTO> findAllPagedByStatus(Pageable pageable, PlantationStatus status, Long supervisorId);

    PaginationResponseDTO<PlantationDTO> findAllPagedByParams(Pageable pageable, String params, Long supervisorId);
}
