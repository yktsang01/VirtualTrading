/*
 * AccountTransactionControllerTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.controller;

import com.yktsang.virtrade.api.controller.AccountTransactionService;
import com.yktsang.virtrade.api.controller.IsoDataService;
import com.yktsang.virtrade.api.jwt.JwtService;
import com.yktsang.virtrade.response.AccountTransactionResponse;
import com.yktsang.virtrade.response.IsoCurrencyResponse;
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
 * Provides the test cases for <code>AccountTransactionController</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AccountTransactionControllerTests {

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
     * The mocked account transaction service.
     */
    @MockBean
    private AccountTransactionService acctTxnService;

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
     * Shows the account transaction page with no query parameter and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showAccountTransactionWithNoQueryParamAndProperSession() throws Exception {
        AccountTransactionResponse mockedResp = new AccountTransactionResponse(new ArrayList<>());
        when(acctTxnService.accountTransactions(any(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/accountTxn").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the account transaction page with no query parameter and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showAccountTransactionWithNoQueryParamAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/accountTxn").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the account transaction page with no query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showAccountTransactionWithNoQueryParamValAndProperSession() throws Exception {
        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/accountTxn?ccy=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the account transaction page with no query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showAccountTransactionWithNoQueryParamValAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/accountTxn?ccy=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the account transaction page with query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showAccountTransactionWithQueryParamValAndProperSession() throws Exception {
        AccountTransactionResponse mockedResp = new AccountTransactionResponse(new ArrayList<>());
        when(acctTxnService.accountTransactions(any(), anyString(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/accountTxn?ccy=XXX").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the account transaction page with query parameter value and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showAccountTransactionWithQueryParamValAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/accountTxn?ccy=XXX").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

}
