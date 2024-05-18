/*
 * TradingTransaction.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yktsang.virtrade.entity.TradingDeed;
import com.yktsang.virtrade.util.DateTimeUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;

/**
 * The trading transaction.
 * Fields come from <code>com.yktsang.virtrade.entity.TradingTransaction</code>.
 *
 * @param tradingTransactionId the trading transaction ID
 * @param email                the email address
 * @param tradingSymbol        the trading symbol
 * @param encodedSymbol        the URL encoded trading symbol
 * @param tradingSymbolName    the trading symbol name
 * @param transactionDate      the transaction date
 * @param tradingDeed          the trading deed
 * @param quantity             the quantity
 * @param transactionCurrency  the transaction currency
 * @param transactionPrice     the transaction price
 * @param transactionCost      the transaction cost
 * @param portfolioId          the portfolio ID
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record TradingTransaction(BigInteger tradingTransactionId, String email,
                                 String tradingSymbol, String encodedSymbol, String tradingSymbolName,
                                 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
                                 LocalDate transactionDate,
                                 TradingDeed tradingDeed, Integer quantity,
                                 String transactionCurrency, BigDecimal transactionPrice,
                                 BigDecimal transactionCost, BigInteger portfolioId) {

    /**
     * Returns the trading transaction ID as string.
     *
     * @return the trading transaction ID as string
     */
    @JsonIgnore
    public String tradingTransactionIdAsString() {
        return tradingTransactionId.toString();
    }

    /**
     * Returns the transaction date as <code>java.util.Date</code>
     *
     * @return the transaction date as java.util.Date
     */
    @JsonIgnore
    public Date transactionDateAsDate() {
        return DateTimeUtil.toDate(transactionDate);
    }

}
