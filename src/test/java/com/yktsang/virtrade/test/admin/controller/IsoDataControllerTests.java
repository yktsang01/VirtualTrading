/*
 * IsoDataControllerTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.admin.controller;

import com.yktsang.virtrade.api.controller.IsoDataService;
import com.yktsang.virtrade.api.jwt.JwtService;
import com.yktsang.virtrade.response.IsoCode;
import com.yktsang.virtrade.response.IsoCodeResponse;
import com.yktsang.virtrade.response.IsoDataResponse;
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

import java.time.LocalDateTime;
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
 * Provides the test cases for <code>IsoDataController</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
public class IsoDataControllerTests {

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
     * Shows the ISO data page with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showIsoDataWithProperSession() throws Exception {
        IsoDataResponse mockedResp = new IsoDataResponse(new ArrayList<>());
        when(isoDataService.isoData(any(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/admin/isoData").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the ISO data page with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showIsoDataWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/admin/isoData").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the ISO data creation page with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showIsoDataCreationWithProperSession() throws Exception {
        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/admin/isoCreation").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the ISO data creation page with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showIsoDataCreationWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/admin/isoCreation").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Creates ISO data with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void createIsoDataWithProperSession() throws Exception {
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(isoDataService.createIsoData(any()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.CREATED).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(post("/admin/createIsoData").session(mockHttpSession)
                        .param("countryCode", "XX")
                        .param("countryName", "Whatever")
                        .param("currencyCode", "XXX")
                        .param("currencyName", "Whatever")
                        .param("currencyMinorUnits", "UNKNOWN")
                        .param("activate", "N"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Creates ISO data with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void createIsoDataWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(post("/admin/createIsoData").session(mockHttpSession))
                // no need param
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the ISO data update page with no query parameter and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showIsoDataUpdateWithNoQueryParamAndProperSession() throws Exception {
        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/admin/isoUpdate").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the ISO data update page with no query parameter and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showIsoDataUpdateWithNoQueryParamAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/admin/isoUpdate").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the ISO data update page with no query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showIsoDataUpdateWithNoQueryParamValAndProperSession() throws Exception {
        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/admin/isoUpdate?code=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the ISO data update page with no query parameter value and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showIsoDataUpdateWithNoQueryParamValAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/admin/isoUpdate?code=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the ISO data update page with query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showIsoDataUpdateWithQueryParamValAndProperSession() throws Exception {
        IsoCodeResponse mockedResp = new IsoCodeResponse(
                new IsoCode("XX", "wherever",
                        "XXX", "whatever", null,
                        true, LocalDateTime.now(), "whoever",
                        null, null));
        when(isoDataService.isoData(any(), anyString()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/admin/isoUpdate?code=XX").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the ISO data update page with query parameter value and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showIsoDataUpdateWithQueryParamValAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/admin/isoUpdate?code=XX").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Updates ISO data with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void updateIsoDataWithProperSession() throws Exception {
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(isoDataService.updateIsoData(any()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(post("/admin/updateIsoData").session(mockHttpSession)
                        .param("hiddenCountryCode", "XX")
                        .param("countryName", "Whatever")
                        .param("currencyCode", "XXX")
                        .param("currencyName", "Whatever")
                        .param("currencyMinorUnits", "UNKNOWN")
                        .param("deactivate", "Y"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Updates ISO data with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void updateIsoDataWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(post("/admin/updateIsoData").session(mockHttpSession))
                // no need param
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Activates ISO data with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void activateIsoDataWithProperSession() throws Exception {
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(isoDataService.activateIsoData(any()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(post("/admin/activateIsoData").session(mockHttpSession)
                        .param("activate", "XX"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Activates ISO data with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void activateIsoDataWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(post("/admin/activateIsoData").session(mockHttpSession))
                // no need param
                .andExpect(status().is3xxRedirection());
    }

}
