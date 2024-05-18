/*
 * RiskToleranceLevelEnumConverterTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.entity;

import com.yktsang.virtrade.entity.RiskToleranceLevel;
import com.yktsang.virtrade.entity.RiskToleranceLevelEnumConverter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Provides the test cases for <code>RiskToleranceLevelEnumConverter</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class RiskToleranceLevelEnumConverterTests {

    /**
     * Converts HIGH risk tolerance level to HIGH database value.
     */
    @Test
    public void convertHighRiskToleranceLevelToDatabaseValue() {
        String dbVal = new RiskToleranceLevelEnumConverter().convertToDatabaseColumn(RiskToleranceLevel.HIGH);
        assertEquals("HIGH", dbVal);
    }

    /**
     * Converts MEDIUM risk tolerance level to MEDIUM database value.
     */
    @Test
    public void convertMediumRiskToleranceLevelToDatabaseValue() {
        String dbVal = new RiskToleranceLevelEnumConverter().convertToDatabaseColumn(RiskToleranceLevel.MEDIUM);
        assertEquals("MEDIUM", dbVal);
    }

    /**
     * Converts LOW risk tolerance level to LOW database value.
     */
    @Test
    public void convertLowRiskToleranceLevelToDatabaseValue() {
        String dbVal = new RiskToleranceLevelEnumConverter().convertToDatabaseColumn(RiskToleranceLevel.LOW);
        assertEquals("LOW", dbVal);
    }

    /**
     * Converts HIGH database value to HIGH risk tolerance level.
     */
    @Test
    public void convertHighDatabaseValueToRiskToleranceLevel() {
        Enum<RiskToleranceLevel> level = new RiskToleranceLevelEnumConverter().convertToEntityAttribute("HIGH");
        assertEquals("HIGH", level.name());
    }

    /**
     * Converts MEDIUM database value to MEDIUM risk tolerance level.
     */
    @Test
    public void convertMediumDatabaseValueToRiskToleranceLevel() {
        Enum<RiskToleranceLevel> level = new RiskToleranceLevelEnumConverter().convertToEntityAttribute("MEDIUM");
        assertEquals("MEDIUM", level.name());
    }

    /**
     * Converts LOW database value to LOW risk tolerance level.
     */
    @Test
    public void convertLowDatabaseValueToRiskToleranceLevel() {
        Enum<RiskToleranceLevel> level = new RiskToleranceLevelEnumConverter().convertToEntityAttribute("LOW");
        assertEquals("LOW", level.name());
    }

    /**
     * Converts unknown database value to risk tolerance level.
     */
    @Test
    public void convertUnknownDatabaseValueToRiskToleranceLevel() {
        assertThrows(IllegalStateException.class, () -> new RiskToleranceLevelEnumConverter().convertToEntityAttribute("BLA"));
    }

}
