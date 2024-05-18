/*
 * ProfileControllerTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.controller;

import com.yktsang.virtrade.api.controller.AccountService;
import com.yktsang.virtrade.api.controller.ProfileService;
import com.yktsang.virtrade.api.jwt.JwtService;
import com.yktsang.virtrade.entity.RiskToleranceLevel;
import com.yktsang.virtrade.response.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import static com.yktsang.virtrade.test.controller.MemberSessionHelper.getImproperMockSession;
import static com.yktsang.virtrade.test.controller.MemberSessionHelper.getProperMockSession;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Provides the test cases for <code>ProfileController</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ProfileControllerTests {

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
     * The mocked profile service.
     */
    @MockBean
    private ProfileService profileService;
    /**
     * The mocked account service.
     */
    @MockBean
    private AccountService acctService;

    /**
     * Shows the member registration page.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showMemberRegistration() throws Exception {
        mvc.perform(get("/register"))
                .andExpect(status().isOk());
    }

    /**
     * Executes member registration.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void executeMemberRegistration() throws Exception {
        JwtResponse mockedResp = new JwtResponse("sometoken");
        when(profileService.register(any()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        mvc.perform(post("/register")
                        .param("email", "user@domain.com")
                        .param("password1", "abcd1234")
                        .param("password2", "abcd1234")
                        .param("fullName", "Dummy user")
                        .param("dateOfBirthYear", "1980")
                        .param("dateOfBirthMonth", "10")
                        .param("dateOfBirthDay", "20")
                        .param("hideDateOfBirth", "Y")
                        .param("riskTolerance", "LOW")
                        .param("autoTransferToBank", "N")
                        .param("allowReset", "Y"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the public profile page with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showPublicProfileWithProperSession() throws Exception {
        when(jwtService.generateToken(anyString()))
                .thenReturn("sometoken");
        when(jwtService.extractExpiration(anyString()))
                .thenReturn(new Date());
        TraderProfile trader = new TraderProfile("user@domain.com", "name",
                LocalDate.now(), true, RiskToleranceLevel.MEDIUM,
                false, true, LocalDateTime.now());
        ProfileResponse mockedResp = new ProfileResponse(trader);
        when(profileService.publicProfile(any()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/publicProfile").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the public profile page with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showPublicProfileWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/publicProfile").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the member profile page with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showMemberProfileWithProperSession() throws Exception {
        when(jwtService.generateToken(anyString()))
                .thenReturn("sometoken");
        when(jwtService.extractExpiration(anyString()))
                .thenReturn(new Date());
        TraderProfile trader = new TraderProfile("user@domain.com", "name",
                LocalDate.now(), true, RiskToleranceLevel.MEDIUM,
                false, true, LocalDateTime.now());
        AccountProfile account = new AccountProfile("user@domain.com", false, false);
        CompleteProfileResponse mockedResp = new CompleteProfileResponse(trader, account);
        when(profileService.privateProfile(any()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/profile").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the member profile page with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showMemberProfileWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/profile").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Updates member profile with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void updateMemberProfileWithProperSession() throws Exception {
        when(jwtService.generateToken(anyString()))
                .thenReturn("sometoken");
        when(jwtService.extractExpiration(anyString()))
                .thenReturn(new Date());
        TraderProfile trader = new TraderProfile("user@domain.com", "name",
                LocalDate.now(), true, RiskToleranceLevel.MEDIUM,
                false, true, LocalDateTime.now());
        ProfileResponse mockedResp = new ProfileResponse(trader);
        when(profileService.updateProfile(any()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(post("/member/updateProfile").session(mockHttpSession)
                        .param("email", "user@domain.com")
                        .param("fullName", "Dummy user")
                        .param("dateOfBirthYear", "1980")
                        .param("dateOfBirthMonth", "10")
                        .param("dateOfBirthDay", "20")
                        .param("hideDateOfBirth", "Y")
                        .param("riskTolerance", "LOW")
                        .param("autoTransferToBank", "N")
                        .param("allowReset", "Y"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Updates member profile with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void updateMemberProfileWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(post("/member/updateProfile").session(mockHttpSession))
                // no need param
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Updates member account with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void updateMemberAccountWithProperSession() throws Exception {
        when(jwtService.generateToken(anyString()))
                .thenReturn("sometoken");
        when(jwtService.extractExpiration(anyString()))
                .thenReturn(new Date());
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(acctService.updateAccount(any()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(post("/member/updateAccount").session(mockHttpSession)
                        .param("email", "user@domain.com")
                        .param("password1", "abcd1234")
                        .param("password2", "abcd1234"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Updates member account with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void updateMemberAccountWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(post("/member/updateAccount").session(mockHttpSession))
                // no need param
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Requests admin access with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void requestAdminWithProperSession() throws Exception {
        when(jwtService.generateToken(anyString()))
                .thenReturn("sometoken");
        when(jwtService.extractExpiration(anyString()))
                .thenReturn(new Date());
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(acctService.requestAdminAccess(any()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(post("/member/requestAdmin").session(mockHttpSession)
                        .param("admin", "Y"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Requests admin access with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void requestAdminWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(post("/member/requestAdmin").session(mockHttpSession))
                // no need param
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Deactivates profile with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void deactivateProfileWithProperSession() throws Exception {
        when(jwtService.generateToken(anyString()))
                .thenReturn("sometoken");
        when(jwtService.extractExpiration(anyString()))
                .thenReturn(new Date());
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(profileService.deactivateProfile(any()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(post("/member/deactivateProfile").session(mockHttpSession)
                        .param("deactivate", "Y")
                        .param("reason", "blabla"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Deactivates profile with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void deactivateProfileWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(post("/member/deactivateProfile").session(mockHttpSession))
                // no need param
                .andExpect(status().is3xxRedirection());
    }

}
