package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.Production;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductionRepository extends JpaRepository<Production, Long> {
    @Query("SELECT p FROM Production p WHERE " +
            "(:plantationName IS NULL OR p.plantation.name LIKE %:plantationName%) OR " +
            "(:productionInKg IS NULL OR CAST(p.productionInKg AS string) LIKE %:productionInKg%)")
    Page<Production> findProductionsByPlantation_NameOrProductionInKg(
            Pageable pageable, String plantationName, String productionInKg);

    Page<Production> findProductionsByPlantation_Planter_Supervisor_Id(Pageable pageable, Long supervisorId);

    @Query("SELECT p FROM Production p ORDER BY p.year")
    List<Production> findAllOrderByYear();

    /**
     * Somme de la production totale
     */
    @Query("SELECT COALESCE(SUM(p.productionInKg), 0.0) FROM Production p")
    Double sumTotalProduction();

    /**
     * ✨ NOUVEAU : Somme de la production pour une année spécifique
     */
    @Query("SELECT COALESCE(SUM(p.productionInKg), 0.0) FROM Production p WHERE YEAR(p.year) = :year")
    Double sumProductionForYear(@Param("year") int year);

    /**
     * Somme des revenus (production * prix d'achat)
     */
    @Query("SELECT COALESCE(SUM(p.purchasePrice), 0.0) FROM Production p")
    Double sumTotalRevenue();

    /**
     * ✨ NOUVEAU : Somme des revenus pour une année spécifique
     */
    @Query("SELECT COALESCE(SUM(p.purchasePrice), 0.0) FROM Production p WHERE YEAR(p.year) = :year")
    Double sumRevenueForYear(@Param("year") int year);

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
     * ✨ CORRIGÉ : Production groupée par SECTEUR (gpsLocation.displayName)
     */
    @Query("SELECT pl.sector, COALESCE(SUM(pr.productionInKg), 0.0) " +
            "FROM Production pr " +
            "JOIN Plantation pl ON pr.plantation.id = pl.id " +
            "WHERE pl.sector IS NOT NULL " +
            "GROUP BY pl.sector " +
            "ORDER BY SUM(pr.productionInKg) DESC")
    List<Object[]> sumProductionBySector();

    /**
     * ✨ CORRIGÉ : Production groupée par SECTEUR pour une année spécifique
     */
    @Query("SELECT pl.sector, COALESCE(SUM(pr.productionInKg), 0.0) " +
            "FROM Production pr " +
            "JOIN Plantation pl ON pr.plantation.id = pl.id " +
            "WHERE pl.sector IS NOT NULL " +
            "AND YEAR(pr.year) = :year " +
            "GROUP BY pl.sector " +
            "ORDER BY SUM(pr.productionInKg) DESC")
    List<Object[]> sumProductionBySectorAndYear(@Param("year") Integer year);

    /**
     * Liste des années distinctes disponibles
     */
    @Query("SELECT DISTINCT YEAR(p.year) FROM Production p ORDER BY YEAR(p.year) DESC")
    List<Integer> findDistinctYears();

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
     * Nombre de productions non payées
     */
    long countByMustBePaidTrue();

    /**
     * Prix d'achat moyen
     */
    @Query("SELECT COALESCE(AVG(p.purchasePrice), 0.0) FROM Production p")
    Double avgPurchasePrice();
}