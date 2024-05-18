/*
 * WatchListRepository.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * Performs the database operations of the <code>WatchList</code>.
 * Primary key to the database table "watch_list" is the account watch list ID.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Repository
public interface WatchListRepository
        extends CrudRepository<WatchList, BigInteger>, PagingAndSortingRepository<WatchList, BigInteger> {

    /**
     * Returns the list of active <code>WatchList</code> using email address.
     *
     * @param email the email address
     * @return the list of WatchList
     */
    @Query(value = "SELECT wl FROM WatchList wl " +
            "WHERE wl.email = :email AND wl.removalDateTime is null " +
            "ORDER BY wl.symbol")
    List<WatchList> findActiveByEmail(String email);

    /**
     * Returns the sublist of active <code>WatchList</code> using email address with pagination support.
     *
     * @param email    the email address
     * @param pageable the Pageable supporting pagination
     * @return the sublist of WatchList
     */
    @Query(value = "SELECT wl FROM WatchList wl " +
            "WHERE wl.email = :email AND wl.removalDateTime is null " +
            "ORDER BY wl.symbol")
    Page<WatchList> findActiveByEmail(String email, Pageable pageable);

    /**
     * Returns the list of active <code>WatchList</code> using email address and currency.
     *
     * @param email    the email address
     * @param currency the currency
     * @return the list of WatchList
     */
    @Query(value = "SELECT wl FROM WatchList wl " +
            "WHERE wl.email = :email AND wl.currency = :currency AND wl.removalDateTime is null " +
            "ORDER BY wl.symbol")
    List<WatchList> findActiveByEmailAndCurrency(String email, String currency);

    /**
     * Returns the sublist of active <code>WatchList</code> using email address and currency with pagination support.
     *
     * @param email    the email address
     * @param currency the currency
     * @param pageable the Pageable supporting pagination
     * @return the sublist of WatchList
     */
    @Query(value = "SELECT wl FROM WatchList wl " +
            "WHERE wl.email = :email AND wl.currency = :currency AND wl.removalDateTime is null " +
            "ORDER BY wl.symbol")
    Page<WatchList> findActiveByEmailAndCurrency(String email, String currency, Pageable pageable);

    /**
     * Returns the list of <code>WatchList</code> using email address.
     *
     * @param email the email address
     * @return the list of WatchList
     */
    @Query(value = "SELECT wl FROM WatchList wl " +
            "WHERE wl.email = :email " +
            "ORDER BY wl.symbol")
    List<WatchList> findByEmail(String email);

    /**
     * Returns the list of <code>WatchList</code> using email address and currency.
     *
     * @param email    the email address
     * @param currency the currency
     * @return the list of WatchList
     */
    @Query(value = "SELECT wl FROM WatchList wl " +
            "WHERE wl.email = :email AND wl.currency = :currency " +
            "ORDER BY wl.symbol")
    List<WatchList> findByEmailAndCurrency(String email, String currency);

}
