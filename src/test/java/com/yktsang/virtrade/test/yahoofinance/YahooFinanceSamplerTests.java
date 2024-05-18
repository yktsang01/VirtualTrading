/*
 * YahooFinanceSamplerTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.yahoofinance;

import com.yktsang.virtrade.yahoofinance.YahooFinanceSampler;
import com.yktsang.virtrade.yahoofinance.YahooStock;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Provides the test cases for <code>YahooFinanceSampler</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class YahooFinanceSamplerTests {

    /**
     * Tests reading the JSON file then searches for specific criteria.
     *
     * @throws IOException when there is an I/O problem
     */
    @Test
    public void readThenSearch() throws IOException {
        YahooFinanceSampler sampler = new YahooFinanceSampler();
        List<YahooStock> stocks = sampler.readOnline("https://www.yktsang.com/virtrade/stocks.json");

        List<YahooStock> symbolSearchResults = stocks.stream()
                .filter(s -> StringUtils.containsIgnoreCase(s.getSymbol(), "0005.HK"))
                .toList();
        assertEquals(1, symbolSearchResults.size());

        List<YahooStock> nameSearchResults = stocks.stream()
                .filter(s -> StringUtils.containsIgnoreCase(s.getName(), "nasdaq"))
                .toList();
        assertEquals(1, nameSearchResults.size());
    }

}
