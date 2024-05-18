/*
 * TraderRepositoryTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.entity;

import com.yktsang.virtrade.entity.RiskToleranceLevel;
import com.yktsang.virtrade.entity.Trader;
import com.yktsang.virtrade.entity.TraderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Provides the test cases for <code>TraderRepository</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class TraderRepositoryTests {

    /**
     * The mocked trader repository.
     */
    @MockBean
    private TraderRepository traderRepo;

    /**
     * Initializes the mocked data.
     */
    @BeforeEach
    public void init() {
        Trader dummyTrader = new Trader("user@domain.com", "User", LocalDate.now(),
                true, RiskToleranceLevel.MEDIUM, false, true);

        when(traderRepo.save(any(Trader.class)))
                .thenReturn(dummyTrader);
        when(traderRepo.findById(anyString()))
                .thenReturn(Optional.of(dummyTrader));
    }

    /**
     * Tests saving trader.
     */
    @Test
    public void save() {
        Trader savedTrader = traderRepo.save(
                new Trader("john@domain.com", "John", LocalDate.now(),
                        true, RiskToleranceLevel.MEDIUM, false, true));
        assertEquals("user@domain.com", savedTrader.getEmail());
    }

    /**
     * Tests finding trader by ID.
     */
    @Test
    public void findById() {
        Optional<Trader> traderOpt = traderRepo.findById("john@domain.com");
        assertTrue(traderOpt.isPresent());
    }

}
