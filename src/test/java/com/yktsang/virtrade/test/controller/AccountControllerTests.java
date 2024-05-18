/*
 * AccountControllerTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.controller;

import com.yktsang.virtrade.api.controller.AccountService;
import com.yktsang.virtrade.response.JwtResponse;
import com.yktsang.virtrade.response.SuccessResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static com.yktsang.virtrade.test.controller.MemberSessionHelper.getImproperMockSession;
import static com.yktsang.virtrade.test.controller.MemberSessionHelper.getProperMockSession;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Provides the test cases for <code>AccountController</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTests {

    /**
     * The mocked MVC.
     */
    @Autowired
    private MockMvc mvc;
    /**
     * The mocked account service.
     */
    @MockBean
    private AccountService acctService;

    /**
     * Shows the login page with no query parameter.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showLoginWithNoQueryParam() throws Exception {
        mvc.perform(get("/login"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the login page with no query parameter value.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showLoginWithNoQueryParamVal() throws Exception {
        mvc.perform(get("/login?from="))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the login page with query parameter value.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showLoginWithQueryParamVal() throws Exception {
        mvc.perform(get("/login?from=member"))
                .andExpect(status().isOk());
    }

    /**
     * Executes login.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void executeLogin() throws Exception {
        JwtResponse mockedResp = new JwtResponse("sometoken");
        when(acctService.login(any()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        mvc.perform(post("/login")
                        .param("referrer", "member")
                        .param("email", "user@domain.com")
                        .param("password", "abcd1234"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Executes member logout with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void executeMemberLogoutWithProperSession() throws Exception {
        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/logout").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Executes member logout with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void executeMemberLogoutWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/logout").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the reset password page with no query parameter.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showResetPasswordWithNoQueryParam() throws Exception {
        mvc.perform(get("/resetPassword"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the reset password page wth no query parameter value.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showResetPasswordWithNoQueryParamVal() throws Exception {
        mvc.perform(get("/resetPassword?from="))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the reset password page wth query parameter value.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showResetPasswordWithQueryParamVal() throws Exception {
        mvc.perform(get("/resetPassword?from=member"))
                .andExpect(status().isOk());
    }

    /**
     * Executes reset password.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void executeResetPassword() throws Exception {
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(acctService.resetPassword(any()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        mvc.perform(post("/resetPassword")
                        .param("referrer", "member")
                        .param("email", "user@domain.com")
                        .param("password1", "abcd1234")
                        .param("password2", "abcd1234"))
                .andExpect(status().is3xxRedirection());
    }

}
