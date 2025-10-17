package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.Production;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface ProductionRepository extends JpaRepository<Production, Long> {
    @Query("SELECT p FROM Production p WHERE " +
            "(:plantationName IS NULL OR p.plantation.name LIKE %:plantationName%) OR " +
            "(:productionInKg IS NULL OR CAST(p.productionInKg AS string) LIKE %:productionInKg%)")
    Page<Production> findProductionsByPlantation_NameOrProductionInKg(Pageable pageable, String plantationName, String productionInKg);

    Page<Production> findProductionsByPlantation_Planter_Supervisor_Id(Pageable pageable, Long supervisorId);

    @Query("SELECT p FROM Production p ORDER BY p.year")
    List<Production> findAllOrderByYear();

    /**
     * Somme de la production totale
     */
    @Query("SELECT COALESCE(SUM(p.productionInKg), 0.0) FROM Production p")
    Double sumTotalProduction();

    /**
     * Somme de la production avant une date
     */
    @Query("SELECT COALESCE(SUM(p.productionInKg), 0.0) FROM Production p WHERE p.year < :date")
    Double sumProductionBeforeMonth(@Param("date") LocalDateTime date);

    /**
     * Somme des revenus (production * prix d'achat)
     */
    @Query("SELECT COALESCE(SUM(p.productionInKg * p.purchasePrice), 0.0) FROM Production p")
    Double sumTotalRevenue();

    /**
     * Somme des revenus avant une date
     */
    @Query("SELECT COALESCE(SUM(p.productionInKg * p.purchasePrice), 0.0) FROM Production p WHERE p.year < :date")
    Double sumRevenueBeforeMonth(@Param("date") LocalDateTime date);

    /**
     * Production groupée par plantation
     */
    @Query("SELECT pl.name, COALESCE(SUM(pr.productionInKg), 0.0) " +
            "FROM Production pr " +
            "JOIN Plantation pl ON pr.plantation.id = pl.id " +
            "GROUP BY pl.id, pl.name " +
            "ORDER BY SUM(pr.productionInKg) DESC")
    List<Object[]> sumProductionByPlantation();

    /**
     * Top planteurs par production
     */
    @Query("SELECT p.firstname, p.lastname, COALESCE(SUM(prod.productionInKg), 0.0) " +
            "FROM Production prod " +
            "JOIN Plantation pl ON prod.plantation.id = pl.id " +
            "JOIN Planter p ON pl.planter.id = p.id " +
            "GROUP BY p.id, p.firstname, p.lastname " +
            "ORDER BY SUM(prod.productionInKg) DESC")
    List<Object[]> findTopPlantersByProduction(@Param("limit") int limit);

    /**
     * Productions entre deux dates
     */
    @Query("SELECT p FROM Production p WHERE p.year BETWEEN :startDate AND :endDate ORDER BY p.year")
    List<Production> findByYearBetween(@Param("startDate") Date startDate,
                                       @Param("endDate") Date endDate);

    /**
     * Nombre de productions non payées
     */
    long countByMustBePaidTrue();

    /**
     * Prix d'achat moyen
     */
    @Query("SELECT COALESCE(AVG(p.purchasePrice), 0.0) FROM Production p")
    Double avgPurchasePrice();

}
