package com.avos.sipra.sipagri.services.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChartDataDTO {
    private String name;
    private Double value;
    private String period;
    private String color;

    public ChartDataDTO(String name, Double value, String period) {
        this.name = name;
        this.value = value;
        this.period = period;
    }
    public ChartDataDTO(String name, Double value) {
        this.name = name;
        this.value = value;
    }
}