/*
 * YahooStock.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.yahoofinance;

import yahoofinance.Stock;

/**
 * An extension to <code>yahoofinance.Stock</code> from Yahoo Finance API.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public class YahooStock extends Stock {

    /**
     * The quote type.
     */
    private String quoteType;
    /**
     * The URL encoded trading symbol.
     */
    private String encodedSymbol;
    /**
     * The index indicator.
     */
    private boolean index;

    /**
     * Creates a <code>YahooStock</code> with the given trading symbol.
     *
     * @param symbol the trading symbol
     */
    public YahooStock(String symbol) {
        super(symbol);
    }

    /**
     * Returns the quote type.
     *
     * @return the quote type
     */
    public String getQuoteType() {
        return quoteType;
    }

    /**
     * Assigns the quote type.
     *
     * @param quoteType the quote type
     */
    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
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
     * Assigns the URL encoded trading symbol.
     *
     * @param encodedSymbol the URL encoded trading symbol
     */
    public void setEncodedSymbol(String encodedSymbol) {
        this.encodedSymbol = encodedSymbol;
    }

    /**
     * Returns the index indicator.
     *
     * @return the index indicator
     */
    public boolean isIndex() {
        return index;
    }

    /**
     * Assigns the index indicator.
     *
     * @param index the index indicator
     */
    public void setIndex(boolean index) {
        this.index = index;
    }

}
