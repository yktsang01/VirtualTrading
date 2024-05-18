/*
 * UpdateIsoRequest.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.request;

/**
 * The update ISO request.
 *
 * @param countryCode        the country code
 * @param countryName        the country name
 * @param currencyCode       the currency code
 * @param currencyName       the currency name
 * @param currencyMinorUnits the currency minor units (number of decimal places)
 * @param deactivate         the deactivate indicator
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record UpdateIsoRequest(String countryCode, String countryName,
                               String currencyCode, String currencyName, Integer currencyMinorUnits,
                               Boolean deactivate) {
}
