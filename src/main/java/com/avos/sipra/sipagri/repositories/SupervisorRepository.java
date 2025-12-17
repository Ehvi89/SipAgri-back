package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.Supervisor;
import com.avos.sipra.sipagri.enums.SupervisorProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * Repository interface for performing CRUD operations and custom queries on
 * {@link Supervisor} entities.
 * <p>
 * This interface extends {@link JpaRepository}, providing methods for standard
 * data access operations, such as saving, deleting, and retrieving entities,
 * as well as custom queries specific to the {@link Supervisor} entity.
 */
@Repository
public interface SupervisorRepository extends JpaRepository<Supervisor, Long> {

    /**
     * Retrieves an optional {@link Supervisor} entity based on the specified email address.
     *
     * @param email the email address to search for
     * @return an {@link Optional} containing the {@link Supervisor} entity if found, or an empty {@link Optional} if not found
     */
    Optional<Supervisor> findByEmail(String email);

    /**
     * Searches for supervisors based on optional filtering criteria such as firstname, lastname, and profile.
     * The search is case-insensitive for firstname and lastname, and supports partial matching.
     * If a parameter is null, it is ignored in the filtering.
     *
     * @param pageable the pagination information including page number, size, and sorting options
     * @param firstname the partial or full firstname of the supervisor; can be null to ignore this filter
     * @param lastname the partial or full lastname of the supervisor; can be null to ignore this filter
     * @param profile the specific {@link SupervisorProfile} to filter by; can be null to ignore this filter
     * @return a paginated list of supervisors matching the provided criteria
     */
    @Query("""
            SELECT s FROM Supervisor s
            WHERE ((:firstname IS NULL OR LOWER(s.firstname) LIKE LOWER(CONCAT('%', :firstname, '%')))
            OR (:lastname IS NULL OR LOWER(s.lastname) LIKE LOWER(CONCAT('%', :lastname, '%'))))
            AND (:profile IS NULL OR s.profile = :profile)
            """)
    Page<Supervisor> searchSupervisors(Pageable pageable, String firstname, String lastname, SupervisorProfile profile);
}
