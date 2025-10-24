package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.Planter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlanterRepository extends JpaRepository<Planter, Long> {
    Page<Planter> findPlanterByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(
            Pageable pageable, String firstname, String lastname);

    Page<Planter> findPlanterBySupervisor_Id(Long supervisorId, Pageable pageable);

    Page<Planter> findPlanterByVillageContainingIgnoreCase(Pageable pageable, String village);

    /**
     * ✨ NOUVEAU : Compte les planteurs créés après une date (pour l'année en cours)
     */
    @Query("SELECT COUNT(p) FROM Planter p WHERE p.createdAt >= :date")
    long countPlantersCreatedAfter(@Param("date") LocalDateTime date);

    /**
     * Compte les planteurs par village
     */
    @Query("SELECT p.village, COUNT(p) FROM Planter p GROUP BY p.village ORDER BY COUNT(p) DESC")
    List<Object[]> countPlantersByVillage();

    /**
     * Compte les planteurs par genre
     */
    @Query("SELECT p.gender, COUNT(p) FROM Planter p GROUP BY p.gender")
    List<Object[]> countByGender();

    /**
     * Compte les planteurs par statut marital
     */
    @Query("SELECT p.maritalStatus, COUNT(p) FROM Planter p GROUP BY p.maritalStatus")
    List<Object[]> countByMaritalStatus();

    /**
     * Calcule l'âge moyen des planteurs
     */
    @Query("SELECT AVG(YEAR(CURRENT_DATE) - YEAR(p.birthday)) FROM Planter p")
    Double calculateAverageAge();

    /**
     * Calcule le nombre moyen d'enfants
     */
    @Query("SELECT AVG(p.childrenNumber) FROM Planter p")
    Double averageChildrenNumber();

}