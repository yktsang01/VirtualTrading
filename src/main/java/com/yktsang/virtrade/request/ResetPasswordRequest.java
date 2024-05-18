/*
 * ResetPasswordRequest.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.request;

/**
 * The reset password request.
 *
 * @param username        the username, representing an email address
 * @param password        the password
 * @param confirmPassword the confirmed password
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record ResetPasswordRequest(String username,
                                   String password, String confirmPassword) {
}
