/*
 * BankAccountRepositoryTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.entity;

import com.yktsang.virtrade.entity.BankAccount;
import com.yktsang.virtrade.entity.BankAccountRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Provides the test cases for <code>BankAccountRepository</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class BankAccountRepositoryTests {

    /**
     * The mocked bank account repository.
     */
    @MockBean
    private BankAccountRepository bankAcctRepo;

    /**
     * Initializes the mocked data.
     */
    @BeforeEach
    public void init() {
        BankAccount dummyBankAccount =
                new BankAccount("user@domain.com", "XXX", "name", "number");
        List<BankAccount> dummyBankAccounts = new ArrayList<>();
        dummyBankAccounts.add(dummyBankAccount);
        Page<BankAccount> dummyPage = new PageImpl<>(dummyBankAccounts);

        when(bankAcctRepo.save(any(BankAccount.class)))
                .thenReturn(dummyBankAccount);
        when(bankAcctRepo.findById(any(BigInteger.class)))
                .thenReturn(Optional.of(dummyBankAccount));
        when(bankAcctRepo.findByEmail(anyString(), anyBoolean()))
                .thenReturn(dummyBankAccounts);
        when(bankAcctRepo.findByEmail(anyString()))
                .thenReturn(dummyBankAccounts);
        when(bankAcctRepo.findByEmailAndCurrency(anyString(), anyString(), anyBoolean()))
                .thenReturn(dummyBankAccounts);
        when(bankAcctRepo.findByEmailAndCurrency(anyString(), anyString()))
                .thenReturn(dummyBankAccounts);
        when(bankAcctRepo.findByEmail(anyString(), anyBoolean(), any(Pageable.class)))
                .thenReturn(dummyPage);
        when(bankAcctRepo.findByEmailAndCurrency(anyString(), anyString(), anyBoolean(), any(Pageable.class)))
                .thenReturn(dummyPage);
    }

    /**
     * Tests saving bank account.
     */
    @Test
    public void save() {
        BankAccount savedBankAccount = bankAcctRepo.save(
                new BankAccount("john@domain.com", "XXX", "name", "number"));
        assertEquals("user@domain.com", savedBankAccount.getEmail());
    }

    /**
     * Tests finding bank account by ID.
     */
    @Test
    public void findById() {
        Optional<BankAccount> bankAcctOpt = bankAcctRepo.findById(BigInteger.ONE);
        assertTrue(bankAcctOpt.isPresent());
    }

    /**
     * Tests finding active bank accounts by email.
     */
    @Test
    public void findActiveByEmail() {
        assertEquals(1, bankAcctRepo.findByEmail("john@domain.com", true).size());
    }

    /**
     * Tests finding active bank accounts by email with pagination.
     */
    @Test
    public void findActiveByEmailPaginated() {
        assertEquals(1L, bankAcctRepo.findByEmail("john@domain.com", true, PageRequest.of(1, 5)).getTotalElements());
    }

    /**
     * Tests finding bank accounts by email.
     */
    @Test
    public void findByEmail() {
        assertEquals(1, bankAcctRepo.findByEmail("john@domain.com").size());
    }

    /**
     * Tests finding active bank accounts by email and currency.
     */
    @Test
    public void findByActiveEmailAndCurrency() {
        assertEquals(1, bankAcctRepo.findByEmailAndCurrency("john@domain.com", "XXX", true).size());
    }

    /**
     * Tests finding active bank accounts by email and currency with pagination.
     */
    @Test
    public void findByActiveEmailAndCurrencyPaginated() {
        assertEquals(1L, bankAcctRepo.findByEmailAndCurrency("john@domain.com", "XXX", true, PageRequest.of(1, 5)).getTotalElements());
    }

    /**
     * Tests finding bank accounts by email and currency.
     */
    @Test
    public void findByEmailAndCurrency() {
        assertEquals(1, bankAcctRepo.findByEmailAndCurrency("john@domain.com", "XXX").size());
    }

    /**
     * Tests delete bank accounts.
     */
    @Test
    public void deleteAll() {
        BankAccount bankAccountToDelete =
                new BankAccount("user@domain.com", "XXX", "name", "number");
        List<BankAccount> bankAccountsToDelete = new ArrayList<>();
        bankAccountsToDelete.add(bankAccountToDelete);

        bankAcctRepo.deleteAll(bankAccountsToDelete);
        when(bankAcctRepo.findByEmail(anyString()))
                .thenReturn(new ArrayList<>());

        assertEquals(0, bankAcctRepo.findByEmail("user@domain.com").size());
    }

}
