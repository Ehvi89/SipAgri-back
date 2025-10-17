package com.avos.sipra.sipagri.services.cores;

import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.PlantationDTO;
import org.springframework.data.domain.Pageable;

public interface PlantationService extends CrudService<PlantationDTO, Long> {
    PlantationDTO findByProductionsId(Long productionsId);

    PaginationResponseDTO<PlantationDTO> findAllPagedPlantationByPlanterSupervisor(Pageable pageable, Long supervisorId);

    /**
     * Retrieves a paginated list of plantations filtered by a specific village.
     *
     * @param pageable the pagination and sorting information such as page number, size, and sort order
     * @param params   the filtering criteria or parameters to identify plantations by village
     * @return a {@code PaginationResponseDTO} containing the paginated data including the list of plantations
     *         in the specified village, along with pagination metadata such as current page, total pages,
     *         and total elements
     */
    PaginationResponseDTO<PlantationDTO> findAllPagedByVillage(Pageable pageable, String params);
}
