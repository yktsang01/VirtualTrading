/*
 * IsoCurrency.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.entity;

import java.util.Objects;

/**
 * The ISO currency.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public class IsoCurrency implements Comparable<IsoCurrency> {

    /**
     * The currency code.
     */
    private final String currencyCode;
    /**
     * The currency name.
     */
    private String currencyName;

    /**
     * Creates a <code>IsoCurrency</code> with currency code.
     *
     * @param currencyCode the currency code
     */
    public IsoCurrency(String currencyCode) {
        this.currencyCode = currencyCode.toUpperCase();
    }

    /**
     * Creates a <code>IsoCurrency</code> with currency code and currency name.
     *
     * @param currencyCode the currency code
     * @param currencyName the currency name
     */
    public IsoCurrency(String currencyCode, String currencyName) {
        this.currencyCode = currencyCode.toUpperCase();
        this.currencyName = currencyName;
    }

    /**
     * Returns the currency alpha code.
     *
     * @return the currency alpha code
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * Returns the currency name.
     *
     * @return the currency name
     */
    public String getCurrencyName() {
        return currencyName;
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
        IsoCurrency that = (IsoCurrency) o;
        return Objects.equals(currencyCode, that.currencyCode);
    }

    /**
     * Returns the hash code.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(currencyCode);
    }

    /**
     * Returns the sorted order of <code>IsoCurrency</code>,
     * given the provided <code>IsoCurrency</code>.
     *
     * @param o the provided IsoCurrency
     * @return the sorted order of IsoCurrency
     */
    @Override
    public int compareTo(IsoCurrency o) {
        return this.currencyCode.compareTo(o.currencyCode);
    }

}
