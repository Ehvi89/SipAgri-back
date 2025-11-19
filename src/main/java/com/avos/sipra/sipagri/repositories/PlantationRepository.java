package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.Plantation;
import com.avos.sipra.sipagri.enums.PlantationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlantationRepository extends JpaRepository<Plantation, Long> {
    /**
     * Finds a plantation entity based on the ID of its associated productions.
     *
     * @param productionsId the ID of the productions associated with the plantation to be retrieved
     * @return an Optional containing the found Plantation entity, or an empty Optional if no plantation is found
     */
    Optional<Plantation> findByProductions_id(Long productionsId);

    /**
     * Retrieves a paginated list of plantations where the name contains the given string,
     * ignoring case sensitivity.
     *
     * @param pageable the pagination information, including page number, size, and sorting options
     * @param name the substring to search for within plantation names; the search is case-insensitive
     * @return a paginated list of plantations matching the specified criteria
     */
    Page<Plantation> findPlantationsByNameContainingIgnoreCase(Pageable pageable, String name);

    Page<Plantation> findPlantationsByNameContainingIgnoreCaseAndPlanter_Supervisor_Id(Pageable pageable, String name, Long supervisorId);

    /**
     * Retrieves a paginated list of plantations managed by a planter whose supervisor matches
     * the given supervisor ID.
     *
     * @param pageable the pagination information, including page number, size, and sorting options
     * @param supervisorId the ID of the supervisor to filter plantations by
     * @return a paginated list of plantations associated with the specified supervisor
     */
    Page<Plantation> findPlantationsByPlanter_Supervisor_Id(Pageable pageable, Long supervisorId);

    /**
     * Retrieves a paginated list of plantations where the GPS location's display name contains
     * the given village (case-insensitive).
     *
     * @param pageable the pagination information, including page number, size, and sorting options
     * @param village the village name to filter plantations by; the search is case-insensitive
     * @return a paginated list of plantations matching the specified criteria
     */
    Page<Plantation> findPlantationsByGpsLocation_displayNameContainingIgnoreCase(Pageable pageable, String village);

    Page<Plantation> findPlantationsByGpsLocation_displayNameContainingIgnoreCaseAndPlanter_Supervisor_Id(Pageable pageable, String village, Long supervisorId);

    Page<Plantation> findPlantationsByStatus(Pageable pageable, PlantationStatus status);

    Page<Plantation> findPlantationsByStatusAndPlanter_Supervisor_Id(Pageable pageable, PlantationStatus status, Long supervisorId);

    /**
     * Counts the total number of plantations with the specified status.
     *
     * @param status the status of the plantations to be counted
     * @return the total number of plantations matching the given status
     */
    long countByStatus(PlantationStatus status);

    /**
     * Computes the total cost of all kits associated with plantations.
     * If no kits are associated, returns 0.0.
     *
     * @return the sum of total costs of all kits as a Double, or 0.0 if no kits are associated
     */
    @Query("SELECT COALESCE(SUM(p.kit.totalCost), 0.0) FROM Plantation p WHERE p.kit IS NOT NULL")
    Double sumAllKitsValue();

    /**
     * Computes the total sum of the kit costs for all plantations with a specified status
     * where the kits are not null. If no matching plantations or kits are found, returns 0.0.
     *
     * @param status the status of the plantations to filter (e.g., active)
     * @return the total sum of the kit costs for plantations with the given status as a Double,
     *         or 0.0 if no such kits are found
     */
    @Query("SELECT COALESCE(SUM(p.kit.totalCost), 0.0) FROM Plantation p WHERE p.status = :status AND p.kit IS NOT NULL")
    Double sumKitsValueForActivePlantations(@Param("status") PlantationStatus status);

    /**
     * Counts the number of plantations associated with a specific supervisor.
     *
     * @param supervisorId the ID of the supervisor whose associated plantations are to be counted
     * @return the total number of plantations managed by the specified supervisor
     */
    @Query("SELECT COUNT(p) FROM Plantation p WHERE p.planter.supervisor.id = :supervisorId")
    Long countPlantationBySupervisor(@Param("supervisorId") Long supervisorId);

    /**
     * Counts the number of plantations associated with a specific supervisor and having a specific status.
     *
     * @param status the status of the plantations to be counted
     * @param supervisorId the ID of the supervisor whose associated plantations are to be counted
     * @return the total number of plantations managed by the specified supervisor
     *         and having the specified status
     */
    @Query("""
            SELECT COUNT(p) FROM Plantation p
            WHERE p.status = :status
            AND p.planter.supervisor.id = :supervisorId
            """)
    Long countPlantationBySupervisorByStatus(@Param("status") PlantationStatus status,
                                             @Param("supervisorId") Long supervisorId);

    /**
     * Computes the total cost of all kits associated with plantations managed by a specific supervisor.
     * If no such kits are associated, returns 0.0.
     *
     * @param supervisorId the ID of the supervisor whose plantations' kit costs are to be computed
     * @return the total cost of all kits associated with plantations managed by the specified supervisor,
     *         or 0.0 if no such kits are found
     */
    @Query("""
            SELECT COALESCE(SUM(p.kit.totalCost), 0.0)
            FROM Plantation p
            WHERE p.kit IS NOT NULL
            AND p.planter.supervisor.id = :supervisorId
            """)
    Double sumAllKitsValueBySupervisor(@Param("supervisorId") Long supervisorId);

    /**
     * Computes the total cost of all kits associated with active plantations managed by a specific supervisor.
     * If no matching kits are associated with active plantations, returns 0.0.
     *
     * @param status the status of the plantations to filter (e.g., active)
     * @param supervisorId the ID of the supervisor whose plantations' kit costs are to be computed
     * @return the total cost of all kits associated with the active plantations managed by the specified supervisor,
     *         or 0.0 if no such kits are found
     */
    @Query("""
            SELECT COALESCE(SUM(p.kit.totalCost), 0.0)
            FROM Plantation p
            WHERE p.status = :status
            AND p.kit IS NOT NULL
            AND p.planter.supervisor.id = :supervisorId
            """)
    Double sumKitsValueForActivePlantationsBySupervisor(@Param("status") PlantationStatus status,
                                            @Param("supervisorId") Long supervisorId);
}