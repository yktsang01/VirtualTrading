/*
 * Portfolio.java
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
import java.time.LocalDateTime;
import java.util.List;

/**
 * The portfolio. Represents the database table "portfolio".
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Entity
@Table(name = "portfolio")
public class Portfolio {

    /**
     * The portfolio ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pid")
    private BigInteger portfolioId;
    /**
     * The email address.
     */
    @Column(name = "email")
    private String email;
    /**
     * The portfolio name.
     */
    @Column(name = "portfolio_name")
    private String portfolioName;
    /**
     * The currency.
     */
    @Column(name = "currency")
    private String currency;
    /**
     * The invested transferAmount.
     */
    @Column(name = "invested_amount")
    private BigDecimal investedAmount;
    /**
     * The current transferAmount.
     */
    @Column(name = "current_amount")
    private BigDecimal currentAmount;
    /**
     * The profit and loss transferAmount.
     */
    @Column(name = "profit_loss")
    private BigDecimal profitLoss;
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
     * The list of <code>TradingTransaction</code>.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "portfolio_id")
    private List<TradingTransaction> tradingTransactions;

    /**
     * Constructs a <code>Portfolio</code>.
     */
    public Portfolio() {
    }

    /**
     * Constructs a <code>Portfolio</code> with email address, portfolio name, and currency.
     *
     * @param email         the email address
     * @param portfolioName the portfolio name
     * @param currency      the currency
     */
    public Portfolio(String email, String portfolioName, String currency) {
        this(email, portfolioName, currency, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    /**
     * Constructs a <code>Portfolio</code> with email address, portfolio name, currency,
     * invested transferAmount, current transferAmount, and profit and loss transferAmount.
     *
     * @param email          the email address
     * @param portfolioName  the portfolio name
     * @param currency       the currency
     * @param investedAmount the invested transferAmount
     * @param currentAmount  the current transferAmount
     * @param profitLoss     the profit and loss transferAmount
     */
    public Portfolio(String email, String portfolioName, String currency,
                     BigDecimal investedAmount, BigDecimal currentAmount, BigDecimal profitLoss) {
        this.email = email;
        this.portfolioName = portfolioName;
        this.currency = currency.toUpperCase();
        this.investedAmount = investedAmount;
        this.currentAmount = currentAmount;
        this.profitLoss = profitLoss;
        this.creationDateTime = LocalDateTime.now();
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
     * Returns the portfolio name.
     *
     * @return the portfolio name
     */
    public String getPortfolioName() {
        return portfolioName;
    }

    /**
     * Assigns the portfolio name.
     *
     * @param portfolioName the portfolio name
     */
    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    /**
     * Returns the currency.
     *
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Assigns the currency.
     *
     * @param currency the currency
     */
    public void setCurrency(String currency) {
        this.currency = currency.toUpperCase();
    }

    /**
     * Returns the invested transferAmount.
     *
     * @return the invested transferAmount
     */
    public BigDecimal getInvestedAmount() {
        return investedAmount;
    }

    /**
     * Assigns the invested transferAmount.
     *
     * @param investedAmount the invested transferAmount
     */
    public void setInvestedAmount(BigDecimal investedAmount) {
        this.investedAmount = investedAmount;
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
     * Returns the profit and loss transferAmount.
     *
     * @return the profit and loss transferAmount
     */
    public BigDecimal getProfitLoss() {
        return profitLoss;
    }

    /**
     * Assigns the profit and loss transferAmount.
     *
     * @param profitLoss the profit and loss transferAmount
     */
    public void setProfitLoss(BigDecimal profitLoss) {
        this.profitLoss = profitLoss;
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
     * Returns the list of <code>TradingTransaction</code>.
     *
     * @return the list of TradingTransaction
     */
    public List<TradingTransaction> getTradingTransactions() {
        return tradingTransactions;
    }

    /**
     * Assigns the list of <code>TradingTransaction</code>.
     *
     * @param tradingTransactions the list of TradingTransaction
     */
    public void setTradingTransactions(List<TradingTransaction> tradingTransactions) {
        this.tradingTransactions = tradingTransactions;
    }

}
