/*
 * BankAccountRepository.java
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
 * Performs the database operations of the <code>BankAccount</code>.
 * Primary key to the database table "bank_account" is the bank ID.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Repository
public interface BankAccountRepository
        extends CrudRepository<BankAccount, BigInteger>, PagingAndSortingRepository<BankAccount, BigInteger> {

    /**
     * Returns the list of active (in use) <code>BankAccount</code> using email address.
     *
     * @param email the email address
     * @param inUse the in use indicator (always true)
     * @return the list of BankAccount
     */
    @Query(value = "SELECT ba FROM BankAccount ba " +
            "WHERE ba.email = :email and ba.inUse = :inUse " +
            "ORDER BY ba.creationDateTime DESC")
    List<BankAccount> findByEmail(String email, boolean inUse);

    /**
     * Returns the sublist of active (in use) <code>BankAccount</code> using email address with pagination support.
     *
     * @param email    the email address
     * @param inUse    the in use indicator (always true)
     * @param pageable the Pageable supporting pagination
     * @return the sublist of BankAccount
     */
    @Query(value = "SELECT ba FROM BankAccount ba " +
            "WHERE ba.email = :email and ba.inUse = :inUse " +
            "ORDER BY ba.creationDateTime DESC")
    Page<BankAccount> findByEmail(String email, boolean inUse, Pageable pageable);

    /**
     * Returns the list of <code>BankAccount</code> using email address.
     *
     * @param email the email address
     * @return the list of BankAccount
     */
    @Query(value = "SELECT ba FROM BankAccount ba " +
            "WHERE ba.email = :email " +
            "ORDER BY ba.creationDateTime DESC")
    List<BankAccount> findByEmail(String email);

    /**
     * Returns the list of active (in use) <code>BankAccount</code> using email address and currency.
     *
     * @param email    the email address
     * @param currency the currency
     * @param inUse    the in use indicator (always true)
     * @return the list of BankAccount
     */
    @Query(value = "SELECT ba FROM BankAccount ba " +
            "WHERE ba.email = :email and ba.currency = :currency and ba.inUse = :inUse " +
            "ORDER BY ba.creationDateTime DESC")
    List<BankAccount> findByEmailAndCurrency(String email, String currency, boolean inUse);

    /**
     * Returns the sublist of active (in use) <code>BankAccount</code> using email address and currency with pagination support.
     *
     * @param email    the email address
     * @param currency the currency
     * @param inUse    the in use indicator (always true)
     * @param pageable the Pageable supporting pagination
     * @return the sublist of BankAccount
     */
    @Query(value = "SELECT ba FROM BankAccount ba " +
            "WHERE ba.email = :email and ba.currency = :currency and ba.inUse = :inUse " +
            "ORDER BY ba.creationDateTime DESC")
    Page<BankAccount> findByEmailAndCurrency(String email, String currency, boolean inUse, Pageable pageable);

    /**
     * Returns the list of <code>BankAccount</code> using email address and currency.
     *
     * @param email    the email address
     * @param currency the currency
     * @return the list of BankAccount
     */
    @Query(value = "SELECT ba FROM BankAccount ba " +
            "WHERE ba.email = :email and ba.currency = :currency " +
            "ORDER BY ba.creationDateTime DESC")
    List<BankAccount> findByEmailAndCurrency(String email, String currency);

}
