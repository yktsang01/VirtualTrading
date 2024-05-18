/*
 * TradingUtilTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.util;

import com.yktsang.virtrade.util.TradingUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Provides the test cases for <code>TradingUtil</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class TradingUtilTests {

    /**
     * Tests calculating fees.
     */
    @Test
    public void calculateFees() {
        double transactionCost = 1500.00D;
        BigDecimal bigDecFees = TradingUtil.calculateFees(new BigDecimal(transactionCost));
        double doubleFees = 0.50D
                + (transactionCost * 0.005 / 100)
                + (transactionCost * 0.002 / 100)
                + (transactionCost * 0.005 / 100)
                + Math.ceil((transactionCost * 0.1 / 100));
        assertEquals(new BigDecimal(doubleFees).setScale(4, RoundingMode.HALF_UP), bigDecFees);
    }

}
