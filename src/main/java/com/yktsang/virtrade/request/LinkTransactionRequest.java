/*
 * LinkTransactionRequest.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.request;

import java.math.BigInteger;
import java.util.List;

/**
 * The link transaction request.
 *
 * @param newPortfolio                the new portfolio indicator
 * @param portfolioRequest            the create portfolio request
 * @param portfolioId                 the portfolio ID the trading transactions going to link to
 * @param tradingTransactionIdsToLink the trading transactions to link
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record LinkTransactionRequest(Boolean newPortfolio,
                                     CreatePortfolioRequest portfolioRequest,
                                     BigInteger portfolioId,
                                     List<BigInteger> tradingTransactionIdsToLink) {
}
