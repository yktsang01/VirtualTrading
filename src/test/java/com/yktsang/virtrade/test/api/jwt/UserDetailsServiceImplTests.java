/*
 * UserDetailsServiceImplTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.api.jwt;

import com.yktsang.virtrade.api.jwt.CustomUserDetails;
import com.yktsang.virtrade.api.jwt.UserDetailsServiceImpl;
import com.yktsang.virtrade.entity.Account;
import com.yktsang.virtrade.entity.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Provides the test cases for <code>UserDetailsServiceImpl</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class UserDetailsServiceImplTests {

    /**
     * The mocked account repository.
     */
    @MockBean
    private AccountRepository accountRepo;
    /**
     * The mocked user details service implementation.
     */
    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    /**
     * Tests valid user.
     */
    @Test
    public void validUser() {
        Account dummyAccount = new Account("user@domain.com", "pwd");
        dummyAccount.setActive(true);
        when(accountRepo.findById(anyString()))
                .thenReturn(Optional.of(dummyAccount));
        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(new CustomUserDetails(dummyAccount));

        UserDetails user = userDetailsService.loadUserByUsername("john@domain.com");
        assertTrue(user.isEnabled());
    }

    /**
     * Tests invalid user.
     */
    @Test
    public void invalidUser() {
        when(accountRepo.findById(anyString()))
                .thenReturn(Optional.empty());
        when(userDetailsService.loadUserByUsername(anyString()))
                .thenThrow(new UsernameNotFoundException("user not found"));

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nobody@domain.com"));
    }

    /**
     * Tests authorities.
     */
    @Test
    public void authorities() {
        Account dummyAdminAccount = new Account("admin@domain.com", "pwd");
        dummyAdminAccount.setActive(true);
        dummyAdminAccount.setAdmin(true);
        when(accountRepo.findById(anyString()))
                .thenReturn(Optional.of(dummyAdminAccount));
        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(new CustomUserDetails(dummyAdminAccount));

        UserDetails admin = userDetailsService.loadUserByUsername("admin@domain.com");
        assertEquals(2, admin.getAuthorities().size());
    }

}
