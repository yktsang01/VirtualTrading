/*
 * AuthRequest.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.request;

/**
 * The auth request.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public class AuthRequest {

    /**
     * The username. Represents an email address.
     */
    private final String username;
    /**
     * The password.
     */
    private final String password;
    /**
     * The admin login indicator.
     */
    private Boolean adminLogin;

    /**
     * Creates a <code>AuthRequest</code> with username and password.
     *
     * @param username the username
     * @param password the password
     */
    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
        this.adminLogin = false;
    }

    /**
     * Returns the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the admin login indicator.
     *
     * @return the admin login indicator
     */
    public Boolean isAdminLogin() {
        return adminLogin;
    }

    /**
     * Assigns the admin login indicator.
     *
     * @param adminLogin the admin login indicator.
     */
    public void setAdminLogin(Boolean adminLogin) {
        this.adminLogin = adminLogin;
    }

}
