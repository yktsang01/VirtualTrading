/*
 * ResetPortfolioRequest.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.request;

/**
 * The reset portfolio request.
 *
 * @param resetAllCurrencies the reset all currencies indicator
 * @param currencyToReset    the currency to reset
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record ResetPortfolioRequest(Boolean resetAllCurrencies, String currencyToReset) {
}
