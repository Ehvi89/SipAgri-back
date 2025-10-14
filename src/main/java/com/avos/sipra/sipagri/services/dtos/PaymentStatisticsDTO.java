package com.avos.sipra.sipagri.services.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentStatisticsDTO {
    private long paid;
    private long unpaid;
    private long total;

    public double getPaidPercentage() {
        return total > 0 ? (paid * 100.0) / total : 0.0;
    }

    public double getUnpaidPercentage() {
        return total > 0 ? (unpaid * 100.0) / total : 0.0;
    }
}
