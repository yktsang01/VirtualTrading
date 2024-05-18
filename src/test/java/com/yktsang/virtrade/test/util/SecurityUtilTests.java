/*
 * SecurityUtilTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.util;

import com.yktsang.virtrade.util.SecurityUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Provides the test cases for <code>SecurityUtil</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class SecurityUtilTests {

    /**
     * Tests hashing password then checking it.
     */
    @Test
    public void hashThenCheckPassword() {
        String rawPassword = "abcd1234";
        String hashedPassword = SecurityUtil.hashPassword(rawPassword);
        assertTrue(SecurityUtil.checkPassword(rawPassword, hashedPassword));
    }

    /**
     * Tests validating a valid email address.
     */
    @Test
    public void validateValidEmail() {
        String emailAddress = "user@domain.com";
        assertFalse(SecurityUtil.isEmailNotValid(emailAddress));
    }

    /**
     * Tests validating an invalid email address.
     */
    @Test
    public void validateInvalidEmail() {
        String emailAddress = "user@domain@com";
        assertTrue(SecurityUtil.isEmailNotValid(emailAddress));
    }

}
