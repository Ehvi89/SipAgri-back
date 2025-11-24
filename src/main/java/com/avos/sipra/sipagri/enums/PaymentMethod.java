package com.avos.sipra.sipagri.enums;

/**
 * Enumeration representing various payment methods available
 * for transactions and planter payouts. Each constant specifies
 * a distinct payment option supported in the system.
 */
public enum PaymentMethod {
    /**
     * Represents the payment method using a cheque for planter payouts
     * and transactions. This method is typically used when transactions
     * are settled through written, dated, and signed financial instruments
     * instructing a bank to pay a specific amount of money to a specified person.
     */
    CHEQUE,
    /**
     * Represents the WAVE payment method.
     * This payment option is available for planter payouts and transactions.
     */
    WAVE,
    /**
     * Represents the Orange Money payment method.
     * This payment option is used for transactions and payouts
     * facilitated through the Orange Money platform, which is
     * widely used in specific regions for secure and efficient
     * financial operations.
     */
    ORANGE_MONEY,
    /**
     * Represents the MTN Money payment method.
     * This payment option is used for transactions and payouts
     * facilitated through the MTN Money platform.
     */
    MTN_MONEY,
    /**
     * Represents the payment method option using MOOV Money for planter payouts
     * and transactions. MOOV Money is a mobile payment system commonly used in
     * specific regions for financial operations.
     */
    MOOV_MONEY
}
