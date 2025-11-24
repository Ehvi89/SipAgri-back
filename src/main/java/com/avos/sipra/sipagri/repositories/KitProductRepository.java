package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.KitProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations and customized queries on
 * {@link KitProduct} entities.
 * <p>
 * This interface extends {@link JpaRepository}, providing basic methods for accessing
 * and managing data, such as save, delete, and find operations. It also defines
 * a custom query method for paginated retrieval of {@link KitProduct} entities
 * based on the name of the associated product or its quantity.
 */
@Repository
public interface KitProductRepository extends JpaRepository<KitProduct, Long> {
    /**
     * Retrieves a paginated list of KitProduct entities based on the name of the associated product
     * or its quantity. The search is case-sensitive.
     *
     * @param pageable the pagination information, including page number, size, and sorting options
     * @param productName the name of the associated product to search for
     * @param quantity the quantity to search for
     * @return a paginated list of KitProduct entities that match the specified product name or quantity
     */
    Page<KitProduct> findKitProductByProduct_NameOrQuantity(Pageable pageable, String productName, String quantity);
}
