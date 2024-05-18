/*
 * SellRequest.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.request;

import java.math.BigInteger;

/**
 * The sell request.
 *
 * @param symbol             the trading symbol to sell
 * @param quantityToSell     the quantity to sell
 * @param autoTransferToBank the auto transfer to bank indicator
 * @param bankAccountId      the bank account ID where funds will be auto transfer to
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record SellRequest(String symbol, Integer quantityToSell,
                          Boolean autoTransferToBank, BigInteger bankAccountId) {
}
