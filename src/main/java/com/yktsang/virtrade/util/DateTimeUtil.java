/*
 * DateTimeUtil.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Provides the date/time-related utility functions.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public class DateTimeUtil {

    /**
     * Returns the <code>java.util.Date</code> given the <code>java.time.LocalDateTime</code>.
     * The outputted date is displayed in the UTC time zone.
     *
     * @param datetime the java.time.LocalDateTime
     * @return the java.util.Date
     */
    public static Date toDate(LocalDateTime datetime) {
        return Date.from(datetime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Returns the <code>java.util.Date</code> given the <code>java.time.LocalDate</code>.
     * The outputted date is displayed in the UTC time zone.
     *
     * @param date the java.time.LocalDate
     * @return the java.util.Date
     */
    public static Date toDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

}
