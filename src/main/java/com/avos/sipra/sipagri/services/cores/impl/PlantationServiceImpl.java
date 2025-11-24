package com.avos.sipra.sipagri.services.cores.impl;

import com.avos.sipra.sipagri.entities.Plantation;
import com.avos.sipra.sipagri.enums.PlantationStatus;
import com.avos.sipra.sipagri.repositories.PlantationRepository;
import com.avos.sipra.sipagri.repositories.PlanterRepository;
import com.avos.sipra.sipagri.services.cores.CalculationService;
import com.avos.sipra.sipagri.services.cores.PlantationService;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.PlantationDTO;
import com.avos.sipra.sipagri.services.dtos.ProductionDTO;
import com.avos.sipra.sipagri.services.mappers.PlantationMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PlantationServiceImpl implements PlantationService {
    /**
     * An instance of PlantationMapper used to map plantation data between
     * different layers of the application. This is typically responsible
     * for translating data objects to ensure compatibility and proper operations
     * between services, databases, or other components.
     */
    private final PlantationMapper plantationMapper;
    /**
     * A repository interface for managing Plantation entities within the persistence layer.
     * Used in the PlantationServiceImpl class to perform CRUD operations and query-related tasks
     * for Plantation data.
     */
    private final PlantationRepository plantationRepository;
    /**
     * Service for handling calculation logic specific to plantation operations.
     * This service provides methods for executing complex calculations
     * related to plantation data and operations. It is a dependency for
     * the PlantationServiceImpl class and facilitates finance or production
     * calculations as required during the processing of plantation-related data.
     */
    private final CalculationService calculationService;
    /**
     * Repository interface for managing Planter entities in the database.
     * Provides methods to perform CRUD operations and manage data persistence
     * for planter-related functionalities.
     *
     * This repository is injected into the PlantationServiceImpl to
     * facilitate interactions with the data layer for any operations
     * related to Planter entities.
     */
    private final PlanterRepository planterRepository;

    /**
     * Constructs a new instance of PlantationServiceImpl with the provided dependencies.
     *
     * @param plantationMapper the mapper used for converting between Plantation entities and PlantationDTOs
     * @param plantationRepository the repository for performing CRUD operations on Plantation entities
     * @param calculationService the service responsible for various calculations related to plantations
     * @param planterRepository the repository for managing Planter-related data and operations
     */
    public PlantationServiceImpl(PlantationMapper plantationMapper,
                                 PlantationRepository plantationRepository,
                                 CalculationService calculationService,
                                 PlanterRepository planterRepository) {
        this.plantationMapper = plantationMapper;
        this.plantationRepository = plantationRepository;
        this.calculationService = calculationService;
        this.planterRepository = planterRepository;
    }

    /**
     * Saves a new plantation or updates an existing one in the repository.
     * If the creation date is not set in the provided DTO, it will be initialized to the current date and time.
     * Additionally, production values are calculated for each associated production if present.
     *
     * @param plantationDTO the {@code PlantationDTO} object containing the plantation data to be saved.
     *                       It may also include associated production data to process.
     * @return the saved {@code PlantationDTO} object, mapped from the persisted entity.
     */
    @Override
    public PlantationDTO save(PlantationDTO plantationDTO) {
        Plantation plantation = plantationMapper.toEntity(plantationDTO);
        if (plantationDTO.getCreatedAt() == null) {
            plantation.setCreatedAt(LocalDateTime.now());
        }

        // Calculer les valeurs des productions
        if (plantationDTO.getProductions() != null) {
            for (ProductionDTO productionDTO : plantationDTO.getProductions()) {
                calculationService.calculateProductionValues(productionDTO);
                // mustBePaid sera calculé après sauvegarde
            }
        }

        plantation = plantationRepository.save(plantation);
        return plantationMapper.toDTO(plantation);
    }

    /**
     * Updates an existing plantation record with the data provided in the given {@code PlantationDTO}.
     * Ensures that the plantation exists and assigns a new updated timestamp before saving the changes.
     *
     * @param plantationDTO the {@code PlantationDTO} object containing updated plantation data.
     *                       Must include the plantation ID and the new values to be persisted.
     * @return the updated {@code PlantationDTO} object with the latest changes.
     * @throws IllegalArgumentException if the plantation ID is null or the plantation does not exist.
     */
    @Override
    public PlantationDTO update(PlantationDTO plantationDTO) {
        if (Objects.isNull(plantationDTO.getId())) {throw new IllegalArgumentException("Id cannot be null");}
        if (Boolean.FALSE.equals(existsById(plantationDTO.getId()))) {throw new IllegalArgumentException("Plantation does not exist");}
        plantationDTO.setUpdatedAt(LocalDateTime.now());
        return save(plantationDTO);
    }

    /**
     * Deletes a plantation entity by its unique identifier.
     *
     * @param id the unique identifier of the plantation entity to be deleted
     */
    @Override
    public void delete(Long id) {
        plantationRepository.deleteById(id);
    }

    /**
     * Retrieves a plantation by its unique identifier, converts it to a DTO, and returns the DTO.
     * If the plantation does not exist, a {@code NullPointerException} is thrown.
     *
     * @param id the unique identifier of the plantation to retrieve
     * @return a {@code PlantationDTO} object representing the plantation with the specified ID
     * @throws NullPointerException if the plantation does not exist
     */
    @Override
    public PlantationDTO findOne(Long id) {
        Optional<Plantation> plantation = plantationRepository.findById(id);
        if (plantation.isPresent()) {
            return plantationMapper.toDTO(plantation.get());
        }
        throw new NullPointerException("Plantation does not exist");
    }

    /**
     * Retrieves a PlantationDTO object based on the ID of a related production.
     *
     * @param id the unique identifier of the production whose associated plantation is to be retrieved
     * @return a PlantationDTO object representing the plantation linked to the given production ID
     * @throws NullPointerException if no plantation is found associated with the given production ID
     */
    @Override
    public PlantationDTO findByProductionsId(Long id) {
        Optional<Plantation> plantation = plantationRepository.findByProductions_id(id);
        if (plantation.isPresent()) {
            return plantationMapper.toDTO(plantation.get());
        }
        else throw new NullPointerException("Plantation does not exist");
    }

    /**
     * Retrieves all plantations from the repository, maps them to DTOs, and returns the list of DTOs.
     *
     * @return a list of PlantationDTO objects representing all plantations.
     */
    @Override
    public List<PlantationDTO> findAll() {
        List<Plantation> plantations = plantationRepository.findAll();
        List<PlantationDTO> plantationDTOS = new ArrayList<>();
        for (Plantation plantation : plantations) {
            plantationDTOS.add(plantationMapper.toDTO(plantation));
        }
        return plantationDTOS;
    }

    /**
     * Retrieves a list of PlantationDTO objects associated with a specific supervisor.
     *
     * @param supervisorId the ID of the supervisor whose plantations are to be retrieved
     * @return a list of PlantationDTO objects corresponding to the plantations managed by the specified supervisor
     */
    @Override
    public List<PlantationDTO> findAll(Long supervisorId) {
        List<Plantation> plantations = plantationRepository.findPlantationByPlanter_Supervisor_Id(supervisorId);
        List<PlantationDTO> plantationDTOS = new ArrayList<>();
        for (Plantation plantation : plantations) {
            plantationDTOS.add(plantationMapper.toDTO(plantation));
        }
        return plantationDTOS;
    }

    /**
     * Retrieves a paginated list of plantations from the repository, maps them to DTOs,
     * and returns the result wrapped in a PaginationResponseDTO object.
     *
     * @param pageable the pagination information, including page number, size, and sorting
     * @return a PaginationResponseDTO<PlantationDTO> containing the current page number,
     *         total pages, total elements, and the list of plantations
     */
    @Override
    public PaginationResponseDTO<PlantationDTO> findAllPaged(Pageable pageable) {
        Page<Plantation> page =  plantationRepository.findAll(pageable);

        return getPlantationDTOPaginationResponseDTO(page);
    }

    /**
     * Retrieves a paginated list of plantation data matching the provided search parameters.
     *
     * @param pageable the pagination information, including page number, size, and sorting
     * @param params the search parameter used to filter plantations by name
     * @return a {@code PaginationResponseDTO<PlantationDTO>} containing the current page number, total pages,
     *         total elements, and the list of plantations matching the search criteria
     */
    @Override
    public PaginationResponseDTO<PlantationDTO> findAllPagedByParams(Pageable pageable, String params) {
        Page<Plantation> page =  plantationRepository.findPlantationsByNameContainingIgnoreCase(pageable, params);

        return getPlantationDTOPaginationResponseDTO(page);
    }

    /**
     * Retrieves a paginated list of plantations filtered by the specified parameters.
     *
     * @param pageable the pagination and sorting information
     * @param params the filter parameter for plantation names
     * @param supersiorId the ID of the supervisor to filter plantations by
     * @return a paginated response containing a list of PlantationDTO objects
     */
    @Override
    public PaginationResponseDTO<PlantationDTO> findAllPagedByParams(Pageable pageable, String params, Long supersiorId) {
        Page<Plantation> page =  plantationRepository.findPlantationsByNameContainingIgnoreCaseAndPlanter_Supervisor_Id(pageable, params, supersiorId);

        return getPlantationDTOPaginationResponseDTO(page);
    }

    /**
     * Retrieves a paginated list of plantations filtered by the given village name.
     *
     * @param pageable the pagination information, including page number, size, and sorting
     * @param village the village name used to filter plantations
     * @return a {@code PaginationResponseDTO<PlantationDTO>} containing the current page number, total pages,
     *         total elements, and the list of plantations located in the specified village
     */
    @Override
    public PaginationResponseDTO<PlantationDTO> findAllPagedByVillage(Pageable pageable, String village) {
        Page<Plantation> page =  plantationRepository.findPlantationsByGpsLocation_displayNameContainingIgnoreCase(pageable, village);

        return getPlantationDTOPaginationResponseDTO(page);
    }

    /**
     * Retrieves a paginated list of plantations filtered by village name and supervisor ID.
     *
     * @param pageable the pagination information including page number and size
     * @param village the name of the village to filter plantations by
     * @param supervisorId the ID of the supervisor to filter plantations by
     * @return a {@link PaginationResponseDTO} containing a paginated list of {@link PlantationDTO}
     */
    @Override
    public PaginationResponseDTO<PlantationDTO> findAllPagedByVillage(Pageable pageable, String village, Long supervisorId) {
        Page<Plantation> page =  plantationRepository.findPlantationsByGpsLocation_displayNameContainingIgnoreCaseAndPlanter_Supervisor_Id(pageable, village, supervisorId);

        return getPlantationDTOPaginationResponseDTO(page);
    }

    /**
     * Retrieves a paginated list of plantation data filtered by the specified plantation status.
     *
     * @param pageable the pagination information, including page number, size, and sorting
     * @param status the specific status of plantations to filter the results by
     * @return a {@code PaginationResponseDTO<PlantationDTO>} containing the current page number, total pages,
     *         total elements, and the list of plantations matching the specified status
     */
    @Override
    public PaginationResponseDTO<PlantationDTO> findAllPagedByStatus(Pageable pageable, PlantationStatus status) {
        Page<Plantation> page =  plantationRepository.findPlantationsByStatus(pageable, status);

        return getPlantationDTOPaginationResponseDTO(page);
    }

    /**
     * Finds a paginated list of plantations filtered by their status and supervisor ID.
     *
     * @param pageable the pagination and sorting information
     * @param status the status of the plantations to filter by
     * @param supervisorId the ID of the supervisor to filter plantations by
     * @return a paginated response DTO containing the filtered plantations
     */
    @Override
    public PaginationResponseDTO<PlantationDTO> findAllPagedByStatus(Pageable pageable, PlantationStatus status, Long supervisorId) {
        Page<Plantation> page =  plantationRepository.findPlantationsByStatusAndPlanter_Supervisor_Id(pageable, status, supervisorId);

        return getPlantationDTOPaginationResponseDTO(page);
    }

    /**
     * Retrieves a paginated list of plantations managed by a specific planter supervisor.
     *
     * @param pageable the pagination information, including page number, size, and sorting.
     * @param supervisorId the unique identifier of the supervisor managing the plantations.
     * @return a {@code PaginationResponseDTO<PlantationDTO>} containing the current page number,
     *         total pages, total elements, and the list of plantations managed by the given supervisor.
     */
    @Override
    public PaginationResponseDTO<PlantationDTO> findAllPagedPlantationByPlanterSupervisor(Pageable pageable, Long supervisorId) {
        Page<Plantation> page =  plantationRepository.findPlantationsByPlanter_Supervisor_Id(pageable, supervisorId);

        return getPlantationDTOPaginationResponseDTO(page);
    }

    /**
     * Constructs a {@code PaginationResponseDTO<PlantationDTO>} object from a given {@code Page<Plantation>}.
     * It converts the content of the page into a list of {@code PlantationDTO} using the plantationMapper
     * and includes pagination details such as the current page, total pages, and the total number of elements.
     *
     * @param page the {@code Page<Plantation>} object containing the paginated plantations and their metadata
     * @return a {@code PaginationResponseDTO<PlantationDTO>} containing the current page, total pages,
     *         total elements, and the list of {@code PlantationDTO}
     */
    private PaginationResponseDTO<PlantationDTO> getPlantationDTOPaginationResponseDTO(Page<Plantation> page) {
        final int currentPage = page.getNumber();
        final int totalPages = page.getTotalPages();
        final int totalElements = (int) page.getTotalElements();

        List<PlantationDTO> plantationDTOS = new ArrayList<>();
        for (Plantation plantation : page.getContent()) {
            plantationDTOS.add(plantationMapper.toDTO(plantation));
        }

        return new PaginationResponseDTO<>(currentPage, totalPages, totalElements, plantationDTOS);
    }

    /**
     * Partially updates the provided plantation data. It performs checks to ensure the plantation exists
     * before updating. If the provided plantation ID is null or the plantation does not exist, it throws
     * an exception. Feeds the updated data into the repository and returns the updated plantation DTO.
     *
     * @param plantationDTO the {@code PlantationDTO} object containing the plantation's partial data
     *                       that needs to be updated
     * @return the updated {@code PlantationDTO} object with the latest changes
     * @throws IllegalArgumentException if the plantation ID is null or the plantation does not exist
     * @throws NullPointerException if the plantation does not exist in the repository
     */
    @Override
    public PlantationDTO partialUpdate(PlantationDTO plantationDTO) {
        if (Objects.isNull(plantationDTO.getId())) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (Boolean.FALSE.equals(existsById(plantationDTO.getId()))) {
            throw new IllegalArgumentException("Plantation does not exist");
        }

        Optional<Plantation> plantationOptional = plantationRepository.findById(plantationDTO.getId());
        if (plantationOptional.isPresent()) {
            // Calculer les valeurs des nouvelles productions
            if (plantationDTO.getProductions() != null) {
                List<ProductionDTO> productionDTOS = new ArrayList<>();
                for (ProductionDTO production : plantationDTO.getProductions()) {
                    productionDTOS.add(calculationService.calculateProductionValues(production));
                }

                plantationDTO.setProductions(productionDTOS);
            }

            Plantation plantation = plantationMapper.partialUpdate(plantationOptional.get(), plantationDTO);
            plantation = plantationRepository.save(plantation);
            return plantationMapper.toDTO(plantation);
        }
        throw new NullPointerException("Plantation does not exist");
    }

    /**
     * Checks whether a plantation entity exists in the repository by its unique identifier.
     *
     * @param id the unique identifier of the plantation entity to check for existence
     * @return {@code true} if a plantation entity with the specified identifier exists; {@code false} otherwise
     */
    @Override
    public Boolean existsById(Long id) {
        return plantationRepository.existsById(id);
    }
}
