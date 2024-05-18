/*
 * JwtServiceTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.api.jwt;

import com.yktsang.virtrade.api.jwt.CustomUserDetails;
import com.yktsang.virtrade.api.jwt.JwtService;
import com.yktsang.virtrade.entity.Account;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Provides the test cases for <code>JwtService</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class JwtServiceTests {

    /**
     * The mocked JWT service.
     */
    @MockBean
    private JwtService jwtService;

    /**
     * Tests generating token and checking the subject.
     */
    @Test
    public void generateTokenThenCheckSubject() {
        String email = "user@domain.com";
        when(jwtService.generateToken(email))
                .thenReturn("usertoken");
        when(jwtService.extractUsername("usertoken"))
                .thenReturn(email);

        String token = jwtService.generateToken(email);
        String username = jwtService.extractUsername(token);
        assertEquals(email, username);
    }

    /**
     * Tests generating then validating the token.
     */
    @Test
    public void generateThenValidateToken() {
        String email = "user@domain.com";
        Account dummyAccount = new Account(email, "abcd1234");
        dummyAccount.setActive(true);
        when(jwtService.generateToken(email))
                .thenReturn("usertoken");
        when(jwtService.validateToken(anyString(), any(CustomUserDetails.class)))
                .thenReturn(true);

        String token = jwtService.generateToken(email);
        assertTrue(jwtService.validateToken(token, new CustomUserDetails(dummyAccount)));
    }

    /**
     * Compares JWT of different subjects.
     */
    @Test
    public void compareTokenOfDifferentSubjects() {
        when(jwtService.generateToken("john@domain.com"))
                .thenReturn("johntoken");
        when(jwtService.generateToken("jane@domain.com"))
                .thenReturn("janetoken");

        String johnToken = jwtService.generateToken("john@domain.com");
        String janeToken = jwtService.generateToken("jane@domain.com");
        assertNotEquals(johnToken, janeToken);
    }

}
