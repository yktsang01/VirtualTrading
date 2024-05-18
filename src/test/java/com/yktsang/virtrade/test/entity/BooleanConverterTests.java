/*
 * BooleanConverterTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.entity;

import com.yktsang.virtrade.entity.BooleanConverter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Provides the test cases for <code>BooleanConverter</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class BooleanConverterTests {

    /**
     * Converts true to Y.
     */
    @Test
    public void convertTrueToY() {
        String dbVal = new BooleanConverter().convertToDatabaseColumn(true);
        assertEquals("Y", dbVal);
    }

    /**
     * Converts false to N.
     */
    @Test
    public void convertFalseToN() {
        String dbVal = new BooleanConverter().convertToDatabaseColumn(false);
        assertEquals("N", dbVal);
    }

    /**
     * Converts Y to true.
     */
    @Test
    public void convertYToTrue() {
        boolean bool = new BooleanConverter().convertToEntityAttribute("Y");
        assertTrue(bool);
    }

    /**
     * Converts N to false.
     */
    @Test
    public void convertNToFalse() {
        boolean bool = new BooleanConverter().convertToEntityAttribute("N");
        assertFalse(bool);
    }

}
