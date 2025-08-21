package com.avos.sipra.sipagri.services.cores;

import com.avos.sipra.sipagri.entities.Plantation;
import com.avos.sipra.sipagri.repositories.PlantationRepository;
import com.avos.sipra.sipagri.services.dtos.ParamsDTO;
import com.avos.sipra.sipagri.services.dtos.ProductionDTO;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CalculationService {
    private final ParamsService paramsService;
    private final PlantationRepository plantationRepository;

    public CalculationService(ParamsService paramsService, PlantationRepository plantationRepository) {
        this.paramsService = paramsService;
        this.plantationRepository = plantationRepository;
    }

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
            System.out.println("Erreur calcul mustBePaid: " + e.getMessage());
            throw new  IllegalStateException("Erreur calcul mustBePaid: " + e.getMessage());
        }

        productionDTO.setMustBePaid(mustBePaid);
        return productionDTO;
    }
}
