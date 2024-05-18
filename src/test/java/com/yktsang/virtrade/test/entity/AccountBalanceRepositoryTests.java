/*
 * AccountBalanceRepositoryTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.entity;

import com.yktsang.virtrade.entity.AccountBalance;
import com.yktsang.virtrade.entity.AccountBalancePK;
import com.yktsang.virtrade.entity.AccountBalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Provides the test cases for <code>AccountBalanceRepository</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class AccountBalanceRepositoryTests {

    /**
     * The mocked account balance repository.
     */
    @MockBean
    private AccountBalanceRepository acctBalRepo;

    /**
     * Initializes the mocked data.
     */
    @BeforeEach
    public void init() {
        AccountBalance dummyBalance =
                new AccountBalance("user@domain.com", "XXX", BigDecimal.ZERO, BigDecimal.ZERO);
        List<AccountBalance> dummyBalances = new ArrayList<>();
        dummyBalances.add(dummyBalance);
        Page<AccountBalance> dummyPage = new PageImpl<>(dummyBalances);

        when(acctBalRepo.save(any(AccountBalance.class)))
                .thenReturn(dummyBalance);
        when(acctBalRepo.findById(any(AccountBalancePK.class)))
                .thenReturn(Optional.of(dummyBalance));
        when(acctBalRepo.findByEmail(anyString()))
                .thenReturn(dummyBalances);
        when(acctBalRepo.findByEmail(anyString(), any(Pageable.class)))
                .thenReturn(dummyPage);
    }

    /**
     * Tests saving account balance.
     */
    @Test
    public void save() {
        AccountBalance savedBalance = acctBalRepo.save(
                new AccountBalance("john@domain.com", "ABC", BigDecimal.ZERO, BigDecimal.TEN));
        assertEquals("user@domain.com", savedBalance.getEmail());
    }

    /**
     * Tests finding account balance by ID.
     */
    @Test
    public void findById() {
        Optional<AccountBalance> balOpt = acctBalRepo.findById(
                new AccountBalancePK("john@domain.com", "PQR"));
        assertTrue(balOpt.isPresent());
    }

    /**
     * Tests finding account balances by email.
     */
    @Test
    public void findByEmail() {
        assertEquals(1, acctBalRepo.findByEmail("john@domain.com").size());
    }

    /**
     * Tests finding account balances by email with pagination.
     */
    @Test
    public void findByEmailPaginated() {
        assertEquals(1L, acctBalRepo.findByEmail("john@domain.com", PageRequest.of(1, 5)).getTotalElements());
    }

    /**
     * Tests delete account balance.
     */
    @Test
    public void delete() {
        AccountBalance balanceToDelete =
                new AccountBalance("user@domain.com", "XXX", BigDecimal.ZERO, BigDecimal.ZERO);

        acctBalRepo.delete(balanceToDelete);
        when(acctBalRepo.findById(any(AccountBalancePK.class)))
                .thenReturn(Optional.empty());

        Optional<AccountBalance> balOpt =
                acctBalRepo.findById(new AccountBalancePK("user@domain.com", "XXX"));
        assertFalse(balOpt.isPresent());
    }

    /**
     * Tests delete account balances.
     */
    @Test
    public void deleteAll() {
        AccountBalance balanceToDelete =
                new AccountBalance("user@domain.com", "XXX", BigDecimal.ZERO, BigDecimal.ZERO);
        List<AccountBalance> balancesToDelete = new ArrayList<>();
        balancesToDelete.add(balanceToDelete);

        acctBalRepo.deleteAll(balancesToDelete);
        when(acctBalRepo.findByEmail(anyString()))
                .thenReturn(new ArrayList<>());

        assertEquals(0, acctBalRepo.findByEmail("user@domain.com").size());
    }

}
