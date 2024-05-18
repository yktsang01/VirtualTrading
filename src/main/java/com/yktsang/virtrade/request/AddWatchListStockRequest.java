/*
 * AddWatchListStockRequest.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.request;

import java.util.List;

/**
 * The add watch list stock request.
 *
 * @param stockSymbolsToAdd the stock symbols to add
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record AddWatchListStockRequest(List<String> stockSymbolsToAdd) {
}
