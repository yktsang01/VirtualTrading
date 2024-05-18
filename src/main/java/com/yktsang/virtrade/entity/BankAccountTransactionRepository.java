/*
 * BankAccountTransactionRepository.java
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
 * Performs the database operations of the <code>BankAccountTransaction</code>.
 * Primary key to the database table "bank_account_transaction" is the bank account transaction ID.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Repository
public interface BankAccountTransactionRepository
        extends CrudRepository<BankAccountTransaction, BigInteger>, PagingAndSortingRepository<BankAccountTransaction, BigInteger> {

    /**
     * Returns the list of <code>BankAccountTransaction</code> using email address.
     *
     * @param email the email address
     * @return the list of BankAccountTransaction
     */
    @Query(value = "SELECT bat FROM BankAccountTransaction bat " +
            "WHERE bat.email = :email " +
            "ORDER BY bat.transactionDateTime DESC")
    List<BankAccountTransaction> findByEmail(String email);

    /**
     * Returns the list of <code>BankAccountTransaction</code> using email address with pagination support.
     *
     * @param email    the email address
     * @param pageable the Pageable supporting pagination
     * @return the list of BankAccountTransaction
     */
    @Query(value = "SELECT bat FROM BankAccountTransaction bat " +
            "WHERE bat.email = :email " +
            "ORDER BY bat.transactionDateTime DESC")
    Page<BankAccountTransaction> findByEmail(String email, Pageable pageable);

    /**
     * Returns the list of <code>BankAccountTransaction</code> using email address and currency.
     *
     * @param email    the email address
     * @param currency the currency
     * @return the list of BankAccountTransaction
     */
    @Query(value = "SELECT bat FROM BankAccountTransaction bat " +
            "WHERE bat.email = :email and bat.currency = :currency " +
            "ORDER BY bat.transactionDateTime DESC")
    List<BankAccountTransaction> findByEmailAndCurrency(String email, String currency);

    /**
     * Returns the list of <code>BankAccountTransaction</code> using email address and currency with pagination support.
     *
     * @param email    the email address
     * @param currency the currency
     * @param pageable the Pageable supporting pagination
     * @return the list of BankAccountTransaction
     */
    @Query(value = "SELECT bat FROM BankAccountTransaction bat " +
            "WHERE bat.email = :email and bat.currency = :currency " +
            "ORDER BY bat.transactionDateTime DESC")
    Page<BankAccountTransaction> findByEmailAndCurrency(String email, String currency, Pageable pageable);

}
