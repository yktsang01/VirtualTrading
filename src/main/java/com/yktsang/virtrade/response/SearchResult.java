/*
 * SearchResult.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.response;

import java.math.BigDecimal;

/**
 * The search result
 *
 * @param symbol        the trading symbol
 * @param encodedSymbol the URL encoded trading symbol
 * @param name          the trading symbol name
 * @param index         the index indicator
 * @param currency      the currency
 * @param price         the price
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record SearchResult(String symbol, String encodedSymbol, String name, boolean index,
                           String currency, BigDecimal price) {
}
