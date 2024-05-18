/*
 * AccountBalance.java
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
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * The account balance. Represents the database table "account_balance".
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Entity
@IdClass(AccountBalancePK.class)
@Table(name = "account_balance")
public class AccountBalance {

    /**
     * The email address.
     */
    @Id
    @Column(name = "email")
    private String email;
    /**
     * The currency.
     */
    @Id
    @Column(name = "currency")
    private String currency;
    /**
     * The trading transferAmount.
     */
    @Column(name = "trading_amount")
    private BigDecimal tradingAmount;
    /**
     * The non-trading transferAmount.
     */
    @Column(name = "non_trading_amount")
    private BigDecimal nonTradingAmount;
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
     * The number of decimal places to display.
     */
    @Transient
    private Integer decimalPlacesToDisplay;

    /**
     * Constructs a <code>AccountBalance</code>.
     */
    public AccountBalance() {
    }

    /**
     * Constructs a <code>AccountBalance</code> with email address, currency,
     * trading transferAmount, and non-trading transferAmount.
     *
     * @param email            the email address
     * @param currency         the currency
     * @param tradingAmount    the trading transferAmount
     * @param nonTradingAmount the non-trading transferAmount
     */
    public AccountBalance(String email, String currency, BigDecimal tradingAmount, BigDecimal nonTradingAmount) {
        this.email = email;
        this.currency = currency.toUpperCase();
        this.tradingAmount = tradingAmount;
        this.nonTradingAmount = nonTradingAmount;
        this.creationDateTime = LocalDateTime.now();
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
     * Returns the trading transferAmount.
     *
     * @return the trading transferAmount
     */
    public BigDecimal getTradingAmount() {
        return tradingAmount;
    }

    /**
     * Assigns the trading transferAmount.
     *
     * @param tradingAmount the trading transferAmount
     */
    public void setTradingAmount(BigDecimal tradingAmount) {
        this.tradingAmount = tradingAmount;
    }

    /**
     * Returns the non-trading transferAmount.
     *
     * @return the non-trading transferAmount
     */
    public BigDecimal getNonTradingAmount() {
        return nonTradingAmount;
    }

    /**
     * Assigns the non-trading transferAmount.
     *
     * @param nonTradingAmount the non-trading transferAmount
     */
    public void setNonTradingAmount(BigDecimal nonTradingAmount) {
        this.nonTradingAmount = nonTradingAmount;
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
     * Returns the number of decimal places to display.
     *
     * @return the number of decimal places to display
     */
    public Integer getDecimalPlacesToDisplay() {
        return Objects.isNull(decimalPlacesToDisplay) ? 2 : decimalPlacesToDisplay;
    }

    /**
     * Assigns the number of decimal places to display.
     *
     * @param decimalPlacesToDisplay the number of decimal places to display
     */
    public void setDecimalPlacesToDisplay(Integer decimalPlacesToDisplay) {
        this.decimalPlacesToDisplay = decimalPlacesToDisplay;
    }

}
