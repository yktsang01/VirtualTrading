/*
 * Portfolio.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * The portfolio.
 * Fields come from <code>com.yktsang.virtrade.entity.Portfolio</code>.
 *
 * @param portfolioId      the portfolio ID
 * @param email            the email address
 * @param portfolioName    the portfolio name
 * @param currency         the currency
 * @param investedAmount   the invested amount
 * @param currentAmount    the current amount
 * @param profitLossAmount the profit and loss amount
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record Portfolio(BigInteger portfolioId, String email,
                        String portfolioName, String currency,
                        BigDecimal investedAmount, BigDecimal currentAmount, BigDecimal profitLossAmount) {

    /**
     * Returns the portfolio ID as string.
     *
     * @return the portfolio ID as string
     */
    @JsonIgnore
    public String portfolioIdAsString() {
        return portfolioId.toString();
    }

}
