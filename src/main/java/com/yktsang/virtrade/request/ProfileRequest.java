/*
 * ProfileRequest.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.request;

/**
 * The profile request.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public class ProfileRequest {

    /**
     * The email address.
     */
    private final String email;
    /**
     * The full name.
     */
    private final String fullName;
    /**
     * The year of birth.
     */
    private final Integer birthYear;
    /**
     * The month of birth.
     */
    private final Integer birthMonth;
    /**
     * The day of birth.
     */
    private final Integer birthDay;
    /**
     * The hide date of birth indicator.
     */
    private final Boolean hideDateOfBirth;
    /**
     * The risk tolerance.
     */
    private final String riskTolerance;
    /**
     * The auto transfer to bank indicator.
     */
    private final Boolean autoTransferToBank;
    /**
     * The allow reset portfolio indicator.
     */
    private final Boolean allowReset;

    /**
     * Creates a <code>ProfileRequest</code> with email address, full name,
     * year of birth, month of birth, day of birth, hide date of birth indicator, risk tolerance,
     * auto transfer to bank indicator, and allow reset portfolio indicator.
     *
     * @param email              the email address
     * @param fullName           the full name
     * @param birthYear          the year of birth
     * @param birthMonth         the month of birth
     * @param birthDay           the day of birth
     * @param hideDateOfBirth    the hide date of birth indicator
     * @param riskTolerance      the risk tolerance
     * @param autoTransferToBank the auto transfer to bank indicator
     * @param allowReset         the allow reset portfolio indicator
     */
    public ProfileRequest(String email, String fullName,
                          Integer birthYear, Integer birthMonth, Integer birthDay,
                          Boolean hideDateOfBirth, String riskTolerance,
                          Boolean autoTransferToBank, Boolean allowReset) {
        this.email = email;
        this.fullName = fullName;
        this.birthYear = birthYear;
        this.birthMonth = birthMonth;
        this.birthDay = birthDay;
        this.hideDateOfBirth = hideDateOfBirth;
        this.riskTolerance = riskTolerance;
        this.autoTransferToBank = autoTransferToBank;
        this.allowReset = allowReset;
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
     * Returns the full name.
     *
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Returns the year of birth.
     *
     * @return the year of birth
     */
    public Integer getBirthYear() {
        return birthYear;
    }

    /**
     * Returns the month of birth.
     *
     * @return the month of birth
     */
    public Integer getBirthMonth() {
        return birthMonth;
    }

    /**
     * Returns the day of birth.
     *
     * @return the day of birth
     */
    public Integer getBirthDay() {
        return birthDay;
    }

    /**
     * Returns the hide date of birth indicator.
     *
     * @return the hide date of birth indicator
     */
    public Boolean isHideDateOfBirth() {
        return hideDateOfBirth;
    }

    /**
     * Returns the risk tolerance.
     *
     * @return the risk tolerance
     */
    public String getRiskTolerance() {
        return riskTolerance;
    }

    /**
     * Returns the auto transfer to bank indicator.
     *
     * @return the auto transfer to bank indicator
     */
    public Boolean isAutoTransferToBank() {
        return autoTransferToBank;
    }

    /**
     * Returns the allow reset portfolio indicator.
     *
     * @return the allow reset portfolio indicator
     */
    public Boolean isAllowReset() {
        return allowReset;
    }

}
