package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.Kit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations and custom queries
 * on {@link Kit} entities.
 * <p>
 * This interface extends {@link JpaRepository}, providing basic methods
 * for data access such as saving, deleting, and finding entities. It also
 * includes a custom query method to support paginated searches based on
 * the name of the kits, ignoring case sensitivity.
 */
@Repository
public interface KitRepository extends JpaRepository<Kit, Long> {
    /**
     * Retrieves a paginated list of kits whose names contain the specified string, ignoring case sensitivity.
     *
     * @param pageable the pagination information including page number, size, and sorting options
     * @param name the substring to search for in kit names
     * @return a paginated list of kits that match the search criteria
     */
    Page<Kit> findKitByNameContainingIgnoreCase(Pageable pageable, String name);
}
