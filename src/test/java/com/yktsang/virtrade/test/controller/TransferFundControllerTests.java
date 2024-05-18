/*
 * TransferFundControllerTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.controller;

import com.yktsang.virtrade.api.controller.AccountBalanceService;
import com.yktsang.virtrade.api.controller.BankAccountService;
import com.yktsang.virtrade.api.controller.TransferService;
import com.yktsang.virtrade.api.jwt.JwtService;
import com.yktsang.virtrade.response.AccountBalanceResponse;
import com.yktsang.virtrade.response.BankAccountResponse;
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

import static com.yktsang.virtrade.test.controller.MemberSessionHelper.getImproperMockSession;
import static com.yktsang.virtrade.test.controller.MemberSessionHelper.getProperMockSession;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Provides the test cases for <code>TransferFundController</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TransferFundControllerTests {

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
     * The mocked account balance service.
     */
    @MockBean
    private AccountBalanceService acctBalService;
    /**
     * The mocked bank account service.
     */
    @MockBean
    private BankAccountService bankAcctService;
    /**
     * The mocked transfer service.
     */
    @MockBean
    private TransferService transferService;

    /**
     * Initializes the JWT.
     */
    @BeforeEach
    public void init() {
        when(jwtService.generateToken(anyString()))
                .thenReturn("sometoken");
        when(jwtService.extractExpiration(anyString()))
                .thenReturn(new Date());
    }

    /**
     * Shows the transfer fund page with no query parameter and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showTransferFundsWithNoQueryParamAndProperSession() throws Exception {
        AccountBalanceResponse mockedAcctBalResp = new AccountBalanceResponse(new ArrayList<>());
        when(acctBalService.accountBalances(any(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedAcctBalResp));
        BankAccountResponse mockedBankAcctResp = new BankAccountResponse(new ArrayList<>());
        when(bankAcctService.bankAccounts(any(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedBankAcctResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/transferFunds").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the transfer fund page with no query parameter and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showTransferFundsWithNoQueryParamAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/transferFunds").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the transfer fund page with no query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showTransferFundsWithNoQueryParamValAndProperSession() throws Exception {
        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/transferFunds?ccy=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the transfer fund page with no query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showTransferFundsWithNoQueryParamValAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/transferFunds?ccy=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the transfer fund page with query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showTransferFundsWithQueryParamValAndProperSession() throws Exception {
        AccountBalanceResponse mockedAcctBalResp = new AccountBalanceResponse(new ArrayList<>());
        when(acctBalService.accountBalances(any(), anyString()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedAcctBalResp));
        BankAccountResponse mockedBankAcctResp = new BankAccountResponse(new ArrayList<>());
        when(bankAcctService.bankAccounts(any(), anyString(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedBankAcctResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/transferFunds?ccy=XXX").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the transfer fund page with no query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showTransferFundsWithQueryParamValAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/transferFunds?ccy=XXX").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Transfers funds with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void transferWithProperSession() throws Exception {
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(transferService.transferFunds(any()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(post("/member/transferFunds").session(mockHttpSession)
                        .param("selectedCcy", "XXX")
                        .param("accountCcy", "XXX")
                        .param("bankAcctId", "1")
                        .param("amount", "1000"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Transfer funds with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void transferWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(post("/member/transferFunds").session(mockHttpSession))
                // no need param
                .andExpect(status().is3xxRedirection());
    }

}
