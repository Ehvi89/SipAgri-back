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
    /**
     * Retrieves a paginated list of productions based on the provided plantation name or production amount in kilograms.
     * <p>
     * The search is performed using a case-insensitive match for the plantation name, or a match for the production amount
     * when converted to a string. Either parameter can be null to exclude it from the search criteria.
     *
     * @param pageable the pagination and sorting information.
     * @param plantationName the name of the plantation to filter productions, or null to exclude this criterion.
     * @param productionInKg the production amount in kilograms to filter productions, or null to exclude this criterion.
     * @return a paginated list of {@code Production} entities matching the search criteria. If no matches are found, an empty page is returned.
     */
    @Query("SELECT p FROM Production p WHERE " +
            "(:plantationName IS NULL OR p.plantation.name LIKE %:plantationName%) OR " +
            "(:productionInKg IS NULL OR CAST(p.productionInKg AS string) LIKE %:productionInKg%)")
    Page<Production> findProductionsByPlantation_NameOrProductionInKg(
            Pageable pageable, String plantationName, String productionInKg);

    /**
     * Finds a paginated list of productions based on the supervisor ID of the planter's plantation.
     *
     * @param pageable the pagination information including page number, size, and sorting details
     * @param supervisorId the ID of the plantation supervisor whose productions are to be retrieved
     * @return a paginated list of productions associated with the specified plantation supervisor
     */
    Page<Production> findProductionsByPlantation_Planter_Supervisor_Id(Pageable pageable, Long supervisorId);

    /**
     * Retrieves a list of all Production entities, ordered by the year in ascending order.
     *
     * @return a list of Production entities sorted by their year.
     */
    @Query("SELECT p FROM Production p ORDER BY p.year")
    List<Production> findAllOrderByYear();

    /**
     * Retrieves a list of Production entities associated with a specific supervisor,
     * ordered by the year.
     *
     * @param supervisorId the ID of the supervisor whose related productions
     *                     are to be retrieved
     * @return a list of Production entities ordered by year for the specified supervisor
     */
    @Query("SELECT p FROM Production p " +
            "WHERE p.plantation.planter.supervisor.id = :supervisorId " +
            "ORDER BY p.year")
    List<Production> findAllBySupervisorOrderByYear(@Param("supervisorId") Long supervisorId);

    /**
     * Calculates the total production in kilograms by summing up the values of the
     * 'productionInKg' field from all records in the Production entity.
     *
     * @return the total production in kilograms as a Double, or 0.0 if no production data exists.
     */
    @Query("SELECT COALESCE(SUM(p.productionInKg), 0.0) FROM Production p")
    Double sumTotalProduction();

    /**
     * Calculates the total production in kilograms for a given year.
     *
     * @param year the year for which the total production should be summed
     * @return the total production in kilograms for the specified year, or 0.0 if no production data exists for that year
     */
    @Query("SELECT COALESCE(SUM(p.productionInKg), 0.0) FROM Production p WHERE YEAR(p.year) = :year")
    Double sumProductionForYear(@Param("year") int year);

    /**
     * Calculates the total revenue by summing up the purchase price of all productions.
     *
     * @return the total revenue as a Double, or 0.0 if no records are found.
     */
    @Query("SELECT COALESCE(SUM(p.purchasePrice), 0.0) FROM Production p")
    Double sumTotalRevenue();

    /**
     * Calculates the total revenue for a specific year by summing up the purchase prices
     * of all productions recorded in that year.
     *
     * @param year the year for which the revenue is to be calculated
     * @return the total revenue as a Double for the specified year, or 0.0 if no data exists for that year
     */
    @Query("SELECT COALESCE(SUM(p.purchasePrice), 0.0) FROM Production p WHERE YEAR(p.year) = :year")
    Double sumRevenueForYear(@Param("year") int year);

    /**
     * Retrieves the total production grouped by plantation name, ordered by production in descending order.
     * <p>
     * The method uses a custom query to join the Production and Plantation entities, aggregates production
     * values for each plantation, and returns the results with plantation names and their respective
     * summed production values.
     *
     * @return a list of object arrays where each array contains:
     *         - the plantation name (String),
     *         - the total production in kilograms (Double), ordered descending by total production.
     */
    @Query("SELECT pl.name, COALESCE(SUM(pr.productionInKg), 0.0) " +
            "FROM Production pr " +
            "JOIN Plantation pl ON pr.plantation.id = pl.id " +
            "GROUP BY pl.id, pl.name " +
            "ORDER BY SUM(pr.productionInKg) DESC")
    List<Object[]> sumProductionByPlantation();

    @Query("SELECT pl.name, COALESCE(SUM(pr.productionInKg), 0.0) " +
            "FROM Production pr " +
            "JOIN Plantation pl ON pr.plantation.id = pl.id " +
            "WHERE pl.planter.supervisor.id = :supervisorId " +
            "GROUP BY pl.id, pl.name " +
            "ORDER BY SUM(pr.productionInKg) DESC")
    List<Object[]> sumProductionByPlantationBySupervisor(@Param("supervisorId") Long supervisorId);

    /**
     * Retrieves the total production grouped by sector, ordered by the total production in descending order.
     * <p>
     * The method uses a custom query to join the Production and Plantation entities, aggregates production
     * values for each sector, and returns the results with sector names and their respective summed production values.
     *
     * @return a list of object arrays where each array contains:
     *         - the sector name (String),
     *         - the total production in kilograms (Double), ordered descending by total production.
     */
    @Query("""
            SELECT pl.sector, COALESCE(SUM(pr.productionInKg), 0.0)
            FROM Production pr
            JOIN Plantation pl ON pr.plantation.id = pl.id
            WHERE pl.sector IS NOT NULL
            GROUP BY pl.sector
            ORDER BY SUM(pr.productionInKg) DESC
            """)
    List<Object[]> sumProductionBySector();

    /**
     * Retrieves the total production in kilograms grouped by sector for plantations overseen
     * by a specific supervisor. The results are ordered in descending order of production.
     *
     * @param supervisorId the ID of the supervisor whose associated plantations' production
     *                      will be aggregated
     * @return a list of objects where each entry is an array containing the sector and the
     *         aggregated production (in kilograms) for that sector
     */
    @Query("""
            SELECT pl.sector, COALESCE(SUM(pr.productionInKg), 0.0)
            FROM Production pr
            JOIN Plantation pl ON pr.plantation.id = pl.id
            WHERE pl.sector IS NOT NULL
            AND pl.planter.supervisor.id = :supervisorId
            GROUP BY pl.sector
            ORDER BY SUM(pr.productionInKg) DESC
            """)
    List<Object[]> sumProductionBySupervisorBySector(@Param("supervisorId") Long supervisorId);

    /**
     * Retrieves the total production grouped by sector for a specified year, ordered by production in descending order.
     *
     * This method uses a custom query to join the Production and Plantation entities, filters data by the provided year,
     * aggregates production values for each sector, and returns the results as a list with sectors and their respective
     * total production values.
     *
     * @param year the year for which the production data should be retrieved
     * @return a list of object arrays where each array contains:
     *         - the sector name (String),
     *         - the total production in kilograms (Double), ordered descending by total production
     */
    @Query("""
            SELECT pl.sector, COALESCE(SUM(pr.productionInKg), 0.0)
            FROM Production pr
            JOIN Plantation pl ON pr.plantation.id = pl.id
            WHERE pl.sector IS NOT NULL
            AND YEAR(pr.year) = :year
            GROUP BY pl.sector
            ORDER BY SUM(pr.productionInKg) DESC
            """)
    List<Object[]> sumProductionBySectorAndYear(@Param("year") Integer year);

    /**
     * Retrieves the total production in kilograms grouped by sector for a specific supervisor and year.
     * The production is summed for each sector, where the year of production matches the given year,
     * and the plantations are supervised by the given supervisor.
     *
     * @param supervisorId the unique identifier of the supervisor whose supervised plantations are considered
     * @param year the year for which the production data is calculated
     * @return a list of objects where each element is an array containing the sector (String) as the first element
     *         and the total production in kilograms (Double) as the second element, ordered by total production in
     *         descending order
     */
    @Query("""
            SELECT pl.sector, COALESCE(SUM(pr.productionInKg), 0.0)
            FROM Production pr
            JOIN Plantation pl ON pr.plantation.id = pl.id
            WHERE pl.sector IS NOT NULL
            AND YEAR(pr.year) = :year
            AND pl.planter.supervisor.id = :supervisorId
            GROUP BY pl.sector
            ORDER BY SUM(pr.productionInKg) DESC
            """)
    List<Object[]> sumProductionBySupervisorBySectorAndYear(@Param("supervisorId") Long supervisorId, @Param("year") Integer year);

    /**
     * Retrieves a list of distinct years from the Production entity, ordered in descending order.
     *
     * @return a list of distinct years as integers, sorted in descending order.
     */
    @Query("SELECT DISTINCT YEAR(p.year) FROM Production p ORDER BY YEAR(p.year) DESC")
    List<Integer> findDistinctYears();

    /**
     * Retrieves a list of distinct years associated with productions supervised
     * by the specified supervisor, ordered in descending order.
     *
     * @param supervisorId the unique identifier of the supervisor whose productions' years are to be retrieved
     * @return a list of distinct years in descending order related to the supervisor's productions
     */
    @Query("""
            SELECT DISTINCT YEAR(p.year) FROM Production p
            WHERE p.plantation.planter.supervisor.id = :supervisorId
            ORDER BY YEAR(p.year) DESC
            """)
    List<Integer> findDistinctYearsBySupervisor(@Param("supervisorId") Long supervisorId);

    /**
     * Counts the total production associated with a specific supervisor.
     *
     * @param supervisorId the ID of the supervisor whose associated production is being counted
     * @return the total count of production entries associated with the specified supervisor
     */
    @Query("""
            SELECT COUNT(p) FROM Production p
            WHERE p.plantation.planter.supervisor.id = :supervisorId
            """)
    Double countTotalProductionBySupervisor(@Param("supervisorId") Long supervisorId);

    /**
     * Calculates the total production in kilograms for a specific year and supervisor.
     *
     * @param year the year for which the production needs to be summed
     * @param supervisorId the unique identifier of the supervisor whose plantations' production is considered
     * @return the total production in kilograms as a Double; returns 0.0 if no production data is found
     */
    @Query("""
            SELECT COALESCE(SUM(p.productionInKg), 0.0)
            FROM Production p WHERE YEAR(p.year) = :year
            AND p.plantation.planter.supervisor.id = :supervisorId
            """)
    Double sumProductionForYearBySupervisor(@Param("year") int year,
                                            @Param("supervisorId") Long supervisorId);

    /**
     * Calculates the total revenue generated by all plantations supervised by a specific supervisor.
     *
     * @param supervisorId The unique identifier of the supervisor whose total revenue is to be calculated.
     * @return The sum of all purchase prices for plantations under the specified supervisor.
     *         Returns 0.0 if no plantations are found.
     */
    @Query("""
            SELECT COALESCE(SUM(p.purchasePrice), 0.0)
            FROM Production p
            WHERE p.plantation.planter.supervisor.id = :supervisorId
            """)
    Double sumTotalRevenueBySupervisor(@Param("supervisorId") Long supervisorId);

    /**
     * Calculates the total revenue for a specific year by summing up the purchase prices
     * of all productions recorded in that year.
     *
     * @param year the year for which the revenue is to be calculated
     * @return the total revenue as a Double for the specified year, or 0.0 if no data exists for that year
     */
    @Query("""
            SELECT COALESCE(SUM(p.purchasePrice), 0.0)
            FROM Production p
            WHERE YEAR(p.year) = :year
            AND p.plantation.planter.supervisor.id = :supervisorId
            """)
    Double sumRevenueForYearBySupervisor(@Param("year") int year,
                                         @Param("supervisorId") Long supervisorId);
}