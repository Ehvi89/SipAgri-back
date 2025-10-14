package com.avos.sipra.sipagri.services.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemographicStatsDTO {
    private double averageAge;
    private double averageChildren;
    private long maleCount;
    private long femaleCount;
    private long marriedCount;
    private long singleCount;
}