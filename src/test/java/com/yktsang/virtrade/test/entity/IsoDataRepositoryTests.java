/*
 * IsoDataRepositoryTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.entity;

import com.yktsang.virtrade.entity.IsoData;
import com.yktsang.virtrade.entity.IsoDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Provides the test cases for <code>IsoDataRepository</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class IsoDataRepositoryTests {

    /**
     * The mocked ISO data repository.
     */
    @MockBean
    private IsoDataRepository isoDataRepo;

    /**
     * Initializes the mocked data.
     */
    @BeforeEach
    public void init() {
        IsoData dummyIsoCode = new IsoData("XX", "name",
                "XXX", "name", null,
                true, "admin@domain.com");
        List<IsoData> dummyIsoCodes = new ArrayList<>();
        dummyIsoCodes.add(dummyIsoCode);
        Page<IsoData> dummyPage = new PageImpl<>(dummyIsoCodes);

        when(isoDataRepo.save(any(IsoData.class)))
                .thenReturn(dummyIsoCode);
        when(isoDataRepo.findById(anyString()))
                .thenReturn(Optional.of(dummyIsoCode));
        when(isoDataRepo.findAll())
                .thenReturn(dummyIsoCodes);
        when(isoDataRepo.findAll(any(Sort.class)))
                .thenReturn(dummyIsoCodes);
        when(isoDataRepo.findAll(any(Pageable.class)))
                .thenReturn(dummyPage);
        when(isoDataRepo.findActiveIsoData(anyBoolean()))
                .thenReturn(dummyIsoCodes);
        when(isoDataRepo.findMinorUnits(anyString()))
                .thenReturn(2);
    }

    /**
     * Tests saving ISO data.
     */
    @Test
    public void save() {
        IsoData savedIsoCode = isoDataRepo.save(
                new IsoData("XX", "name",
                        "XXX", "name", null,
                        true, "admin@domain.com"));
        assertEquals("XX", savedIsoCode.getCountryAlpha2Code());
    }

    /**
     * Tests finding ISO data by ID.
     */
    @Test
    public void findById() {
        Optional<IsoData> isoCodeOpt = isoDataRepo.findById("XX");
        assertTrue(isoCodeOpt.isPresent());
    }

    /**
     * Tests finding all ISO data.
     */
    @Test
    public void findAll() {
        assertEquals(1, isoDataRepo.findAll().size());
    }

    /**
     * Tests finding all ISO data sorted.
     */
    @Test
    public void findAllSorted() {
        assertEquals(1, isoDataRepo.findAll(Sort.by(Sort.Direction.ASC, "countryAlpha2Code")).size());
    }

    /**
     * Tests finding all ISO data with pagination.
     */
    @Test
    public void findAllPaginated() {
        assertEquals(1L, isoDataRepo.findAll(PageRequest.of(1, 5)).getTotalElements());
    }

    /**
     * Tests finding active ISO data.
     */
    @Test
    public void findActiveIsoData() {
        assertEquals(1, isoDataRepo.findActiveIsoData(true).size());
    }

    /**
     * Tests finding decimal places of ISO data.
     */
    @Test
    public void findMinorUnits() {
        assertEquals(2, isoDataRepo.findMinorUnits("XXX"));
    }

}
