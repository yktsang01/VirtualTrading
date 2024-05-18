/*
 * AdminControllerTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.admin.controller;

import com.yktsang.virtrade.api.controller.AccountService;
import com.yktsang.virtrade.api.jwt.JwtService;
import com.yktsang.virtrade.response.AdminAccessResponse;
import com.yktsang.virtrade.response.AdminRequestResponse;
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

import java.util.ArrayList;
import java.util.Date;

import static com.yktsang.virtrade.test.admin.controller.AdminSessionHelper.getImproperMockSession;
import static com.yktsang.virtrade.test.admin.controller.AdminSessionHelper.getProperMockSession;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Provides the test cases for <code>AdminController</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTests {

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
     * The mocked account service.
     */
    @MockBean
    private AccountService accountService;

    /**
     * Shows the admin welcome page.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void adminIndex() throws Exception {
        when(accountService.checkAdminLastUpdated())
                .thenReturn(true);

        mvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Executes the admin logout with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void executeAdminLogoutWithProperSession() throws Exception {
        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/admin/logout").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Executes the admin logout with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void executeAdminLogoutWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/admin/logout").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the admin profile page with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showAdminProfileWithProperSession() throws Exception {
        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/admin/profile").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the admin profile page with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showAdminProfileWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/admin/profile").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the admin requests page with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showAdminRequestsWithProperSession() throws Exception {
        when(jwtService.generateToken(anyString()))
                .thenReturn("sometoken");
        when(jwtService.extractExpiration(anyString()))
                .thenReturn(new Date());
        AdminRequestResponse mockedResp = new AdminRequestResponse(new ArrayList<>());
        when(accountService.adminRequests(any(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/admin/adminRequests").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the admin requests page with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showAdminRequestsWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/admin/adminRequests").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Grants admin access with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void grantAdminAccessWithProperSession() throws Exception {
        when(jwtService.generateToken(anyString()))
                .thenReturn("sometoken");
        when(jwtService.extractExpiration(anyString()))
                .thenReturn(new Date());
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(accountService.grantAdminAccesses(any()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(post("/admin/adminRequests").session(mockHttpSession)
                        .param("grant", "user01@domain.com", "user02@domain.com"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Grants admin access with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void grantAdminAccessWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(post("/admin/adminRequests").session(mockHttpSession))
                // no need param
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the admin access page with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showAdminAccessWithProperSession() throws Exception {
        when(jwtService.generateToken(anyString()))
                .thenReturn("sometoken");
        when(jwtService.extractExpiration(anyString()))
                .thenReturn(new Date());
        AdminAccessResponse mockedResp = new AdminAccessResponse(new ArrayList<>());
        when(accountService.adminAccesses(any(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/admin/adminAccess").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the admin access page with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showAdminAccessWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/admin/adminAccess").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Revokes admin access with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void revokeAdminAccessWithProperSession() throws Exception {
        when(jwtService.generateToken(anyString()))
                .thenReturn("sometoken");
        when(jwtService.extractExpiration(anyString()))
                .thenReturn(new Date());
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(accountService.revokeAdminAccesses(any()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(post("/admin/adminAccess").session(mockHttpSession)
                        .param("revoke", "user01@domain.com", "user02@domain.com"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Revokes admin access with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void revokeAdminAccessWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(post("/admin/adminAccess").session(mockHttpSession))
                // no need param
                .andExpect(status().is3xxRedirection());
    }

}
