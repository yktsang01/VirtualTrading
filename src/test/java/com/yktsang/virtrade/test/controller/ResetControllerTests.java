/*
 * ResetControllerTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.controller;

import com.yktsang.virtrade.api.controller.IsoDataService;
import com.yktsang.virtrade.api.controller.ResetService;
import com.yktsang.virtrade.api.jwt.JwtService;
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

import java.util.Date;
import java.util.HashSet;

import static com.yktsang.virtrade.test.controller.MemberSessionHelper.getImproperMockSession;
import static com.yktsang.virtrade.test.controller.MemberSessionHelper.getProperMockSession;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Provides the test cases for <code>ResetController</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ResetControllerTests {

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
     * The mocked reset service.
     */
    @MockBean
    private ResetService resetService;

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
     * Shows the reset page with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showResetWithProperSession() throws Exception {
        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/reset").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the reset page with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showResetWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/reset").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Resets portfolio with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void resetWithProperSession() throws Exception {
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(resetService.resetPortfolio(any()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(post("/member/reset").session(mockHttpSession)
                        .param("resetCurrency", "XXX"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Resets portfolio with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void resetWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(post("/member/reset").session(mockHttpSession))
                // no need param
                .andExpect(status().is3xxRedirection());
    }

}
