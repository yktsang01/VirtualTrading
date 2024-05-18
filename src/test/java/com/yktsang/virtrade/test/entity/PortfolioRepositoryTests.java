/*
 * PortfolioRepositoryTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.entity;

import com.yktsang.virtrade.entity.Portfolio;
import com.yktsang.virtrade.entity.PortfolioRepository;
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
 * Provides the test cases for <code>PortfolioRepository</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class PortfolioRepositoryTests {

    /**
     * The mocked portfolio repository.
     */
    @MockBean
    private PortfolioRepository portRepo;

    /**
     * Initializes the mocked data.
     */
    @BeforeEach
    public void init() {
        Portfolio dummyPortfolio = new Portfolio("user@domain.com", "name", "XXX");
        List<Portfolio> dummyPortfolios = new ArrayList<>();
        dummyPortfolios.add(dummyPortfolio);
        Page<Portfolio> dummyPage = new PageImpl<>(dummyPortfolios);

        when(portRepo.save(any(Portfolio.class)))
                .thenReturn(dummyPortfolio);
        when(portRepo.findById(any(BigInteger.class)))
                .thenReturn(Optional.of(dummyPortfolio));
        when(portRepo.findByEmail(anyString()))
                .thenReturn(dummyPortfolios);
        when(portRepo.findByEmailAndCurrency(anyString(), anyString()))
                .thenReturn(dummyPortfolios);
        when(portRepo.findByEmail(anyString(), any(Pageable.class)))
                .thenReturn(dummyPage);
        when(portRepo.findByEmailAndCurrency(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(dummyPage);
    }

    /**
     * Tests saving portfolio.
     */
    @Test
    public void save() {
        Portfolio savedPortfolio = portRepo.save(
                new Portfolio("john@domain.com", "name", "XXX"));
        assertEquals("user@domain.com", savedPortfolio.getEmail());
    }

    /**
     * Tests finding portfolio by ID.
     */
    @Test
    public void findById() {
        Optional<Portfolio> portfolioOpt = portRepo.findById(BigInteger.ONE);
        assertTrue(portfolioOpt.isPresent());
    }

    /**
     * Tests finding portfolios by email.
     */
    @Test
    public void findByEmail() {
        assertEquals(1, portRepo.findByEmail("john@domain.com").size());
    }

    /**
     * Tests finding portfolios by email and currency.
     */
    @Test
    public void findByEmailAndCurrency() {
        assertEquals(1, portRepo.findByEmailAndCurrency("john@domain.com", "XXX").size());
    }

    /**
     * Tests finding portfolios by email with pagination.
     */
    @Test
    public void findByEmailPaginated() {
        assertEquals(1L, portRepo.findByEmail("john@domain.com", PageRequest.of(1, 5)).getTotalElements());
    }

    /**
     * Tests finding portfolios by email and currency with pagination.
     */
    @Test
    public void findByEmailAndCurrencyPaginated() {
        assertEquals(1L, portRepo.findByEmailAndCurrency("john@domain.com", "XXX", PageRequest.of(1, 5)).getTotalElements());
    }

    /**
     * Tests delete portfolios.
     */
    @Test
    public void deleteAll() {
        Portfolio portfolioToDelete =
                new Portfolio("user@domain.com", "name", "XXX");
        List<Portfolio> portfoliosToDelete = new ArrayList<>();
        portfoliosToDelete.add(portfolioToDelete);

        portRepo.deleteAll(portfoliosToDelete);
        when(portRepo.findByEmail(anyString()))
                .thenReturn(new ArrayList<>());

        assertEquals(0, portRepo.findByEmail("user@domain.com").size());
    }

}
