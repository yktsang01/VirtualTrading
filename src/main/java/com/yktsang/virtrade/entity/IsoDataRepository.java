/*
 * IsoDataRepository.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.entity;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Performs the database operations of the <code>IsoData</code>.
 * Primary key to the database table "iso_data" is the country code.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Repository
public interface IsoDataRepository
        extends ListCrudRepository<IsoData, String>, ListPagingAndSortingRepository<IsoData, String> {

    /**
     * Returns the list of active <code>IsoData</code>.
     *
     * @param active the active indicator (always true)
     * @return the list of active IsoData
     */
    @Query(value = "SELECT id FROM IsoData id " +
            "WHERE id.active = :active " +
            "ORDER BY id.countryAlpha2Code")
    List<IsoData> findActiveIsoData(boolean active);

    /**
     * Get the number of decimal places to display for currency.
     *
     * @param currency the currency
     * @return the number of decimal places to display
     */
    @Query(value = "SELECT id.currencyMinorUnits FROM IsoData id " +
            "WHERE id.currencyAlphaCode = :currency")
    Integer findMinorUnits(String currency);

}
