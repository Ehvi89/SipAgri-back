package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.Params;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParamsRepository extends JpaRepository<Params, Long> {
    List<Params> findByCodeParams(String code);

    Optional<Params> findByName(String name);
}
