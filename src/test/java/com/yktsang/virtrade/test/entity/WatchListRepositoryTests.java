/*
 * WatchListRepositoryTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.entity;

import com.yktsang.virtrade.entity.WatchList;
import com.yktsang.virtrade.entity.WatchListRepository;
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
 * Provides the test cases for <code>WatchListRepository</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class WatchListRepositoryTests {

    /**
     * The mocked watch list repository.
     */
    @MockBean
    private WatchListRepository watchListRepo;

    /**
     * Initializes the mocked data.
     */
    @BeforeEach
    public void init() {
        WatchList dummyWatchList =
                new WatchList("user@domain.com", "SYM", "name", "XXX");
        List<WatchList> dummyWatchLists = new ArrayList<>();
        dummyWatchLists.add(dummyWatchList);
        Page<WatchList> dummyPage = new PageImpl<>(dummyWatchLists);

        when(watchListRepo.save(any(WatchList.class)))
                .thenReturn(dummyWatchList);
        when(watchListRepo.findById(any(BigInteger.class)))
                .thenReturn(Optional.of(dummyWatchList));
        when(watchListRepo.findActiveByEmail(anyString()))
                .thenReturn(dummyWatchLists);
        when(watchListRepo.findActiveByEmailAndCurrency(anyString(), anyString()))
                .thenReturn(dummyWatchLists);
        when(watchListRepo.findByEmail(anyString()))
                .thenReturn(dummyWatchLists);
        when(watchListRepo.findByEmailAndCurrency(anyString(), anyString()))
                .thenReturn(dummyWatchLists);
        when(watchListRepo.findActiveByEmail(anyString(), any(Pageable.class)))
                .thenReturn(dummyPage);
        when(watchListRepo.findActiveByEmailAndCurrency(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(dummyPage);
    }

    /**
     * Tests saving watch list.
     */
    @Test
    public void save() {
        WatchList savedWatchList = watchListRepo.save(
                new WatchList("john@domain.com", "SYM", "name", "XXX"));
        assertEquals("user@domain.com", savedWatchList.getEmail());
    }

    /**
     * Tests finding watch list by ID.
     */
    @Test
    public void findById() {
        Optional<WatchList> watchListOpt = watchListRepo.findById(BigInteger.ONE);
        assertTrue(watchListOpt.isPresent());
    }

    /**
     * Tests finding active watch lists by email.
     */
    @Test
    public void findActiveByEmail() {
        assertEquals(1, watchListRepo.findActiveByEmail("john@domain.com").size());
    }

    /**
     * Tests finding active watch lists by email and currency.
     */
    @Test
    public void findActiveByEmailAndCurrency() {
        assertEquals(1, watchListRepo.findActiveByEmailAndCurrency("john@domain.com", "XXX").size());
    }

    /**
     * Tests finding active watch lists by email with pagination.
     */
    @Test
    public void findActiveByEmailPaginated() {
        assertEquals(1L, watchListRepo.findActiveByEmail("john@domain.com", PageRequest.of(1, 5)).getTotalElements());
    }

    /**
     * Tests finding active watch lists by email and currency with pagination.
     */
    @Test
    public void findActiveByEmailAndCurrencyPaginated() {
        assertEquals(1L, watchListRepo.findActiveByEmailAndCurrency("john@domain.com", "XXX", PageRequest.of(1, 5)).getTotalElements());
    }

    /**
     * Tests finding watch lists by email.
     */
    @Test
    public void findByEmail() {
        assertEquals(1, watchListRepo.findByEmail("john@domain.com").size());
    }

    /**
     * Tests finding watch lists by email and currency.
     */
    @Test
    public void findByEmailAndCurrency() {
        assertEquals(1, watchListRepo.findByEmailAndCurrency("john@domain.com", "XXX").size());
    }

    /**
     * Tests delete watch lists.
     */
    @Test
    public void deleteAll() {
        WatchList watchListToDelete =
                new WatchList("user@domain.com", "SYM", "name", "XXX");
        List<WatchList> watchListsToDelete = new ArrayList<>();
        watchListsToDelete.add(watchListToDelete);

        watchListRepo.deleteAll(watchListsToDelete);
        when(watchListRepo.findByEmail(anyString()))
                .thenReturn(new ArrayList<>());

        assertEquals(0, watchListRepo.findByEmail("user@domain.com").size());
    }

}
