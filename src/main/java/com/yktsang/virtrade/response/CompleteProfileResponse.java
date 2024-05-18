/*
 * CompleteProfileResponse.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.response;

/**
 * The complete profile response.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public class CompleteProfileResponse extends ProfileResponse {

    /**
     * The account profile.
     */
    private final AccountProfile account;

    /**
     * Creates a <code>CompleteProfileResponse</code> with trader profile and account profile.
     *
     * @param trader  the trader profile
     * @param account the account profile
     */
    public CompleteProfileResponse(TraderProfile trader, AccountProfile account) {
        super(trader);
        this.account = account;
    }

    /**
     * Returns the account profile.
     *
     * @return the account profile
     */
    public AccountProfile getAccount() {
        return account;
    }

}
