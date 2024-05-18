/*
 * AccountTransactionRepositoryTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.entity;

import com.yktsang.virtrade.entity.AccountTransaction;
import com.yktsang.virtrade.entity.AccountTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Provides the test cases for <code>AccountTransactionRepository</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class AccountTransactionRepositoryTests {

    /**
     * The mocked account transaction repository.
     */
    @MockBean
    private AccountTransactionRepository acctTxnRepo;

    /**
     * Initializes the mocked data.
     */
    @BeforeEach
    public void init() {
        AccountTransaction dummyTransaction =
                new AccountTransaction("user@domain.com", "XXX", "desc");
        List<AccountTransaction> dummyTransactions = new ArrayList<>();
        dummyTransactions.add(dummyTransaction);
        Page<AccountTransaction> dummyPage = new PageImpl<>(dummyTransactions);

        when(acctTxnRepo.save(any(AccountTransaction.class)))
                .thenReturn(dummyTransaction);
        when(acctTxnRepo.findById(any(BigInteger.class)))
                .thenReturn(Optional.of(dummyTransaction));
        when(acctTxnRepo.findByEmail(anyString()))
                .thenReturn(dummyTransactions);
        when(acctTxnRepo.findByEmailAndCurrency(anyString(), anyString()))
                .thenReturn(dummyTransactions);
        when(acctTxnRepo.findByEmail(anyString(), any(Pageable.class)))
                .thenReturn(dummyPage);
        when(acctTxnRepo.findByEmailAndCurrency(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(dummyPage);
    }

    /**
     * Tests saving account transaction.
     */
    @Test
    public void save() {
        AccountTransaction savedTransaction = acctTxnRepo.save(
                new AccountTransaction("john@domain.com", "XXX", "desc"));
        assertEquals("user@domain.com", savedTransaction.getEmail());
    }

    /**
     * Tests finding account transaction by ID.
     */
    @Test
    public void findById() {
        Optional<AccountTransaction> transactionOpt = acctTxnRepo.findById(BigInteger.ONE);
        assertTrue(transactionOpt.isPresent());
    }

    /**
     * Tests finding account transactions by email.
     */
    @Test
    public void findByEmail() {
        assertEquals(1, acctTxnRepo.findByEmail("john@domain.com").size());
    }

    /**
     * Tests finding account transactions by email and currency.
     */
    @Test
    public void findByEmailAndCurrency() {
        assertEquals(1, acctTxnRepo.findByEmailAndCurrency("john@domain.com", "XXX").size());
    }

    /**
     * Tests finding account transactions by email with pagination.
     */
    @Test
    public void findByEmailPaginated() {
        assertEquals(1L, acctTxnRepo.findByEmail("john@domain.com", PageRequest.of(1, 5)).getTotalElements());
    }

    /**
     * Tests finding account transactions by email and currency with pagination.
     */
    @Test
    public void findByEmailAndCurrencyPaginated() {
        assertEquals(1L, acctTxnRepo.findByEmailAndCurrency("john@domain.com", "XXX", PageRequest.of(1, 5)).getTotalElements());
    }

    /**
     * Tests delete account transactions.
     */
    @Test
    public void deleteAll() {
        AccountTransaction transactionToDelete =
                new AccountTransaction("user@domain.com", "XXX", "desc");
        List<AccountTransaction> transactionsToDelete = new ArrayList<>();
        transactionsToDelete.add(transactionToDelete);

        acctTxnRepo.deleteAll(transactionsToDelete);
        when(acctTxnRepo.findByEmail(anyString()))
                .thenReturn(new ArrayList<>());

        assertEquals(0, acctTxnRepo.findByEmail("user@domain.com").size());
    }

}
