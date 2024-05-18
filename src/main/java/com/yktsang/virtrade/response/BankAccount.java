/*
 * BankAccount.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigInteger;

/**
 * The bank account.
 * Fields come from <code>com.yktsang.virtrade.entity.BankAccount</code>.
 *
 * @param bankAccountId     the bank account ID
 * @param email             the email address
 * @param currency          the currency
 * @param bankName          the bank name
 * @param bankAccountNumber the bank account number
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record BankAccount(BigInteger bankAccountId, String email,
                          String currency, String bankName, String bankAccountNumber) {

    /**
     * Returns the bank account ID as string.
     *
     * @return the bank account ID as string
     */
    @JsonIgnore
    public String bankAccountIdAsString() {
        return bankAccountId.toString();
    }

}
