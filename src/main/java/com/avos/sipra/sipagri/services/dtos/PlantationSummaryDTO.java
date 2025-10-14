package com.avos.sipra.sipagri.services.dtos;

import com.avos.sipra.sipagri.entities.Plantation;
import com.avos.sipra.sipagri.entities.Production;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlantationSummaryDTO {
    private Long id;
    private String name;
    private String description;
    private double farmedArea;
    private LocationDTO gpsLocation;
    private String kitName;
    private int productionCount;
    private double totalProduction;
    private double totalRevenue;
    private double averagePrice;

    public PlantationSummaryDTO(Plantation plantation) {
        this.id = plantation.getId();
        this.name = plantation.getName();
        this.description = plantation.getDescription();
        this.farmedArea = plantation.getFarmedArea() != null ? plantation.getFarmedArea() : 0.0;

        // GPS Location
        if (plantation.getGpsLocation() != null) {
            this.gpsLocation = new LocationDTO(
                    plantation.getGpsLocation().getLatitude(),
                    plantation.getGpsLocation().getLongitude()
            );
        }

        // Kit
        if (plantation.getKit() != null) {
            this.kitName = plantation.getKit().getName();
        }

        // Productions
        if (plantation.getProductions() != null && !plantation.getProductions().isEmpty()) {
            this.productionCount = plantation.getProductions().size();

            this.totalProduction = plantation.getProductions().stream()
                    .mapToDouble(Production::getProductionInKg)
                    .sum();

            this.totalRevenue = plantation.getProductions().stream()
                    .mapToDouble(p -> p.getProductionInKg() * p.getPurchasePrice())
                    .sum();

            this.averagePrice = plantation.getProductions().stream()
                    .mapToDouble(Production::getPurchasePrice)
                    .average()
                    .orElse(0.0);
        } else {
            this.productionCount = 0;
            this.totalProduction = 0.0;
            this.totalRevenue = 0.0;
            this.averagePrice = 0.0;
        }
    }
}