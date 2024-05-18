/*
 * BuyRequest.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.request;

/**
 * The buy request.
 *
 * @param symbol        the trading symbol to buy
 * @param quantityToBuy the quantity to buy
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record BuyRequest(String symbol, Integer quantityToBuy) {
}
