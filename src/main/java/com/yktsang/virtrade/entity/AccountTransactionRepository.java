/*
 * AccountTransactionRepository.java
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
 * Performs the database operations of the <code>AccountTransaction</code>.
 * Primary key to the database table "account_transaction" is the account transaction ID.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Repository
public interface AccountTransactionRepository
        extends CrudRepository<AccountTransaction, BigInteger>, PagingAndSortingRepository<AccountTransaction, BigInteger> {

    /**
     * Returns the list of <code>AccountTransaction</code> using email address.
     *
     * @param email the email address
     * @return the list of AccountTransaction
     */
    @Query(value = "SELECT at FROM AccountTransaction at " +
            "WHERE at.email = :email " +
            "ORDER BY at.transactionDateTime DESC")
    List<AccountTransaction> findByEmail(String email);

    /**
     * Returns the sublist of <code>AccountTransaction</code> using email address with pagination support.
     *
     * @param email    the email address
     * @param pageable the Pageable supporting pagination
     * @return the sublist of AccountTransaction
     */
    @Query(value = "SELECT at FROM AccountTransaction at " +
            "WHERE at.email = :email " +
            "ORDER BY at.transactionDateTime DESC")
    Page<AccountTransaction> findByEmail(String email, Pageable pageable);

    /**
     * Returns the list of <code>AccountTransaction</code> using email address and currency.
     *
     * @param email    the email address
     * @param currency the currency
     * @return the list of AccountTransaction
     */
    @Query(value = "SELECT at FROM AccountTransaction at " +
            "WHERE at.email = :email and at.currency = :currency " +
            "ORDER BY at.transactionDateTime DESC")
    List<AccountTransaction> findByEmailAndCurrency(String email, String currency);

    /**
     * Returns the sublist of <code>AccountTransaction</code> using email address and currency with pagination support.
     *
     * @param email    the email address
     * @param currency the currency
     * @param pageable the Pageable supporting pagination
     * @return the sublist of AccountTransaction
     */
    @Query(value = "SELECT at FROM AccountTransaction at " +
            "WHERE at.email = :email and at.currency = :currency " +
            "ORDER BY at.transactionDateTime DESC")
    Page<AccountTransaction> findByEmailAndCurrency(String email, String currency, Pageable pageable);

}
