package com.avos.sipra.sipagri.services.cores;

import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CrudService<T, I> {

    T save(T t);

    T update(T t);

    void delete(I id);

    T findOne(I id);

    List<T> findAll();

    T partialUpdate(T t);

    Boolean existsById(I id);

    PaginationResponseDTO<T> findAllPaged(Pageable pageable);
}
