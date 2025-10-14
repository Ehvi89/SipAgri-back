package com.avos.sipra.sipagri.services.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanterDemographicsDTO {
    private List<ChartDataDTO> byGender;
    private List<ChartDataDTO> byMaritalStatus;
    private Double averageAge;
    private Double averageChildren;
}
