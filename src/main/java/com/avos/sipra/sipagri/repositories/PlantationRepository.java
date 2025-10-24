package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.Plantation;
import com.avos.sipra.sipagri.enums.PlantationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlantationRepository extends JpaRepository<Plantation, Long> {
    Optional<Plantation> findByProductions_id(Long productionsId);

    Page<Plantation> findPlantationsByNameContainingIgnoreCase(Pageable pageable, String name);

    Page<Plantation> findPlantationsByPlanter_Supervisor_Id(Pageable pageable, Long supervisorId);

    Page<Plantation> findPlantationsByGpsLocation_displayNameContainingIgnoreCase(Pageable pageable, String village);

    /**
     * Somme de la surface totale cultivée
     */
    @Query("SELECT COALESCE(SUM(p.farmedArea), 0.0) FROM Plantation p")
    Double sumTotalFarmedArea();

    /**
     * ✨ NOUVEAU : Compte les plantations par statut
     */
    long countByStatus(PlantationStatus status);

    /**
     * ✨ NOUVEAU : Somme de TOUS les kits (peu importe le statut)
     */
    @Query("SELECT COALESCE(SUM(p.kit.totalCost), 0.0) FROM Plantation p WHERE p.kit IS NOT NULL")
    Double sumAllKitsValue();

    /**
     * Somme des kits des plantations avec un statut spécifique
     */
    @Query("SELECT COALESCE(SUM(p.kit.totalCost), 0.0) FROM Plantation p WHERE p.status = :status AND p.kit IS NOT NULL")
    Double sumKitsValueForActivePlantations(@Param("status") PlantationStatus status);

    /**
     * Top plantations par surface cultivée
     */
    @Query("SELECT p FROM Plantation p ORDER BY p.farmedArea DESC")
    List<Plantation> findTopByOrderByFarmedAreaDesc(@Param("limit") int limit);

}