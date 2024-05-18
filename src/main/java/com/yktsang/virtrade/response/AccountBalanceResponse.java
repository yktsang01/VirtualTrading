/*
 * AccountBalanceResponse.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.response;

import java.util.List;

/**
 * The account balance response.
 *
 * @param balances the account balances
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record AccountBalanceResponse(List<AccountBalance> balances) {
}
