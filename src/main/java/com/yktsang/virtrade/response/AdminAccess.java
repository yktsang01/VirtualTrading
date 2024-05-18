/*
 * AdminAccess.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yktsang.virtrade.util.DateTimeUtil;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * The admin access.
 * Fields come from <code>com.yktsang.virtrade.entity.Account</code>.
 *
 * @param email                 the email address
 * @param creationDateTime      the user creation datetime
 * @param adminRequestDateTime  the admin request datetime
 * @param adminApprovalDateTime the admin approval datetime
 * @param adminApproveBy        the admin approve by
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record AdminAccess(String email,
                          @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
                          LocalDateTime creationDateTime,
                          @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
                          LocalDateTime adminRequestDateTime,
                          @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
                          LocalDateTime adminApprovalDateTime,
                          String adminApproveBy) {

    /**
     * Returns the user creation datetime as <code>java.util.Date</code>
     *
     * @return the user creation datetime as java.util.Date
     */
    @JsonIgnore
    public Date creationDateTimeAsDate() {
        return DateTimeUtil.toDate(creationDateTime);
    }

    /**
     * Returns the admin request datetime as <code>java.util.Date</code>
     *
     * @return the admin request datetime as java.util.Date
     */
    @JsonIgnore
    public Date adminRequestDateTimeAsDate() {
        return DateTimeUtil.toDate(adminRequestDateTime);
    }

    /**
     * Returns the admin approval datetime as <code>java.util.Date</code>
     *
     * @return the admin approval datetime as java.util.Date
     */
    @JsonIgnore
    public Date adminApprovalDateTimeAsDate() {
        return DateTimeUtil.toDate(adminApprovalDateTime);
    }

}
