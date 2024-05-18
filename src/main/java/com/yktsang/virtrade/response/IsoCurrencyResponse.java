/*
 * IsoCurrencyResponse.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.response;

import com.yktsang.virtrade.entity.IsoCurrency;

import java.util.Set;

/**
 * The ISO currency response.
 *
 * @param activeCurrencies the active currencies
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record IsoCurrencyResponse(Set<IsoCurrency> activeCurrencies) {
}
