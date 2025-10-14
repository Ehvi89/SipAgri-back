package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.Plantation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlantationRepository extends JpaRepository<Plantation, Long> {
    Optional<Plantation> findByProductions_id(Long productionsId);

    Page<Plantation> findPlantationsByNameContainingIgnoreCase(Pageable pageable, String name);

    Page<Plantation> findPlantationsByPlanter_Supervisor_Id(Pageable pageable, Long supervisorId);

    /**
     * Somme de la surface totale cultivée
     */
    @Query("SELECT SUM(p.farmedArea) FROM Plantation p")
    Double sumTotalFarmedArea();

    /**
     * Top plantations par surface cultivée
     */
    @Query("SELECT p FROM Plantation p ORDER BY p.farmedArea DESC")
    List<Plantation> findTopByOrderByFarmedAreaDesc(@Param("limit") int limit);

    @Query("SELECT COUNT(p) FROM Plantation p WHERE p.createdAt < :date")
    long countPlantationsCreatedBeforeMonth(@Param("date") LocalDateTime date);

    /**
     * Plantations d'un planteur
     */
    List<Plantation> findByPlanterId(Long planterId);

    /**
     * Plantations par nom (recherche)
     */
    @Query("SELECT p FROM Plantation p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Plantation> searchByName(@Param("name") String name);

    /**
     * Plantations avec surface cultivée supérieure à une valeur
     */
    List<Plantation> findByFarmedAreaGreaterThan(Double area);

    /**
     * Compte les plantations par planteur
     */
    long countByPlanterId(Long planterId);
}
