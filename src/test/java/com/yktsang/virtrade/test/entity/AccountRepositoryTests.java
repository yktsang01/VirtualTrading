/*
 * AccountRepositoryTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.entity;

import com.yktsang.virtrade.entity.Account;
import com.yktsang.virtrade.entity.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Provides the test cases for <code>AccountRepository</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class AccountRepositoryTests {

    /**
     * The mocked account repository.
     */
    @MockBean
    private AccountRepository acctRepo;

    /**
     * Initializes the mocked data.
     */
    @BeforeEach
    public void init() {
        Account dummyAccount =
                new Account("user@domain.com", "pwd");
        List<Account> dummyAccounts = new ArrayList<>();
        dummyAccounts.add(dummyAccount);
        Page<Account> dummyPage = new PageImpl<>(dummyAccounts);

        when(acctRepo.save(any(Account.class)))
                .thenReturn(dummyAccount);
        when(acctRepo.findById(anyString()))
                .thenReturn(Optional.of(dummyAccount));
        when(acctRepo.findMembersRequestedAdminAccess(anyBoolean()))
                .thenReturn(dummyAccounts);
        when(acctRepo.findMembersWithAdminAccess(anyBoolean(), anyString()))
                .thenReturn(dummyAccounts);
        when(acctRepo.findMembersRequestedAdminAccess(anyBoolean(), any(Pageable.class)))
                .thenReturn(dummyPage);
        when(acctRepo.findMembersWithAdminAccess(anyBoolean(), anyString(), any(Pageable.class)))
                .thenReturn(dummyPage);
    }

    /**
     * Tests saving account.
     */
    @Test
    public void save() {
        Account savedAccount = acctRepo.save(
                new Account("john@domain.com", "abcd1234"));
        assertEquals("user@domain.com", savedAccount.getEmail());
    }

    /**
     * Tests finding account by ID.
     */
    @Test
    public void findById() {
        Optional<Account> acctOpt = acctRepo.findById("john@domain.com");
        assertTrue(acctOpt.isPresent());
    }

    /**
     * Tests finding members requested admin access.
     */
    @Test
    public void findMembersRequestedAdminAccess() {
        assertEquals(1, acctRepo.findMembersRequestedAdminAccess(true).size());
    }

    /**
     * Tests finding members with admin access.
     */
    @Test
    public void findMembersWithAdminAccess() {
        assertEquals(1, acctRepo.findMembersWithAdminAccess(true, "admin@domain.com").size());
    }

    /**
     * Tests finding members requested admin access with pagination.
     */
    @Test
    public void findMembersRequestedAdminAccessPaginated() {
        assertEquals(1L, acctRepo.findMembersRequestedAdminAccess(true, PageRequest.of(1, 5)).getTotalElements());
    }

    /**
     * Tests finding members with admin access with pagination.
     */
    @Test
    public void findMembersWithAdminAccessPaginated() {
        assertEquals(1L, acctRepo.findMembersWithAdminAccess(true, "admin@domain.com", PageRequest.of(1, 5)).getTotalElements());
    }

}
