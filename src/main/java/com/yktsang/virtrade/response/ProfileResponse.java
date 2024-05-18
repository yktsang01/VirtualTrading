/*
 * ProfileResponse.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.response;

/**
 * The profile response.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public class ProfileResponse {

    /**
     * The trader profile.
     */
    private final TraderProfile trader;

    /**
     * Creates a <code>ProfileResponse</code> with trader profile.
     *
     * @param trader the trader profile
     */
    public ProfileResponse(TraderProfile trader) {
        this.trader = trader;
    }

    /**
     * Returns the trader profile.
     *
     * @return the trader profile
     */
    public TraderProfile getTrader() {
        return trader;
    }

}
