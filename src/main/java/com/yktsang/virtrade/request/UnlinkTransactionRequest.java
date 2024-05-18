/*
 * UnlinkTransactionRequest.java
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
 * The unlink transaction request.
 *
 * @param portfolioId                   the portfolio ID the trading transactions linked to
 * @param tradingTransactionIdsToUnlink the trading transaction IDs to unlink
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record UnlinkTransactionRequest(BigInteger portfolioId,
                                       List<BigInteger> tradingTransactionIdsToUnlink) {
}
