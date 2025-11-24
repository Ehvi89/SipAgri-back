package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations and custom queries on
 * {@link Product} entities.
 * <p>
 * This interface extends {@link JpaRepository}, providing standard methods for data
 * management such as saving, deleting, and finding {@link Product} entities. It also
 * includes a custom query method to support paginated searches by product names, ignoring
 * case sensitivity.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    /**
     * Retrieves a paginated list of products whose names contain the specified string, ignoring case sensitivity.
     *
     * @param pageable the pagination information including page number, size, and sorting options
     * @param name the substring to search for in product names
     * @return a paginated list of products that match the search criteria
     */
    Page<Product> findProductsByNameContainingIgnoreCase(Pageable pageable, String name);
}
