package com.avos.sipra.sipagri.services.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialStatsDTO {
    private double totalRevenue;
    private double averagePurchasePrice;
    private double totalPaidAmount;
    private double totalUnpaidAmount;
    private long paidProductionsCount;
    private long unpaidProductionsCount;
}