package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.Plantation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlantationRepository extends JpaRepository<Plantation, Long> {
    Optional<Plantation> findByProductions_id(Long productionsId);

    Page<Plantation> findPlantationsByNameContainingIgnoreCase(Pageable pageable, String name);
}
