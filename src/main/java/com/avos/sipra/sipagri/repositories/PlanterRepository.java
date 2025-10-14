package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.Planter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlanterRepository extends JpaRepository<Planter, Long> {
    Page<Planter> findPlanterByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(Pageable pageable, String firstname, String lastname);

    Page<Planter> findPlanterBySupervisor_Id(Long supervisorId, Pageable pageable);

    /**
     * Compte les planteurs créés avant une date donnée
     */
    @Query("SELECT COUNT(p) FROM Planter p WHERE p.createdAt < :date")
    long countPlantersCreatedBeforeMonth(@Param("date") LocalDateTime date);

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

    /**
     * Recherche par village
     */
    List<Planter> findByVillage(String village);

    /**
     * Recherche par superviseur
     */
    List<Planter> findBySupervisorId(Long supervisorId);

    /**
     * Recherche par nom (firstname ou lastname)
     */
    @Query("SELECT p FROM Planter p WHERE LOWER(p.firstname) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(p.lastname) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Planter> searchByName(@Param("name") String name);
}

