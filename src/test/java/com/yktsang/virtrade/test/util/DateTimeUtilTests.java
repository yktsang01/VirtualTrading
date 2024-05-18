/*
 * DateTimeUtilTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.util;

import com.yktsang.virtrade.util.DateTimeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Provides the test cases for <code>DateTimeUtil</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class DateTimeUtilTests {

    /**
     * Tests conversion from <code>java.time.LocalDateTime</code> to <code>java.util.Date</code>.
     */
    @Test
    public void convertLocalDateTimeToDate() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        Date d = DateTimeUtil.toDate(now);
        assertEquals(d.toInstant(), now.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Tests conversion from <code>java.time.LocalDate</code> to <code>java.util.Date</code>.
     */
    @Test
    public void convertLocalDateToDate() {
        LocalDate today = LocalDate.now();
        Date d = DateTimeUtil.toDate(today);
        assertEquals(d.toInstant(), today.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

}
