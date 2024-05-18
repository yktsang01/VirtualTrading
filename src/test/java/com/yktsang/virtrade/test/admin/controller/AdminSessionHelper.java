/*
 * AdminSessionHelper.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.admin.controller;

import org.springframework.mock.web.MockHttpSession;

/**
 * Provides the HTTP session for testing purposes.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public class AdminSessionHelper {

    /**
     * Returns the proper mock HTTP session.
     *
     * @return the proper mock HTTP session
     */
    public static MockHttpSession getProperMockSession() {
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setMaxInactiveInterval(1800); // 1800 sec (30 min)
        mockHttpSession.setAttribute("email", "admin@domain.com");
        mockHttpSession.setAttribute("jwt", "sometoken");
        mockHttpSession.setAttribute("admin", true);
        return mockHttpSession;
    }

    /**
     * Returns the improper mock HTTP session.
     *
     * @return the improper mock HTTP session
     */
    public static MockHttpSession getImproperMockSession() {
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setMaxInactiveInterval(1800); // 1800 sec (30 min)
        mockHttpSession.setAttribute("email", "admin@domain.com");
        mockHttpSession.setAttribute("jwt", "sometoken");
        // no admin attribute in session
        return mockHttpSession;
    }

}
