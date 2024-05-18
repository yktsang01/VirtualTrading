/*
 * AccountBalance.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.response;

import java.math.BigDecimal;

/**
 * The account balance.
 * Fields come from <code>com.yktsang.virtrade.entity.AccountBalance</code>.
 *
 * @param email                  the email address
 * @param currency               the currency
 * @param tradingAmount          the trading amount
 * @param nonTradingAmount       the non-trading amount
 * @param decimalPlacesToDisplay the number of decimal places to display
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record AccountBalance(String email, String currency,
                             BigDecimal tradingAmount, BigDecimal nonTradingAmount,
                             Integer decimalPlacesToDisplay) {
}
