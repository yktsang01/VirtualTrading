/*
 * AccountBalanceControllerTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.controller;

import com.yktsang.virtrade.api.controller.AccountBalanceService;
import com.yktsang.virtrade.api.controller.IsoDataService;
import com.yktsang.virtrade.api.jwt.JwtService;
import com.yktsang.virtrade.response.AccountBalanceResponse;
import com.yktsang.virtrade.response.IsoCurrencyResponse;
import com.yktsang.virtrade.response.SuccessResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Provides the test cases for <code>AccountBalanceController</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AccountBalanceControllerTests {

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
     * The mocked account balance service.
     */
    @MockBean
    private AccountBalanceService acctBalService;

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
     * Shows the account balance page with no query parameter and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showAccountBalanceWithNoQueryParamAndProperSession() throws Exception {
        AccountBalanceResponse mockedResp = new AccountBalanceResponse(new ArrayList<>());
        when(acctBalService.accountBalances(any(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/accountBalance").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the account balance page with no query parameter and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showAccountBalanceWithNoQueryParamAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/accountBalance").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the account balance page with no query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showAccountBalanceWithNoQueryParamValAndProperSession() throws Exception {
        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/accountBalance?ccy=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the account balance page with no query parameter value and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showAccountBalanceWithNoQueryParamValAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/accountBalance?ccy=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the account balance page with query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showAccountBalanceWithQueryParamValAndProperSession() throws Exception {
        AccountBalanceResponse mockedResp = new AccountBalanceResponse(new ArrayList<>());
        when(acctBalService.accountBalances(any(), anyString()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/accountBalance?ccy=XXX").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the account balance page with query parameter value and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showAccountBalanceWithQueryParamValAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/accountBalance?ccy=XXX").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Deposits virtual funds with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void depositWithProperSession() throws Exception {
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(acctBalService.depositFunds(any()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(post("/member/depositFunds").session(mockHttpSession)
                        .param("selectedCcy", "XXX")
                        .param("pageNum", "1")
                        .param("pageSize", "5")
                        .param("currency", "XXX")
                        .param("amount", "1000"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Deposits virtual funds with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void depositWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(post("/member/depositFunds").session(mockHttpSession))
                // no need param
                .andExpect(status().is3xxRedirection());
    }

}
