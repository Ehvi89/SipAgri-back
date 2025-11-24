package com.avos.sipra.sipagri.services.cores;

import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.PlantationDTO;
import com.avos.sipra.sipagri.enums.PlantationStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PlantationService extends CrudService<PlantationDTO, Long> {
    /**
     * Retrieves a plantation based on the specified production ID.
     *
     * @param productionsId the identifier of the production to locate the associated plantation
     * @return a {@code PlantationDTO} containing the details of the plantation associated with the given production ID
     */
    PlantationDTO findByProductionsId(Long productionsId);

    /**
     * Retrieves a paginated list of plantations that are supervised by a specific planter supervisor.
     *
     * @param pageable     the pagination and sorting information such as page number, size, and sort order
     * @param supervisorId the identifier of the supervisor to filter plantations associated with their supervision
     * @return a {@code PaginationResponseDTO} containing the paginated data, including the list of plantations
     *         supervised by the specified supervisor, along with pagination metadata such as current page,
     *         total pages, and total elements
     */
    PaginationResponseDTO<PlantationDTO> findAllPagedPlantationByPlanterSupervisor(Pageable pageable, Long supervisorId);

    /**
     * Retrieves a list of all plantations associated with a specific supervisor.
     *
     * @param supervisorId the identifier of the supervisor whose plantations are to be retrieved
     * @return a list of {@code PlantationDTO} representing the plantations supervised by the provided supervisor
     */
    List<PlantationDTO> findAll(Long supervisorId);

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

    /**
     * Retrieves a paginated list of plantations filtered by a specific village and supervised
     * by a specific supervisor.
     *
     * @param pageable     the pagination and sorting information such as page number, size, and sort order
     * @param village      the filtering criteria or parameter to identify plantations by village
     * @param supervisorId the identifier of the supervisor to filter plantations under their supervision
     * @return a {@code PaginationResponseDTO} containing the paginated data, including the list of plantations
     *         in the specified village and supervised by the given supervisor, along with pagination metadata
     *         such as current page, total pages, and total elements
     */
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

    /**
     * Retrieves a paginated list of plantations filtered by a specific status and associated with a given supervisor.
     *
     * @param pageable      the pagination and sorting information such as page number, size, and sort order
     * @param status        the filtering criteria to identify plantations by their status
     * @param supervisorId  the identifier of the supervisor to filter plantations associated with their supervision
     * @return a {@code PaginationResponseDTO} containing the paginated data, including the list of plantations
     *         with the specified status and associated with the given supervisor, along with pagination metadata
     *         such as current page, total pages, and total elements
     */
    PaginationResponseDTO<PlantationDTO> findAllPagedByStatus(Pageable pageable, PlantationStatus status, Long supervisorId);

    /**
     * Retrieves a paginated list of plantations based on the specified parameters
     * and supervised by a specific supervisor.
     *
     * @param pageable the pagination and sorting information such as page number, size, and sort order
     * @param params the filtering criteria or parameters used to query plantations
     * @param supervisorId the identifier of the supervisor to filter plantations associated with
     * @return a PaginationResponseDTO containing the paginated data, including the list of plantations,
     *         pagination metadata (e.g., current page, total pages, total elements), and other relevant information
     */
    PaginationResponseDTO<PlantationDTO> findAllPagedByParams(Pageable pageable, String params, Long supervisorId);
}
