/*
 * TraderProfile.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yktsang.virtrade.entity.RiskToleranceLevel;
import com.yktsang.virtrade.util.DateTimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * The trader profile.
 * Fields come from <code>com.yktsang.virtrade.entity.Trader</code>.
 *
 * @param email              the email address
 * @param fullName           the full name
 * @param dateOfBirth        the date of birth
 * @param hideDateOfBirth    the show/hide date of birth indicator
 * @param riskTolerance      the risk tolerance level
 * @param autoTransferToBank the auto transfer to bank indicator
 * @param allowReset         the allow reset portfolio indicator
 * @param creationDateTime   the creation datetime
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record TraderProfile(String email, String fullName,
                            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
                            LocalDate dateOfBirth,
                            boolean hideDateOfBirth, RiskToleranceLevel riskTolerance,
                            boolean autoTransferToBank, boolean allowReset,
                            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
                            LocalDateTime creationDateTime) {

    /**
     * Returns the date of birth as <code>java.util.Date</code>
     *
     * @return the date of birth as java.util.Date
     */
    @JsonIgnore
    public Date dateOfBirthAsDate() {
        return DateTimeUtil.toDate(dateOfBirth);
    }

    /**
     * Returns the year from the date of birth as string.
     *
     * @return the year from the date of birth as string
     */
    @JsonIgnore
    public String dateOfBirthYearAsString() {
        return Integer.toString(dateOfBirth.getYear());
    }

    /**
     * Returns the creation datetime as <code>java.util.Date</code>
     *
     * @return the creation datetime as java.util.Date
     */
    @JsonIgnore
    public Date creationDateTimeAsDate() {
        return DateTimeUtil.toDate(creationDateTime);
    }

}
