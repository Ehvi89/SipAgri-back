package com.avos.sipra.sipagri.services.cores;

import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * A generic interface defining CRUD (Create, Read, Update, Delete) operations for entities of type T
 * and identifiers of type I. It also provides methods for partial updates, existence checks, and
 * pagination support.
 *
 * @param <T> the type of the entity
 * @param <I> the type of the entity's identifier
 */
public interface CrudService<T, I> {

    /**
     * Saves the given entity.
     *
     * @param t the entity to save
     * @return the saved entity
     */
    T save(T t);

    /**
     * Updates an existing entity with the provided details.
     *
     * @param t the entity containing updated information
     * @return the updated entity
     */
    T update(T t);

    /**
     * Deletes an entity identified by the given ID.
     *
     * @param id the identifier of the entity to delete
     */
    void delete(I id);

    /**
     * Retrieves an entity of type T by its identifier.
     *
     * @param id the identifier of the entity to retrieve
     * @return the entity associated with the given identifier, or null if not found
     */
    T findOne(I id);

    /**
     * Retrieves all entities of type T.
     *
     * @return a list of all entities of type T
     */
    List<T> findAll();

    /**
     * Partially updates the given entity. Only non-null fields in the provided entity
     * will be updated in the underlying data store.
     *
     * @param t the entity containing the values to be updated
     * @return the updated entity after performing the partial update
     */
    T partialUpdate(T t);

    /**
     * Checks whether an entity with the given ID exists in the underlying data store.
     *
     * @param id the ID of the entity to check for existence
     * @return true if the entity exists, false otherwise
     */
    Boolean existsById(I id);

    /**
     * Retrieves a paginated list of all records.
     *
     * @param pageable an object containing pagination details such as page number, size, and sort order
     * @return a {@code PaginationResponseDTO<T>} containing the paginated data, including current page,
     *         total pages, total elements, and the list of data items
     */
    PaginationResponseDTO<T> findAllPaged(Pageable pageable);

    /**
     * Fetches a paginated list of entities based on the specified parameters.
     *
     * @param pageable the pagination and sorting information
     * @param params the filtering criteria or parameters for querying entities
     * @return a PaginationResponseDTO containing the list of entities, pagination details,
     *         and total records matching the filtering criteria
     */
    PaginationResponseDTO<T> findAllPagedByParams(Pageable pageable, String params);
}
