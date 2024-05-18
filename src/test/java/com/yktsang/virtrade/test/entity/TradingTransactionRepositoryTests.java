/*
 * TradingTransactionRepositoryTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.entity;

import com.yktsang.virtrade.entity.TradingDeed;
import com.yktsang.virtrade.entity.TradingTransaction;
import com.yktsang.virtrade.entity.TradingTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Provides the test cases for <code>TradingTransactionRepository</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class TradingTransactionRepositoryTests {

    /**
     * The mocked trading transaction repository.
     */
    @MockBean
    private TradingTransactionRepository tradingTxnRepo;

    /**
     * Initializes the mocked data.
     */
    @BeforeEach
    public void init() {
        TradingTransaction dummyTransaction = new TradingTransaction("user@domain.com",
                "SYM", "name", LocalDate.now(), TradingDeed.BUY,
                10, "XXX", BigDecimal.ONE, BigDecimal.ONE);
        List<TradingTransaction> dummyTransactions = new ArrayList<>();
        dummyTransactions.add(dummyTransaction);
        Page<TradingTransaction> dummyPage = new PageImpl<>(dummyTransactions);

        when(tradingTxnRepo.save(any(TradingTransaction.class)))
                .thenReturn(dummyTransaction);
        when(tradingTxnRepo.findById(any(BigInteger.class)))
                .thenReturn(Optional.of(dummyTransaction));
        when(tradingTxnRepo.findByEmail(anyString()))
                .thenReturn(dummyTransactions);
        when(tradingTxnRepo.findByEmailAndCurrency(anyString(), anyString()))
                .thenReturn(dummyTransactions);
        when(tradingTxnRepo.findByPortfolioId(anyString(), any(BigInteger.class)))
                .thenReturn(dummyTransactions);
        when(tradingTxnRepo.findByEmail(anyString(), any(Pageable.class)))
                .thenReturn(dummyPage);
        when(tradingTxnRepo.findByEmailAndCurrency(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(dummyPage);
        when(tradingTxnRepo.findByPortfolioId(anyString(), any(BigInteger.class), any(Pageable.class)))
                .thenReturn(dummyPage);
        when(tradingTxnRepo.findByTradingSymbol(anyString(), any(TradingDeed.class), anyString()))
                .thenReturn(dummyTransactions);
    }

    /**
     * Tests saving trading transaction.
     */
    @Test
    public void save() {
        TradingTransaction savedTransaction = tradingTxnRepo.save(
                new TradingTransaction("john@domain.com",
                        "SYM", "name", LocalDate.now(), TradingDeed.BUY,
                        10, "XXX", BigDecimal.ONE, BigDecimal.ONE));
        assertEquals("user@domain.com", savedTransaction.getEmail());
    }

    /**
     * Tests finding trading transaction by ID.
     */
    @Test
    public void findById() {
        Optional<TradingTransaction> transactionOpt = tradingTxnRepo.findById(BigInteger.ONE);
        assertTrue(transactionOpt.isPresent());
    }

    /**
     * Tests finding trading transactions by email.
     */
    @Test
    public void findByEmail() {
        assertEquals(1, tradingTxnRepo.findByEmail("john@domain.com").size());
    }

    /**
     * Tests finding trading transactions by email and currency.
     */
    @Test
    public void findByEmailAndCurrency() {
        assertEquals(1, tradingTxnRepo.findByEmailAndCurrency("john@domain.com", "XXX").size());
    }

    /**
     * Tests finding trading transactions by portfolio ID.
     */
    @Test
    public void findByPortfolioId() {
        assertEquals(1, tradingTxnRepo.findByPortfolioId("john@domain.com", BigInteger.ONE).size());
    }

    /**
     * Tests finding trading transactions by email with pagination.
     */
    @Test
    public void findByEmailPaginated() {
        assertEquals(1L, tradingTxnRepo.findByEmail("john@domain.com", PageRequest.of(1, 5)).getTotalElements());
    }

    /**
     * Tests finding trading transactions by email and currency with pagination.
     */
    @Test
    public void findByEmailAndCurrencyPaginated() {
        assertEquals(1L, tradingTxnRepo.findByEmailAndCurrency("john@domain.com", "XXX", PageRequest.of(1, 5)).getTotalElements());
    }

    /**
     * Tests finding trading transactions by portfolio ID with pagination.
     */
    @Test
    public void findByPortfolioIdPaginated() {
        assertEquals(1L, tradingTxnRepo.findByPortfolioId("john@domain.com", BigInteger.ONE, PageRequest.of(1, 5)).getTotalElements());
    }

    /**
     * Tests finding trading transactions by trading symbol.
     */
    @Test
    public void findByTradingSymbol() {
        assertEquals(1, tradingTxnRepo.findByTradingSymbol("john@domain.com", TradingDeed.BUY, "XXX").size());
    }

    /**
     * Tests delete trading transactions.
     */
    @Test
    public void deleteAll() {
        TradingTransaction transactionToDelete =
                new TradingTransaction("user@domain.com",
                        "SYM", "name", LocalDate.now(), TradingDeed.BUY,
                        10, "XXX", BigDecimal.ONE, BigDecimal.ONE);
        List<TradingTransaction> transactionsToDelete = new ArrayList<>();
        transactionsToDelete.add(transactionToDelete);

        tradingTxnRepo.deleteAll(transactionsToDelete);
        when(tradingTxnRepo.findByEmail(anyString()))
                .thenReturn(new ArrayList<>());

        assertEquals(0, tradingTxnRepo.findByEmail("user@domain.com").size());
    }

}
