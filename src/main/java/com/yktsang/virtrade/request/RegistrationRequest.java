/*
 * RegistrationRequest.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.request;

/**
 * The registration request.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public class RegistrationRequest extends ProfileRequest {

    /**
     * The password.
     */
    private final String password;
    /**
     * The confirmed password.
     */
    private final String confirmPassword;

    /**
     * Creates a <code>RegistrationRequest</code> with email address, password, confirmed password,
     * full name, year of birth, month of birth, day of birth, hide date of birth indicator, risk tolerance,
     * auto transfer to bank indicator, and allow reset portfolio indicator.
     *
     * @param email              the email address
     * @param password           the password
     * @param confirmPassword    the confirmed password
     * @param fullName           the full name
     * @param birthYear          the year of birth
     * @param birthMonth         the month of birth
     * @param birthDay           the day of birth
     * @param hideDateOfBirth    the hide date of birth indicator
     * @param riskTolerance      the risk tolerance
     * @param autoTransferToBank the auto transfer to bank indicator
     * @param allowReset         the allow reset portfolio indicator
     */
    public RegistrationRequest(String email, String password, String confirmPassword,
                               String fullName, Integer birthYear, Integer birthMonth, Integer birthDay,
                               Boolean hideDateOfBirth, String riskTolerance,
                               Boolean autoTransferToBank, Boolean allowReset) {
        super(email, fullName, birthYear, birthMonth, birthDay, hideDateOfBirth,
                riskTolerance, autoTransferToBank, allowReset);
        this.password = password;
        this.confirmPassword = confirmPassword;
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
     * Returns the confirmed password.
     *
     * @return the confirmed password
     */
    public String getConfirmPassword() {
        return confirmPassword;
    }

}
