/*
 * TransferFundRequest.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.request;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * The transfer fund request.
 *
 * @param fromAccountCurrency the account balance currency transferring from
 * @param toBankAccountId     the bank account ID transferring to
 * @param transferAmount      the transfer amount
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record TransferFundRequest(String fromAccountCurrency,
                                  BigInteger toBankAccountId,
                                  BigDecimal transferAmount) {
}
