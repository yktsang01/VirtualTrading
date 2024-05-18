/*
 * AddBankAccountRequest.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.request;

/**
 * The add bank account request.
 *
 * @param currency          the currency
 * @param bankName          the bank name
 * @param bankAccountNumber the bank account number
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record AddBankAccountRequest(String currency, String bankName, String bankAccountNumber) {
}
