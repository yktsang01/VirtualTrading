/*
 * WatchList.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.response;

import java.math.BigDecimal;

/**
 * The watch list.
 * Fields come from <code>com.yktsang.virtrade.entity.WatchList</code>.
 *
 * @param symbol   the trading symbol
 * @param name     the trading symbol name
 * @param currency the currency
 * @param price    the price
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record WatchList(String symbol, String name, String currency, BigDecimal price) {
}
