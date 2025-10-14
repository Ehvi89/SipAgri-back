package com.avos.sipra.sipagri.services.dtos;

import com.avos.sipra.sipagri.entities.Planter;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class PlanterDetailsDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private int age;
    private String gender;
    private String maritalStatus;
    private String phoneNumber;
    private int childrenNumber;
    private String village;
    private String supervisor;
    private List<PlantationSummaryDTO> plantations;
    private double totalProduction;
    private double totalRevenue;
    private int totalPlantations;

    public PlanterDetailsDTO(Planter planter) {
        this.id = planter.getId();
        this.firstname = planter.getFirstname();
        this.lastname = planter.getLastname();

        // Calcul de l'âge
        if (planter.getBirthday() != null) {
            this.age = Period.between(LocalDate.parse(planter.getBirthday().toString()), LocalDate.now()).getYears();
        }

        this.gender = planter.getGender() != null ? planter.getGender().toString() : null;
        this.maritalStatus = planter.getMaritalStatus() != null ? planter.getMaritalStatus().toString() : null;
        this.phoneNumber = planter.getPhoneNumber() != null ? String.valueOf(planter.getPhoneNumber()) : null;
        this.childrenNumber = planter.getChildrenNumber() != null ? planter.getChildrenNumber() : 0;
        this.village = planter.getVillage();

        // Superviseur
        if (planter.getSupervisor() != null) {
            this.supervisor = planter.getSupervisor().getFirstname() + " " +
                    planter.getSupervisor().getLastname();
        }

        // Plantations
        if (planter.getPlantations() != null && !planter.getPlantations().isEmpty()) {
            this.plantations = planter.getPlantations().stream()
                    .map(PlantationSummaryDTO::new)
                    .collect(Collectors.toList());

            this.totalPlantations = this.plantations.size();

            this.totalProduction = plantations.stream()
                    .mapToDouble(PlantationSummaryDTO::getTotalProduction)
                    .sum();

            this.totalRevenue = plantations.stream()
                    .mapToDouble(PlantationSummaryDTO::getTotalRevenue)
                    .sum();
        } else {
            this.plantations = new ArrayList<>();
            this.totalPlantations = 0;
            this.totalProduction = 0.0;
            this.totalRevenue = 0.0;
        }
    }
}