/*
 * StockSymbol.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.yahoofinance;

/**
 * The stock symbol.
 *
 * @param symbol      the symbol
 * @param description the description
 * @param type        the type (index or equity)
 * @param currency    the currency
 * @param location    the location
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record StockSymbol(String symbol, String description,
                          String type, String currency, String location) {
}
