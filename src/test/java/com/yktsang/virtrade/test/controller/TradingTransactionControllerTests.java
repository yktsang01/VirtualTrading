/*
 * TradingTransactionControllerTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.controller;

import com.yktsang.virtrade.api.controller.IsoDataService;
import com.yktsang.virtrade.api.controller.PortfolioService;
import com.yktsang.virtrade.api.controller.TradingTransactionService;
import com.yktsang.virtrade.api.jwt.JwtService;
import com.yktsang.virtrade.response.IsoCurrencyResponse;
import com.yktsang.virtrade.response.PortfolioResponse;
import com.yktsang.virtrade.response.TradingTransactionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import static com.yktsang.virtrade.test.controller.MemberSessionHelper.getImproperMockSession;
import static com.yktsang.virtrade.test.controller.MemberSessionHelper.getProperMockSession;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Provides the test cases for <code>TradingTransactionController</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TradingTransactionControllerTests {

    /**
     * The mocked MVC.
     */
    @Autowired
    private MockMvc mvc;
    /**
     * The mocked JWT service.
     */
    @MockBean
    private JwtService jwtService;
    /**
     * The mocked ISO data service.
     */
    @MockBean
    private IsoDataService isoDataService;
    /**
     * The mocked trading transaction service.
     */
    @MockBean
    private TradingTransactionService tradingTxnService;
    /**
     * The mocked portfolio service.
     */
    @MockBean
    private PortfolioService portfolioService;

    /**
     * Initializes the JWT.
     */
    @BeforeEach
    public void init() {
        when(jwtService.generateToken(anyString()))
                .thenReturn("sometoken");
        when(jwtService.extractExpiration(anyString()))
                .thenReturn(new Date());
        when(isoDataService.activeCurrencies(any()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(new IsoCurrencyResponse(new HashSet<>())));
    }

    /**
     * Shows the trading transaction page with no query parameter and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showTradingTransactionWithNoQueryParamAndProperSession() throws Exception {
        TradingTransactionResponse mockedTxnResp = new TradingTransactionResponse(new ArrayList<>());
        when(tradingTxnService.tradingTransactions(any(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedTxnResp));
        PortfolioResponse mockedPortResp = new PortfolioResponse(new ArrayList<>());
        when(portfolioService.portfolios(any(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedPortResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/tradingTxn").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the trading transaction page with no query parameter and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showTradingTransactionWithNoQueryParamAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/tradingTxn").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the trading transaction page with no query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showTradingTransactionWithNoQueryParamValAndProperSession() throws Exception {
        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/tradingTxn?ccy=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the trading transaction page with no query parameter value and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showTradingTransactionWithNoQueryParamValAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/tradingTxn?ccy=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the trading transaction page with query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showTradingTransactionWithQueryParamValAndProperSession() throws Exception {
        TradingTransactionResponse mockedResp = new TradingTransactionResponse(new ArrayList<>());
        when(tradingTxnService.tradingTransactions(any(), anyString(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));
        PortfolioResponse mockedPortResp = new PortfolioResponse(new ArrayList<>());
        when(portfolioService.portfolios(any(), anyString(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedPortResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/tradingTxn?ccy=XXX").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the trading transaction page with query parameter value and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showTradingTransactionWithQueryParamValAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/tradingTxn?ccy=XXX").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

}
