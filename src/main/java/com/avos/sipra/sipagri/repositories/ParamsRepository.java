package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.Params;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for performing CRUD operations and custom queries on
 * {@link Params} entities.
 * <p>
 * This interface extends {@link JpaRepository}, providing default methods for
 * standard data access such as save, delete, and fetch operations. Additionally,
 * it includes custom query methods for retrieving {@link Params} entities based
 * on specific attributes.
 */
@Repository
public interface ParamsRepository extends JpaRepository<Params, Long> {
    /**
     * Retrieves a list of {@link Params} entities that have the specified code.
     *
     * @param code the unique code to filter the {@link Params} entities
     * @return a list of {@link Params} entities matching the specified code
     */
    List<Params> findByCodeParams(String code);

    /**
     * Retrieves an optional {@link Params} entity based on its name.
     *
     * @param name the name of the {@link Params} entity to search for
     * @return an {@link Optional} containing the {@link Params} entity if found, or an empty {@link Optional} if not found
     */
    Optional<Params> findByName(String name);

    /**
     * Retrieves a paginated list of {@link Params} entities that match the specified code or name criteria.
     * If both code and name are provided, entities matching either of the parameters will be retrieved.
     *
     * @param pageable the pagination information including page number, size, and sorting options
     * @param code the code to search for in the Params entities
     * @param name the name to search for in the Params entities
     * @return a paginated list of Params entities matching the specified code or name criteria
     */
    Page<Params> findParamsByCodeParamsOrName(Pageable pageable, String code, String name);
}
