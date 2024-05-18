/*
 * BankAccountControllerTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.controller;

import com.yktsang.virtrade.api.controller.BankAccountService;
import com.yktsang.virtrade.api.controller.IsoDataService;
import com.yktsang.virtrade.api.jwt.JwtService;
import com.yktsang.virtrade.response.BankAccountResponse;
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
 * Provides the test cases for <code>BankAccountController</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
public class BankAccountControllerTests {

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
     * The mocked bank account service.
     */
    @MockBean
    private BankAccountService bankAcctService;

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
     * Shows the bank info with no query parameter and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showBankInfoWithNoQueryParamAndProperSession() throws Exception {
        BankAccountResponse mockedResp = new BankAccountResponse(new ArrayList<>());
        when(bankAcctService.bankAccounts(any(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/bankInfo").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the bank info with no query parameter and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showBankInfoWithNoQueryParamAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/bankInfo").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the bank info with no query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showBankInfoWithNoQueryParamValAndProperSession() throws Exception {
        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/bankInfo?ccy=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the bank info with no query parameter value and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showBankInfoWithNoQueryParamValAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/bankInfo?ccy=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the bank info with query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showBankInfoWithQueryParamValAndProperSession() throws Exception {
        BankAccountResponse mockedResp = new BankAccountResponse(new ArrayList<>());
        when(bankAcctService.bankAccounts(any(), anyString(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/bankInfo?ccy=XXX").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the bank info with query parameter value and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showBankInfoWithQueryParamValAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/bankInfo?ccy=XXX").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Adds bank account with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void addBankWithProperSession() throws Exception {
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(bankAcctService.addBankAccount(any()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.CREATED).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(post("/member/addBank").session(mockHttpSession)
                        .param("selectedCcy", "XXX")
                        .param("pageNum", "1")
                        .param("pageSize", "5")
                        .param("currency", "XXX")
                        .param("bankName", "Dummy Bank")
                        .param("bankAccountNumber", "1234"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Adds bank account with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void addBankWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(post("/member/addBank").session(mockHttpSession))
                // no need param
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Marks the bank account as obsolete with no query parameter and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void obsoleteBankWithNoQueryParamAndProperSession() throws Exception {
        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/obsoleteBank").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Marks the bank account as obsolete with no query parameter and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void obsoleteBankWithNoQueryParamAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/obsoleteBank").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Marks the bank account as obsolete with no query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void obsoleteBankWithNoQueryParamValAndProperSession() throws Exception {
        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/obsoleteBank?id=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Marks the bank account as obsolete with no query parameter value and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void obsoleteBankWithNoQueryParamValAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/obsoleteBank?id=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Marks the bank account as obsolete with query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void obsoleteBankWithQueryParamValAndProperSession() throws Exception {
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(bankAcctService.obsoleteBankAccount(any(), any(BigInteger.class)))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/obsoleteBank?id=1").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Marks the bank account as obsolete with query parameter value and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void obsoleteBankWithQueryParamValAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/obsoleteBank?id=1").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

}
