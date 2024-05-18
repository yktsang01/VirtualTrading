/*
 * WatchList.java
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
import java.util.Objects;

/**
 * The watch list. Represents the database table "watch_list".
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Entity
@Table(name = "watch_list")
public class WatchList {

    /**
     * The watch list ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wlid")
    private BigInteger watchListId;
    /**
     * The email address.
     */
    @Column(name = "email")
    private String email;
    /**
     * The trading symbol.
     */
    @Column(name = "trading_symbol")
    private String symbol;
    /**
     * The symbol name.
     */
    @Column(name = "symbol_name")
    private String name;
    /**
     * The currency.
     */
    @Column(name = "currency")
    private String currency;
    /**
     * The trading price.
     */
    @Transient
    private BigDecimal price = BigDecimal.ZERO;
    /**
     * The addition datetime.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "addition_datetime")
    private LocalDateTime additionDateTime;
    /**
     * The removal datetime.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "removal_datetime")
    private LocalDateTime removalDateTime;

    /**
     * Creates a <code>WatchList</code>.
     */
    public WatchList() {
    }

    /**
     * Creates a <code>WatchList</code> with email address, symbol, name,and currency.
     *
     * @param email    the email address
     * @param symbol   the trading symbol
     * @param name     the symbol name
     * @param currency the currency
     */
    public WatchList(String email, String symbol, String name, String currency) {
        this.email = email;
        this.symbol = symbol;
        this.name = name;
        this.currency = currency.toUpperCase();
        this.additionDateTime = LocalDateTime.now();
    }

    /**
     * Returns the watch list ID.
     *
     * @return the watch list ID
     */
    public BigInteger getWatchListId() {
        return watchListId;
    }

    /**
     * Assigns the watch list ID.
     *
     * @param watchListId the watch list ID
     */
    public void setWatchListId(BigInteger watchListId) {
        this.watchListId = watchListId;
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
    public String getSymbol() {
        return symbol;
    }

    /**
     * Assigns the trading symbol.
     *
     * @param symbol the trading symbol
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the symbol name.
     *
     * @return the symbol name
     */
    public String getName() {
        return name;
    }

    /**
     * Assigns the symbol name.
     *
     * @param name the symbol name
     */
    public void setName(String name) {
        this.name = name;
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
     * Returns the trading price.
     *
     * @return the trading price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Assigns the trading price.
     *
     * @param price the trading price
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * Returns the addition datetime.
     *
     * @return the addition datetime
     */
    public LocalDateTime getAdditionDateTime() {
        return additionDateTime;
    }

    /**
     * Assigns the addition datetime.
     *
     * @param additionDateTime the addition datetime
     */
    public void setAdditionDateTime(LocalDateTime additionDateTime) {
        this.additionDateTime = additionDateTime;
    }

    /**
     * Returns the removal datetime.
     *
     * @return the removal datetime
     */
    public LocalDateTime getRemovalDateTime() {
        return removalDateTime;
    }

    /**
     * Assigns the removal datetime.
     *
     * @param removalDateTime the removal datetime
     */
    public void setRemovalDateTime(LocalDateTime removalDateTime) {
        this.removalDateTime = removalDateTime;
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
        WatchList watchList = (WatchList) o;
        return Objects.equals(email, watchList.email)
                && Objects.equals(symbol, watchList.symbol)
                && Objects.equals(name, watchList.name)
                && Objects.equals(currency, watchList.currency);
    }

    /**
     * Returns the hash code.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(email, symbol, name, currency);
    }

}
