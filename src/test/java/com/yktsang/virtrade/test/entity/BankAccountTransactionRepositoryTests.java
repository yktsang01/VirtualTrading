/*
 * BankAccountTransactionRepositoryTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.entity;

import com.yktsang.virtrade.entity.BankAccountTransaction;
import com.yktsang.virtrade.entity.BankAccountTransactionRepository;
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
 * Provides the test cases for <code>BankAccountTransactionRepository</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class BankAccountTransactionRepositoryTests {

    /**
     * The mocked bank account transaction repository.
     */
    @MockBean
    private BankAccountTransactionRepository bankAcctTxnRepo;

    /**
     * Initializes the mocked data.
     */
    @BeforeEach
    public void init() {
        BankAccountTransaction dummyTransaction =
                new BankAccountTransaction("user@domain.com", "XXX", "desc");
        List<BankAccountTransaction> dummyTransactions = new ArrayList<>();
        dummyTransactions.add(dummyTransaction);
        Page<BankAccountTransaction> dummyPage = new PageImpl<>(dummyTransactions);

        when(bankAcctTxnRepo.save(any(BankAccountTransaction.class)))
                .thenReturn(dummyTransaction);
        when(bankAcctTxnRepo.findById(any(BigInteger.class)))
                .thenReturn(Optional.of(dummyTransaction));
        when(bankAcctTxnRepo.findByEmail(anyString()))
                .thenReturn(dummyTransactions);
        when(bankAcctTxnRepo.findByEmailAndCurrency(anyString(), anyString()))
                .thenReturn(dummyTransactions);
        when(bankAcctTxnRepo.findByEmail(anyString(), any(Pageable.class)))
                .thenReturn(dummyPage);
        when(bankAcctTxnRepo.findByEmailAndCurrency(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(dummyPage);
    }

    /**
     * Tests saving bank account transaction.
     */
    @Test
    public void save() {
        BankAccountTransaction savedTransaction = bankAcctTxnRepo.save(
                new BankAccountTransaction("john@domain.com", "XXX", "desc"));
        assertEquals("user@domain.com", savedTransaction.getEmail());
    }

    /**
     * Tests finding bank account transaction by ID.
     */
    @Test
    public void findById() {
        Optional<BankAccountTransaction> transactionOpt = bankAcctTxnRepo.findById(BigInteger.ONE);
        assertTrue(transactionOpt.isPresent());
    }

    /**
     * Tests finding bank account transactions by email.
     */
    @Test
    public void findByEmail() {
        assertEquals(1, bankAcctTxnRepo.findByEmail("john@domain.com").size());
    }

    /**
     * Tests finding bank account transactions by email and currency.
     */
    @Test
    public void findByEmailAndCurrency() {
        assertEquals(1, bankAcctTxnRepo.findByEmailAndCurrency("john@domain.com", "XXX").size());
    }

    /**
     * Tests finding account transactions by email with pagination.
     */
    @Test
    public void findByEmailPaginated() {
        assertEquals(1L, bankAcctTxnRepo.findByEmail("john@domain.com", PageRequest.of(1, 5)).getTotalElements());
    }

    /**
     * Tests finding account transactions by email and currency with pagination.
     */
    @Test
    public void findByEmailAndCurrencyPaginated() {
        assertEquals(1L, bankAcctTxnRepo.findByEmailAndCurrency("john@domain.com", "XXX", PageRequest.of(1, 5)).getTotalElements());
    }

    /**
     * Tests delete bank account transactions.
     */
    @Test
    public void deleteAll() {
        BankAccountTransaction transactionToDelete =
                new BankAccountTransaction("user@domain.com", "XXX", "desc");
        List<BankAccountTransaction> transactionsToDelete = new ArrayList<>();
        transactionsToDelete.add(transactionToDelete);

        bankAcctTxnRepo.deleteAll(transactionsToDelete);
        when(bankAcctTxnRepo.findByEmail(anyString()))
                .thenReturn(new ArrayList<>());

        assertEquals(0, bankAcctTxnRepo.findByEmail("user@domain.com").size());
    }

}
