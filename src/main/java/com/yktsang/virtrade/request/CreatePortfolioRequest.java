/*
 * CreatePortfolioRequest.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.request;

/**
 * The create portfolio request.
 *
 * @param name     the portfolio name
 * @param currency the portfolio currency
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record CreatePortfolioRequest(String name, String currency) {
}
