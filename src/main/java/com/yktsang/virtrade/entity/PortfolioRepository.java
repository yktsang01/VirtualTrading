/*
 * PortfolioRepository.java
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
 * Performs the database operations of the <code>Portfolio</code>.
 * Primary key to the database table "portfolio" is the portfolio ID.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Repository
public interface PortfolioRepository
        extends CrudRepository<Portfolio, BigInteger>, PagingAndSortingRepository<Portfolio, BigInteger> {

    /**
     * Returns the list of <code>Portfolio</code> using email address.
     *
     * @param email the email address
     * @return the list of Portfolio
     */
    @Query(value = "SELECT p FROM Portfolio p " +
            "WHERE p.email = :email " +
            "ORDER BY p.creationDateTime DESC")
    List<Portfolio> findByEmail(String email);

    /**
     * Returns the sublist of <code>Portfolio</code> using email address with pagination support.
     *
     * @param email    the email address
     * @param pageable the Pageable supporting pagination
     * @return the sublist of Portfolio
     */
    @Query(value = "SELECT p FROM Portfolio p " +
            "WHERE p.email = :email " +
            "ORDER BY p.creationDateTime DESC")
    Page<Portfolio> findByEmail(String email, Pageable pageable);

    /**
     * Returns the list of <code>Portfolio</code> using email address and currency.
     *
     * @param email    the email address
     * @param currency the currency
     * @return the list of Portfolio
     */
    @Query(value = "SELECT p FROM Portfolio p " +
            "WHERE p.email = :email and p.currency = :currency " +
            "ORDER BY p.creationDateTime DESC")
    List<Portfolio> findByEmailAndCurrency(String email, String currency);

    /**
     * Returns the sublist of <code>Portfolio</code> using email address and currency with pagination support.
     *
     * @param email    the email address
     * @param currency the currency
     * @param pageable the Pageable supporting pagination
     * @return the sublist of Portfolio
     */
    @Query(value = "SELECT p FROM Portfolio p " +
            "WHERE p.email = :email and p.currency = :currency " +
            "ORDER BY p.creationDateTime DESC")
    Page<Portfolio> findByEmailAndCurrency(String email, String currency, Pageable pageable);

}
