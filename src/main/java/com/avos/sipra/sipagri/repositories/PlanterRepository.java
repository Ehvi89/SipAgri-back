package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.Planter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for managing {@link Planter} entities with CRUD operations
 * and specialized query methods.
 * <p>
 * This interface extends {@link JpaRepository}, enabling standard DB operations
 * as well as custom query methods for specific planter-related data retrieval.
 */
@Repository
public interface PlanterRepository extends JpaRepository<Planter, Long> {
    /**
     * Retrieves a paginated list of planters whose first name or last name contains a specified string,
     * ignoring case sensitivity.
     *
     * @param pageable the pagination information including page number, size, and sort options
     * @param firstname the substring to search for in the first name of planters
     * @param lastname the substring to search for in the last name of planters
     * @return a paginated list of planters that match the search criteria
     */
    Page<Planter> findPlanterByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(
            Pageable pageable, String firstname, String lastname);

    /**
     * Retrieves a paginated list of planters managed by a specific supervisor.
     *
     * @param supervisorId the ID of the supervisor whose planters are to be retrieved
     * @param pageable the pagination information
     * @return a paginated list of planters associated with the specified supervisor
     */
    Page<Planter> findPlanterBySupervisor_Id(Long supervisorId, Pageable pageable);

    /**
     * Retrieves a list of planters managed by a specific supervisor.
     *
     * @param supervisorId the ID of the supervisor whose planters are to be retrieved
     * @return a list of planters associated with the specified supervisor
     */
    List<Planter> findPlanterBySupervisor_Id(Long supervisorId);

    /**
     * Finds a page of planters whose village names contain the specified string, ignoring case.
     *
     * @param pageable the pagination information
     * @param village  the string to search for within village names
     * @return a page of planters that match the specified village search criteria
     */
    Page<Planter> findPlanterByVillageContainingIgnoreCase(Pageable pageable, String village);

    /**
     * Counts the number of planters created on or after the specified date.
     *
     * @param date the date from which to count the planters
     * @return the number of planters created on or after the specified date
     */
    @Query("SELECT COUNT(p) FROM Planter p WHERE p.createdAt >= :date")
    long countPlantersCreatedAfter(@Param("date") LocalDateTime date);

    /**
     * Calculates the average age of all planters by determining the average of the difference
     * between the current year and the birth year of each planter.
     *
     * @return the average age of all planters as a Double
     */
    @Query("SELECT AVG(YEAR(CURRENT_DATE) - YEAR(p.birthday)) FROM Planter p")
    Double calculateAverageAge();

    /**
     * Counts the number of planters associated with a specific supervisor.
     *
     * @param supervisorId the ID of the supervisor whose associated planters are to be counted
     * @return the total number of planters managed by the specified supervisor
     */
    Long countPlantersBySupervisor_Id(@Param("supervisorId")Long supervisorId);

    /**
     * Counts the number of entities associated with a specific supervisor and created on or after
     * the specified date.
     *
     * @param supervisorId the ID of the supervisor whose associated entities are to be counted
     * @param date the date from which to include entities in the count
     * @return the total number of entities associated with the specified supervisor and created
     *         on or after the given date
     */
    Long countBySupervisor_IdAndCreatedAtGreaterThanEqual(Long supervisorId, LocalDateTime date);
}