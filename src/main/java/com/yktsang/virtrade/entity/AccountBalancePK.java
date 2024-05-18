/*
 * AccountBalancePK.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.entity;

import java.util.Objects;

/**
 * The primary composite key for the database table "account_balance".
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public class AccountBalancePK {

    /**
     * The email address.
     */
    private String email;
    /**
     * The currency.
     */
    private String currency;

    /**
     * Constructs a <code>AccountBalancePK</code>.
     */
    public AccountBalancePK() {
    }

    /**
     * Constructs a <code>AccountBalancePK</code> with email address and currency.
     *
     * @param email    the email address
     * @param currency the currency
     */
    public AccountBalancePK(String email, String currency) {
        this.email = email;
        this.currency = currency.toUpperCase();
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
        AccountBalancePK balancePK = (AccountBalancePK) o;
        return Objects.equals(email, balancePK.email) && Objects.equals(currency, balancePK.currency);
    }

    /**
     * Returns the hash code.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(email, currency);
    }

}
