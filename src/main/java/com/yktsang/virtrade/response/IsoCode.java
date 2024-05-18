/*
 * IsoCode.java
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
 * The ISO code.
 * Fields come from <code>com.yktsang.virtrade.entity.IsoData</code>.
 *
 * @param countryCode          the country code
 * @param countryName          the country name
 * @param currencyCode         the currency code
 * @param currencyName         the currency name
 * @param currencyMinorUnits   the currency minor units (number of decimal places)
 * @param active               the active indicator
 * @param activationDateTime   the activation datetime
 * @param activatedBy          the activated by
 * @param deactivationDateTime the deactivation datetime
 * @param deactivatedBy        the deactivated by
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record IsoCode(String countryCode, String countryName,
                      String currencyCode, String currencyName, Integer currencyMinorUnits,
                      boolean active,
                      @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
                      LocalDateTime activationDateTime,
                      String activatedBy,
                      @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
                      LocalDateTime deactivationDateTime,
                      String deactivatedBy) {

    /**
     * Returns the activation datetime as <code>java.util.Date</code>
     *
     * @return the activation datetime as java.util.Date
     */
    @JsonIgnore
    public Date activationDateTimeAsDate() {
        return DateTimeUtil.toDate(activationDateTime);
    }

    /**
     * Returns the deactivation datetime as <code>java.util.Date</code>
     *
     * @return the deactivation datetime as java.util.Date
     */
    @JsonIgnore
    public Date deactivationDateTimeAsDate() {
        return DateTimeUtil.toDate(deactivationDateTime);
    }

}
