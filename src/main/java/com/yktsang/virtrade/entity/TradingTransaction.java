/*
 * TradingTransaction.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * The trading transaction. Represents the database table "trading_transaction".
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Entity
@Table(name = "trading_transaction")
public class TradingTransaction {

    /**
     * The trading transaction ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ttid")
    private BigInteger tradingTransactionId;
    /**
     * The email address.
     */
    @Column(name = "email")
    private String email;
    /**
     * The trading symbol.
     */
    @Column(name = "trading_symbol")
    private String tradingSymbol;
    /**
     * The trading symbol name.
     */
    @Column(name = "symbol_name")
    private String tradingSymbolName;
    /**
     * The transaction date.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Column(name = "transaction_date")
    private LocalDate transactionDate;
    /**
     * The trading deed.
     */
    @Convert(converter = TradingDeedEnumConverter.class)
    @Column(name = "trading_deed")
    private TradingDeed tradingDeed;
    /**
     * The quantity.
     */
    @Column(name = "quantity")
    private int quantity;
    /**
     * The transaction currency.
     */
    @Column(name = "transaction_currency")
    private String transactionCurrency;
    /**
     * The transaction price.
     */
    @Column(name = "transaction_price")
    private BigDecimal transactionPrice;
    /**
     * The transaction cost.
     */
    @Column(name = "transaction_cost")
    private BigDecimal transactionCost;

    /**
     * The portfolio ID.
     * Foreign key to table "portfolio" represented by <code>Portfolio</code>.
     */
    @Column(name = "portfolio_id")
    private BigInteger portfolioId;
    /**
     * The creation datetime.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_datetime")
    private LocalDateTime creationDateTime;
    /**
     * The last updated datetime.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_updated_datetime")
    private LocalDateTime lastUpdatedDateTime;
    /**
     * The URL encoded trading symbol.
     */
    @Transient
    private String encodedSymbol;

    /**
     * Constructs a <code>TradingTransaction</code>.
     */
    public TradingTransaction() {
    }

    /**
     * Constructs a <code>TradingTransaction</code> with email address,
     * trading symbol, trading symbol name, transaction date, trading deed,
     * quantity, transaction currency, transaction price, and transaction cost.
     *
     * @param email               the email address
     * @param tradingSymbol       the trading symbol
     * @param tradingSymbolName   the trading symbol name
     * @param transactionDate     the transaction date
     * @param tradingDeed         the trading deed
     * @param quantity            the quantity
     * @param transactionCurrency the transaction currency
     * @param transactionPrice    the transaction price
     * @param transactionCost     the transaction cost
     */
    public TradingTransaction(String email,
                              String tradingSymbol, String tradingSymbolName,
                              LocalDate transactionDate, TradingDeed tradingDeed,
                              int quantity, String transactionCurrency,
                              BigDecimal transactionPrice, BigDecimal transactionCost) {
        this.email = email;
        this.tradingSymbol = tradingSymbol;
        this.tradingSymbolName = tradingSymbolName;
        this.transactionDate = transactionDate;
        this.tradingDeed = tradingDeed;
        this.quantity = quantity;
        this.transactionCurrency = transactionCurrency.toUpperCase();
        this.transactionPrice = transactionPrice;
        this.transactionCost = transactionCost;
        this.creationDateTime = LocalDateTime.now();
    }

    /**
     * Returns the trading transaction ID.
     *
     * @return the trading transaction ID
     */
    public BigInteger getTradingTransactionId() {
        return tradingTransactionId;
    }

    /**
     * Assigns the trading transaction ID.
     *
     * @param tradingTransactionId the trading transaction ID
     */
    public void setTradingTransactionId(BigInteger tradingTransactionId) {
        this.tradingTransactionId = tradingTransactionId;
    }

    /**
     * Returns the email address.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Assigns the email address.
     *
     * @param email the email address
     */
    public void setEmail(String email) {
        this.email = email;
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
     * Assigns the trading symbol.
     *
     * @param tradingSymbol the trading symbol
     */
    public void setTradingSymbol(String tradingSymbol) {
        this.tradingSymbol = tradingSymbol;
    }

    /**
     * Returns the trading symbol name.
     *
     * @return the trading symbol name
     */
    public String getTradingSymbolName() {
        return tradingSymbolName;
    }

    /**
     * Assigns the trading symbol name.
     *
     * @param tradingSymbolName the trading symbol name
     */
    public void setTradingSymbolName(String tradingSymbolName) {
        this.tradingSymbolName = tradingSymbolName;
    }

    /**
     * Return the transaction date.
     *
     * @return the transaction date
     */
    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    /**
     * Assigns the transaction date.
     *
     * @param transactionDate the transaction date
     */
    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    /**
     * Returns the trading deed.
     *
     * @return the trading deed
     */
    public TradingDeed getTradingDeed() {
        return tradingDeed;
    }

    /**
     * Assigns the trading deed.
     *
     * @param tradingDeed the trading deed
     */
    public void setTradingDeed(TradingDeed tradingDeed) {
        this.tradingDeed = tradingDeed;
    }

    /**
     * Returns the quantity.
     *
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Assigns the quantity.
     *
     * @param quantity the quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Returns the transaction currency.
     *
     * @return the transaction currency
     */
    public String getTransactionCurrency() {
        return transactionCurrency;
    }

    /**
     * Assigns the transaction currency.
     *
     * @param transactionCurrency the transaction currency
     */
    public void setTransactionCurrency(String transactionCurrency) {
        this.transactionCurrency = transactionCurrency.toUpperCase();
    }

    /**
     * Return the transaction price.
     *
     * @return the transaction price
     */
    public BigDecimal getTransactionPrice() {
        return transactionPrice;
    }

    /**
     * Assigns the transaction price.
     *
     * @param transactionPrice the transaction price
     */
    public void setTransactionPrice(BigDecimal transactionPrice) {
        this.transactionPrice = transactionPrice;
    }

    /**
     * Returns the transaction cost.
     *
     * @return the transaction cost
     */
    public BigDecimal getTransactionCost() {
        return transactionCost;
    }

    /**
     * Assigns the transaction cost.
     *
     * @param transactionCost the transaction cost
     */
    public void setTransactionCost(BigDecimal transactionCost) {
        this.transactionCost = transactionCost;
    }

    /**
     * Returns the portfolio ID.
     *
     * @return the portfolio ID
     */
    public BigInteger getPortfolioId() {
        return portfolioId;
    }

    /**
     * Assigns the portfolio ID.
     *
     * @param portfolioId the portfolio ID
     */
    public void setPortfolioId(BigInteger portfolioId) {
        this.portfolioId = portfolioId;
    }

    /**
     * Returns the creation datetime.
     *
     * @return the creation datetime
     */
    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    /**
     * Assigns the creation datetime.
     *
     * @param creationDateTime the creation datetime
     */
    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    /**
     * Returns the last updated datetime.
     *
     * @return the last updated datetime
     */
    public LocalDateTime getLastUpdatedDateTime() {
        return lastUpdatedDateTime;
    }

    /**
     * Assigns the last updated datetime.
     *
     * @param lastUpdatedDateTime the last updated datetime
     */
    public void setLastUpdatedDateTime(LocalDateTime lastUpdatedDateTime) {
        this.lastUpdatedDateTime = lastUpdatedDateTime;
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

}
