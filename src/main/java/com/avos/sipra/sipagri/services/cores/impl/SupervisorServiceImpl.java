package com.avos.sipra.sipagri.services.cores.impl;

import com.avos.sipra.sipagri.entities.Supervisor;
import com.avos.sipra.sipagri.enums.SupervisorProfile;
import com.avos.sipra.sipagri.repositories.SupervisorRepository;
import com.avos.sipra.sipagri.services.cores.SupervisorService;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.SupervisorDTO;
import com.avos.sipra.sipagri.services.mappers.SupervisorMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of the SupervisorService interface to handle business logic
 * related to supervisors. This class provides functionality for saving, updating,
 * deleting, and retrieving supervisor details, along with handling pagination and
 * partial updates. It utilizes {@link SupervisorMapper} for mapping between entities
 * and data transfer objects (DTOs) and interacts with the {@link SupervisorRepository}
 * for database operations.
 * <p>
 * Dependencies:
 * - SupervisorMapper: Used for mapping between Supervisor entities and SupervisorDTOs.
 * - SupervisorRepository: Handles data access operations for Supervisor entities.
 */
@Service
public class SupervisorServiceImpl implements SupervisorService {
    /**
     *
     */
    private final SupervisorMapper supervisorMapper;
    /**
     * Repository interface for accessing and managing Supervisor entities in the data layer.
     * Provides methods for performing CRUD operations and custom queries related to Supervisors.
     * Used as a dependency in the SupervisorServiceImpl class to handle persistence operations.
     */
    private final SupervisorRepository supervisorRepository;

    /**
     * Constructs a new instance of SupervisorServiceImpl with the specified dependencies.
     *
     * @param supervisorMapper the mapper responsible for converting between Supervisor entities and DTOs
     * @param supervisorRepository the repository responsible for managing Supervisor entities
     */
    public SupervisorServiceImpl(SupervisorMapper supervisorMapper, SupervisorRepository supervisorRepository) {
        this.supervisorRepository = supervisorRepository;
        this.supervisorMapper = supervisorMapper;
    }

    /**
     * Saves a SupervisorDTO by mapping it to an entity, persisting it in the database,
     * and then mapping it back to a DTO.
     *
     * @param supervisorDTO the SupervisorDTO object to be saved
     * @return the saved SupervisorDTO object after persistence
     */
    @Override
    public SupervisorDTO save(SupervisorDTO supervisorDTO) {
        Supervisor supervisor = supervisorMapper.toEntity(supervisorDTO);
        supervisor = supervisorRepository.save(supervisor);
        return supervisorMapper.toDTO(supervisor);
    }

    /**
     * Updates an existing Supervisor record using the provided SupervisorDTO object.
     *
     * @param supervisorDTO the SupervisorDTO object containing updated information
     * @return the updated SupervisorDTO object after persistence
     * @throws IllegalArgumentException if the provided SupervisorDTO is null
     * @throws NullPointerException if the ID in the provided SupervisorDTO does not exist
     */
    @Override
    public SupervisorDTO update(SupervisorDTO supervisorDTO) {
        if (Objects.isNull(supervisorDTO)) {throw new IllegalArgumentException("SupervisorDTO is null");}
        if (Boolean.FALSE.equals(existsById(supervisorDTO.getId()))) {throw new NullPointerException("ID cannot be null");}
        return save(supervisorDTO);
    }

    /**
     * Deletes a supervisor entity from the repository based on the provided identifier.
     *
     * @param id the unique identifier of the supervisor to be deleted
     */
    @Override
    public void delete(Long id) {
        supervisorRepository.deleteById(id);
    }

    /**
     * Retrieves a SupervisorDTO by its unique identifier.
     *
     * @param id the unique identifier of the Supervisor to be retrieved
     * @return the SupervisorDTO corresponding to the given identifier,
     *         or null if no Supervisor is found with the specified id
     */
    @Override
    public SupervisorDTO findOne(Long id) {
        Optional<Supervisor> supervisor = supervisorRepository.findById(id);
        return supervisor.map(supervisorMapper::toDTO).orElse(null);
    }

    /**
     * Retrieves a supervisor by their email address.
     *
     * @param email the email address of the supervisor to find
     * @return a {@link SupervisorDTO} representing the supervisor if found, or null if no supervisor exists with the provided email
     */
    @Override
    public SupervisorDTO findByEmail(String email) {
        Optional<Supervisor> supervisor = supervisorRepository.findByEmail(email);
        return supervisor.map(supervisorMapper::toDTO).orElse(null);
    }

    /**
     * Retrieves all supervisors from the repository, converts them to their corresponding DTOs,
     * and returns them as a list.
     *
     * @return a list of SupervisorDTO objects representing all supervisors in the repository
     */
    @Override
    public List<SupervisorDTO> findAll() {
        List<Supervisor> supervisors = supervisorRepository.findAll();
        List<SupervisorDTO> supervisorDTOS = new ArrayList<>();
        for (Supervisor supervisor : supervisors) {
            supervisorDTOS.add(supervisorMapper.toDTO(supervisor));
        }
        return supervisorDTOS;
    }

    /**
     * Retrieves a paginated list of SupervisorDTO objects based on the provided pagination information.
     *
     * @param pageable the Pageable object containing pagination and sorting information,
     *                 including the page number, page size, and sorting details
     * @return a PaginationResponseDTO containing the paginated list of SupervisorDTO objects along
     *         with metadata such as the current page number, total pages, and total elements
     */
    @Override
    public PaginationResponseDTO<SupervisorDTO> findAllPaged(Pageable pageable) {
        final Page<Supervisor> page = supervisorRepository.findAll(pageable);

        return getSupervisorDTOPaginationResponseDTO(page);
    }

    /**
     * Retrieves a paginated list of SupervisorDTO objects based on the provided pagination and search parameters.
     *
     * @param pageable the Pageable object containing pagination information such as page number and page size
     * @param params the search parameters to filter the list of supervisors
     * @return a PaginationResponseDTO containing the paginated result of SupervisorDTO objects
     */
    @Override
    public PaginationResponseDTO<SupervisorDTO> findAllPagedByParams(Pageable pageable, String params) {
        final Page<Supervisor> page = supervisorRepository.searchSupervisors(pageable, params, params, null);

        return getSupervisorDTOPaginationResponseDTO(page);
    }

    /**
     * Retrieves a paginated list of SupervisorDTO objects based on the specified parameters and profile.
     *
     * @param pageable the pageable object containing pagination and sorting information
     * @param params a string containing search parameters to filter supervisors
     * @param profile the supervisor profile object to further filter the results
     * @return a PaginationResponseDTO containing the paginated list of SupervisorDTO objects
     *         and related pagination metadata
     */
    @Override
    public PaginationResponseDTO<SupervisorDTO> findAllPagedByParams(Pageable pageable, String params, SupervisorProfile profile) {
        final Page<Supervisor> page = supervisorRepository.searchSupervisors(pageable, params, params, profile);

        return getSupervisorDTOPaginationResponseDTO(page);
    }

    /**
     * Converts a paginated result of Supervisor entities into a PaginationResponseDTO containing
     * a paginated list of SupervisorDTOs.
     *
     * @param page the page object containing a list of Supervisor entities and pagination details
     * @return a PaginationResponseDTO containing the current page number, total number of pages,
     *         total elements, and a list of SupervisorDTOs
     */
    private PaginationResponseDTO<SupervisorDTO> getSupervisorDTOPaginationResponseDTO(Page<Supervisor> page) {
        final int currentPage = page.getNumber();
        final int totalPage = page.getTotalPages();
        final int totalElements = (int) page.getTotalElements();

        List<SupervisorDTO> supervisorDTOS = new ArrayList<>();
        for (Supervisor supervisor : page.getContent()) {
            supervisorDTOS.add(supervisorMapper.toDTO(supervisor));
        }

        return new PaginationResponseDTO<>(currentPage, totalPage, totalElements, supervisorDTOS);
    }

    /**
     * Partially updates a Supervisor entity with the data provided in the given SupervisorDTO.
     * The fields of the existing Supervisor entity are updated with the values in the
     * SupervisorDTO if they are not null. The updated entity is then saved in the repository.
     *
     * @param supervisorDTO the SupervisorDTO containing the fields to be updated
     * @return the updated SupervisorDTO after being persisted, or null if the entity could not be found
     * @throws IllegalArgumentException if the provided SupervisorDTO is null
     * @throws NullPointerException if no Supervisor entity exists with the given ID in the SupervisorDTO
     */
    @Override
    public SupervisorDTO partialUpdate(SupervisorDTO supervisorDTO) {
        if (Objects.isNull(supervisorDTO)) {throw new IllegalArgumentException("SupervisorDTO is null");}
        if (Boolean.FALSE.equals(existsById(supervisorDTO.getId()))) {throw new NullPointerException("SupervisorDTO not found");}
        Optional<Supervisor> supervisorOptional = supervisorRepository.findById(supervisorDTO.getId());
        if (supervisorOptional.isPresent()) {
            Supervisor supervisor = supervisorMapper.partialUpdate(supervisorOptional.get(), supervisorDTO);
            supervisor = supervisorRepository.save(supervisor);
            return supervisorMapper.toDTO(supervisor);
        }
        return null;
    }

    /**
     * Checks if a Supervisor entity exists with the given unique identifier.
     *
     * @param id the unique identifier of the Supervisor entity to check for existence
     * @return true if a Supervisor entity exists with the specified id, false otherwise
     */
    @Override
    public Boolean existsById(Long id) {
        return supervisorRepository.existsById(id);
    }
}
