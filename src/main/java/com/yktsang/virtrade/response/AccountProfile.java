/*
 * AccountProfile.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.response;

/**
 * The account profile.
 * Fields come from <code>com.yktsang.virtrade.entity.Account</code>.
 *
 * @param email          the email address
 * @param admin          the admin indicator
 * @param adminRequested the admin requested indicator
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record AccountProfile(String email, boolean admin, boolean adminRequested) {
}
