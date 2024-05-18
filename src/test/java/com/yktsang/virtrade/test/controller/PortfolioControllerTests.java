/*
 * PortfolioControllerTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.controller;

import com.yktsang.virtrade.api.controller.IsoDataService;
import com.yktsang.virtrade.api.controller.PortfolioService;
import com.yktsang.virtrade.api.jwt.JwtService;
import com.yktsang.virtrade.response.*;
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import static com.yktsang.virtrade.test.controller.MemberSessionHelper.getImproperMockSession;
import static com.yktsang.virtrade.test.controller.MemberSessionHelper.getProperMockSession;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Provides the test cases for <code>PortfolioController</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
public class PortfolioControllerTests {

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
     * Shows the portfolio page with no query parameter and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showPortfolioWithNoQueryParamAndProperSession() throws Exception {
        PortfolioResponse mockedResp = new PortfolioResponse(new ArrayList<>());
        when(portfolioService.portfolios(any(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/portfolio").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the portfolio page with no query parameter and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showPortfolioWithNoQueryParamAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/portfolio").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the portfolio page with no query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showPortfolioWithNoQueryParamValAndProperSession() throws Exception {
        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/portfolio?ccy=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the portfolio page with no query parameter value and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showPortfolioWithNoQueryParamValAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/portfolio?ccy=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the portfolio page with query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showPortfolioWithQueryParamValAndProperSession() throws Exception {
        PortfolioResponse mockedResp = new PortfolioResponse(new ArrayList<>());
        when(portfolioService.portfolios(any(), anyString(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/portfolio?ccy=XXX").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the portfolio page with query parameter value and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showPortfolioWithQueryParamValAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/portfolio?ccy=XXX").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the portfolio details page with no query parameter and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showPortfolioDetailsWithNoQueryParamAndProperSession() throws Exception {
        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/portfolioDetails").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the portfolio details page with no query parameter and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showPortfolioDetailsWithNoQueryParamAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/portfolioDetails").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the portfolio details page with no query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showPortfolioDetailsWithNoQueryParamValAndProperSession() throws Exception {
        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/portfolioDetails?id=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the portfolio details page with no query parameter value and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showPortfolioDetailsWithNoQueryParamValAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/portfolioDetails?id=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the portfolio details page with query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showPortfolioDetailsWithQueryParamValAndProperSession() throws Exception {
        PortfolioDetailResponse mockedResp = new PortfolioDetailResponse(
                new Portfolio(BigInteger.ONE, "user@domain.com",
                        "name", "XXX",
                        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO), new ArrayList<>());
        when(portfolioService.portfolioDetails(any(), any(BigInteger.class), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/portfolioDetails?id=1").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the portfolio details page with query parameter value and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showPortfolioDetailsWithQueryParamValAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/portfolioDetails?id=1").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Creates portfolio with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void createPortfolioWithProperSession() throws Exception {
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(portfolioService.createPortfolio(any()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.CREATED).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(post("/member/createPortfolio").session(mockHttpSession)
                        .param("selectedCcy", "XXX")
                        .param("pageNum", "1")
                        .param("pageSize", "5")
                        .param("portName", "blabla")
                        .param("portCcy", "XXX"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Creates portfolio with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void createPortfolioWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(post("/member/createPortfolio").session(mockHttpSession))
                // no need param
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Links trading transactions to portfolio with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void linkToPortfolioWithProperSession() throws Exception {
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(portfolioService.linkToPortfolio(any()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(post("/member/linkToPortfolio").session(mockHttpSession)
                        .param("selectedCcy", "XXX")
                        .param("pageNum", "1")
                        .param("pageSize", "5")
                        .param("portfolio", "1")
                        .param("link", "1", "2"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Links trading transactions to portfolio with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void linkToPortfolioWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(post("/member/linkToPortfolio").session(mockHttpSession))
                // no need param
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Unlinks trading transactions from portfolio with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void unlinkFromPortfolioWithProperSession() throws Exception {
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(portfolioService.unlinkFromPortfolio(any()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(post("/member/unlinkFromPortfolio").session(mockHttpSession)
                        .param("portfolioId", "1")
                        .param("pageNum", "1")
                        .param("pageSize", "5")
                        .param("unlink", "1", "2"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Unlinks trading transactions from portfolio with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void unlinkToPortfolioWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(post("/member/unlinkFromPortfolio").session(mockHttpSession))
                // no need param
                .andExpect(status().is3xxRedirection());
    }

}
