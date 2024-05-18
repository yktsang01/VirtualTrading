/*
 * DepositFundRequest.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.request;

import java.math.BigDecimal;

/**
 * The deposit fund request.
 *
 * @param currency      the currency
 * @param depositAmount the deposit amount
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record DepositFundRequest(String currency, BigDecimal depositAmount) {
}
