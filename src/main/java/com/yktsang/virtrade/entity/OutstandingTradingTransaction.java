/*
 * OutstandingTradingTransaction.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.entity;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * The outstanding trading transaction.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public class OutstandingTradingTransaction {

    /**
     * The trading symbol.
     */
    private final String tradingSymbol;
    /**
     * The trading symbol name.
     */
    private final String symbolName;
    /**
     * The transaction currency.
     */
    private final String currency;
    /**
     * The URL encoded trading symbol.
     */
    private final String encodedSymbol;
    /**
     * The outstanding quantity.
     */
    private int outstandingQuantity;
    /**
     * The current transaction price.
     */
    private BigDecimal currentPrice;
    /**
     * The current transferAmount.
     */
    private BigDecimal currentAmount;

    /**
     * Creates a <code>OutstandingTradingTransaction</code> with trading symbol,
     * trading symbol name and transaction currency.
     *
     * @param tradingSymbol the trading symbol
     * @param symbolName    the trading symbol name
     * @param currency      the transaction currency
     */
    public OutstandingTradingTransaction(String tradingSymbol, String symbolName, String currency) {
        this.tradingSymbol = tradingSymbol;
        this.symbolName = symbolName;
        this.currency = currency.toUpperCase();
        this.outstandingQuantity = 0;
        this.currentPrice = BigDecimal.ZERO;
        this.currentAmount = BigDecimal.ZERO;
        this.encodedSymbol = URLEncoder.encode(tradingSymbol, StandardCharsets.UTF_8);
    }

    /**
     * Returns the trading symbol.
     *
     * @return the trading symbol
     */
    public String getTradingSymbol() {
        return tradingSymbol;
    }

    /**
     * Returns the trading symbol name.
     *
     * @return the trading symbol name
     */
    public String getSymbolName() {
        return symbolName;
    }

    /**
     * Returns the transaction currency.
     *
     * @return the transaction currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Returns the outstanding quantity.
     *
     * @return the outstanding quantity
     */
    public int getOutstandingQuantity() {
        return outstandingQuantity;
    }

    /**
     * Assigns the outstanding quantity.
     *
     * @param outstandingQuantity the outstanding quantity
     */
    public void setOutstandingQuantity(int outstandingQuantity) {
        this.outstandingQuantity = outstandingQuantity;
    }

    /**
     * Returns the current transaction price.
     *
     * @return the current transaction price
     */
    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    /**
     * Assigns the current transaction price.
     *
     * @param currentPrice the current transaction price
     */
    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    /**
     * Returns the current transferAmount.
     *
     * @return the current transferAmount
     */
    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }

    /**
     * Assigns the current transferAmount.
     *
     * @param currentAmount the current transferAmount
     */
    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }

    /**
     * Returns the URL encoded trading symbol.
     *
     * @return the URL encoded trading symbol
     */
    public String getEncodedSymbol() {
        return encodedSymbol;
    }

    /**
     * Returns true if the provided item is the same as this item, false otherwise.
     *
     * @param o the provided item
     * @return true if the provided item is the same as this item, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (Objects.isNull(o) || getClass() != o.getClass()) {
            return false;
        }
        OutstandingTradingTransaction that = (OutstandingTradingTransaction) o;
        return Objects.equals(tradingSymbol, that.tradingSymbol);
    }

    /**
     * Returns the hash code.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(tradingSymbol);
    }

}
