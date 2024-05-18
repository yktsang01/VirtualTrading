/*
 * TradingDeedEnumConverterTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.entity;

import com.yktsang.virtrade.entity.TradingDeed;
import com.yktsang.virtrade.entity.TradingDeedEnumConverter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Provides the test cases for <code>TradingDeedEnumConverter</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class TradingDeedEnumConverterTests {

    /**
     * Converts BUY trading deed to BUY database value.
     */
    @Test
    public void convertBuyTradingDeedToDatabaseValue() {
        String dbVal = new TradingDeedEnumConverter().convertToDatabaseColumn(TradingDeed.BUY);
        assertEquals("BUY", dbVal);
    }

    /**
     * Converts SELL trading deed to SELL database value.
     */
    @Test
    public void convertSellTradingDeedToDatabaseValue() {
        String dbVal = new TradingDeedEnumConverter().convertToDatabaseColumn(TradingDeed.SELL);
        assertEquals("SELL", dbVal);
    }

    /**
     * Converts BUY database value to BUY trading deed.
     */
    @Test
    public void convertBuyDatabaseValueToTradingDeed() {
        Enum<TradingDeed> level = new TradingDeedEnumConverter().convertToEntityAttribute("BUY");
        assertEquals("BUY", level.name());
    }

    /**
     * Converts SELL database value to SELL trading deed.
     */
    @Test
    public void convertSellDatabaseValueToTradingDeed() {
        Enum<TradingDeed> level = new TradingDeedEnumConverter().convertToEntityAttribute("SELL");
        assertEquals("SELL", level.name());
    }

    /**
     * Converts unknown database value to trading deed.
     */
    @Test
    public void convertUnknownDatabaseValueToTradingDeed() {
        assertThrows(IllegalStateException.class, () -> new TradingDeedEnumConverter().convertToEntityAttribute("BLA"));
    }

}
