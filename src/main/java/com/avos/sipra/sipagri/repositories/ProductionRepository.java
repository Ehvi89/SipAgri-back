package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.Production;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionRepository extends JpaRepository<Production, Long> {
    @Query("SELECT p FROM Production p WHERE " +
            "(:plantationName IS NULL OR p.plantation.name LIKE %:plantationName%) OR " +
            "(:productionInKg IS NULL OR CAST(p.productionInKg AS string) LIKE %:productionInKg%)")
    Page<Production> findProductionsByPlantation_NameOrProductionInKg(Pageable pageable, String plantationName, String productionInKg);
}
