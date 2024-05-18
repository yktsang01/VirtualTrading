/*
 * TradingUtil.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Provides the trading-related utility functions.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public class TradingUtil {

    /**
     * Returns the fees (up to 4 decimal places), given the transaction cost.
     * Fees include:
     * 0.005% for transaction levy
     * 0.005% for trading fee
     * 0.002% for investor compensation levy
     * HKD0.50 for trading tariff
     * 0.1% for stamp duty rounded up to nearest dollar
     *
     * @param transactionCost the transaction cost
     * @return the fees
     */
    public static BigDecimal calculateFees(BigDecimal transactionCost) {
        //0.005% transaction levy
        BigDecimal transactionLevy = transactionCost.multiply(new BigDecimal("0.00005"));
        //0.005% trading fee
        BigDecimal tradingFee = transactionCost.multiply(new BigDecimal("0.00005"));
        //0.002% investor compensation levy
        BigDecimal investorCompensationLevy = transactionCost.multiply(new BigDecimal("0.00002"));
        //0.1% stamp duty rounded up to nearest dollar
        BigDecimal stampDuty = transactionCost.multiply(new BigDecimal("0.001"))
                .setScale(0, RoundingMode.UP);
        //0.50 fixed
        BigDecimal tradingTariff = new BigDecimal("0.5");
        return transactionLevy.add(tradingFee)
                .add(investorCompensationLevy)
                .add(stampDuty)
                .add(tradingTariff)
                .setScale(4, RoundingMode.HALF_UP);
    }

}
