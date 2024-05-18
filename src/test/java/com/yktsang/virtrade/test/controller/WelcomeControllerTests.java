/*
 * WelcomeControllerTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static com.yktsang.virtrade.test.controller.MemberSessionHelper.getImproperMockSession;
import static com.yktsang.virtrade.test.controller.MemberSessionHelper.getProperMockSession;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Provides the test cases for <code>WelcomeController</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
public class WelcomeControllerTests {

    /**
     * The mocked MVC.
     */
    @Autowired
    private MockMvc mvc;

    /**
     * Shows the main welcome page with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void mainIndexWithProperSession() throws Exception {
        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the main welcome page with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void mainIndexWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/").session(mockHttpSession))
                .andExpect(status().isOk());
    }

}
