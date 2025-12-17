package com.avos.sipra.sipagri.services.cores;

import com.avos.sipra.sipagri.entities.Plantation;
import com.avos.sipra.sipagri.repositories.PlantationRepository;
import com.avos.sipra.sipagri.services.dtos.ParamsDTO;
import com.avos.sipra.sipagri.services.dtos.ProductionDTO;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * Service class responsible for performing various calculations related
 * to production values and payment requirements for plantations.
 * Uses external services and repositories to fetch required data for the computations.
 * <p>
 * It provides methods for:
 * - Calculating production values based on input data.
 * - Determining whether production payments satisfy plantation costs.
 * <p>
 * Dependencies include:
 * - ParamsService for retrieving parameter values.
 * - PlantationRepository for accessing plantation data.
 */
@Slf4j
@Service
public class CalculationService {
    /**
     * Service responsible for managing and retrieving parameter-related data.
     * This service provides methods to perform CRUD operations and custom
     * queries on parameter entities, such as finding parameters by code or name.
     */
    private final ParamsService paramsService;
    /**
     * Repository interface for managing and accessing plantation data.
     * This is used to interface with the data layer and perform CRUD operations
     * or query plantation-related entities.
     * <p>
     * The repository acts as a bridge between the service layer (e.g., CalculationService)
     * and the underlying data source, abstracting the complexities of data access.
     * Typical operations may include fetching, saving, updating, or deleting data
     * related to plantations.
     */
    private final PlantationRepository plantationRepository;

    /**
     * Constructs a CalculationService instance with the specified services and repository.
     *
     * @param paramsService the service to handle parameter-related operations
     * @param plantationRepository the repository for accessing plantation data
     */
    public CalculationService(ParamsService paramsService, PlantationRepository plantationRepository) {
        this.paramsService = paramsService;
        this.plantationRepository = plantationRepository;
    }

    /**
     * Calculates and sets specific production values for the provided {@code ProductionDTO}.
     * The method computes the purchase price based on the production in kilograms and predefined
     * price parameters, and determines whether the production must be paid using additional plantation details.
     *
     * @param productionDTO the {@link ProductionDTO} containing production data, including production amount in kilograms
     *                      and related plantation details.
     * @return the updated {@link ProductionDTO} with calculated values such as purchase price and payment status.
     * @throws IllegalArgumentException if the production in kilograms is null or less than or equal to zero.
     * @throws IllegalStateException if the required parameter for price calculation is not found or is invalid.
     */
    public ProductionDTO calculateProductionValues(ProductionDTO productionDTO) {
        if (productionDTO.getProductionInKg() == null || productionDTO.getProductionInKg() <= 0) {
            throw new IllegalArgumentException("Production en kg doit être positive et non nulle");
        }

        // Récupération du paramètre de prix
        ParamsDTO paramsDTO = paramsService.findByName("PRIX_ACHAT_MAIS");
        if (paramsDTO == null || paramsDTO.getValue() == null) {
            throw new IllegalStateException("Paramètre PRIX_ACHAT_MAIS non trouvé ou invalide");
        }

        // Calcul du prix d'achat
        double pricePerKg = Double.parseDouble(paramsDTO.getValue());
        double purchasePrice = productionDTO.getProductionInKg() * pricePerKg;
        productionDTO.setPurchasePrice(purchasePrice);

        // Calcul de la possibilité de payer
        productionDTO = calculateMustBePaid(productionDTO, productionDTO.getPlantationId());

        return productionDTO;
    }

    /**
     * Calculates whether a production must be paid based on its purchase price and the associated plantation's kit cost.
     * Updates the {@code mustBePaid} field in the provided {@link ProductionDTO}.
     *
     * @param productionDTO the production data transfer object containing purchase price and other details
     * @param plantationId the identifier of the plantation to retrieve data from, can be null
     * @return the updated {@link ProductionDTO} with the {@code mustBePaid} field set
     * @throws IllegalStateException if an error occurs while fetching plantation data or performing the calculation
     */
    public ProductionDTO calculateMustBePaid(ProductionDTO productionDTO, Long plantationId) {
        boolean mustBePaid = false;

        try {
            if (plantationId != null) {
                Optional<Plantation> plantationOpt = plantationRepository.findById(plantationId);
                if (plantationOpt.isPresent()) {
                    Plantation plantation = plantationOpt.get();
                    if (plantation.getKit() != null && plantation.getKit().getTotalCost() != null) {
                        mustBePaid = productionDTO.getPurchasePrice() >= plantation.getKit().getTotalCost();
                    }
                }
            } else if (productionDTO.getId() != null) {
                Optional<Plantation> plantationOpt = plantationRepository.findByProductions_id(productionDTO.getId());
                if (plantationOpt.isPresent()) {
                    Plantation plantation = plantationOpt.get();
                    if (plantation.getKit() != null && plantation.getKit().getTotalCost() != null) {
                        mustBePaid = productionDTO.getPurchasePrice() >= plantation.getKit().getTotalCost();
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Erreur calcul mustBePaid: " + e.getMessage());
            throw new  IllegalStateException("Erreur calcul mustBePaid: " + e.getMessage());
        }

        productionDTO.setMustBePaid(mustBePaid);
        return productionDTO;
    }
}
