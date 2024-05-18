/*
 * TradingTransactionRepository.java
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
 * Performs the database operations of the <code>TradingTransaction</code>.
 * Primary key to the database table "trading_transaction" is the trading transaction ID.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Repository
public interface TradingTransactionRepository
        extends CrudRepository<TradingTransaction, BigInteger>, PagingAndSortingRepository<TradingTransaction, BigInteger> {

    /**
     * Returns the list of <code>TradingTransaction</code> using email address.
     *
     * @param email the email address
     * @return the list of TradingTransaction
     */
    @Query(value = "SELECT tt FROM TradingTransaction tt " +
            "WHERE tt.email = :email " +
            "ORDER BY tt.creationDateTime DESC")
    List<TradingTransaction> findByEmail(String email);

    /**
     * Returns the sublist of <code>TradingTransaction</code> using email address with pagination support.
     *
     * @param email    the email address
     * @param pageable the Pageable supporting pagination
     * @return the sublist of TradingTransaction
     */
    @Query(value = "SELECT tt FROM TradingTransaction tt " +
            "WHERE tt.email = :email " +
            "ORDER BY tt.creationDateTime DESC")
    Page<TradingTransaction> findByEmail(String email, Pageable pageable);

    /**
     * Returns the list of <code>TradingTransaction</code> using email address and currency.
     *
     * @param email    the email address
     * @param currency the currency
     * @return the list of TradingTransaction
     */
    @Query(value = "SELECT tt FROM TradingTransaction tt " +
            "WHERE tt.email = :email and tt.transactionCurrency = :currency " +
            "ORDER BY tt.creationDateTime DESC")
    List<TradingTransaction> findByEmailAndCurrency(String email, String currency);

    /**
     * Returns the sublist of <code>TradingTransaction</code> using email address and currency with pagination support.
     *
     * @param email    the email address
     * @param currency the currency
     * @param pageable the Pageable supporting pagination
     * @return the sublist of TradingTransaction
     */
    @Query(value = "SELECT tt FROM TradingTransaction tt " +
            "WHERE tt.email = :email and tt.transactionCurrency = :currency " +
            "ORDER BY tt.creationDateTime DESC")
    Page<TradingTransaction> findByEmailAndCurrency(String email, String currency, Pageable pageable);

    /**
     * Returns the list of <code>TradingTransaction</code> using email address and portfolio ID.
     *
     * @param email       the email address
     * @param portfolioId the portfolio ID
     * @return the list of TradingTransaction
     */
    @Query(value = "SELECT tt FROM TradingTransaction tt " +
            "WHERE tt.email = :email and tt.portfolioId = :portfolioId " +
            "ORDER BY tt.creationDateTime DESC")
    List<TradingTransaction> findByPortfolioId(String email, BigInteger portfolioId);

    /**
     * Returns the sublist of <code>TradingTransaction</code> using email address and portfolio ID with pagination support.
     *
     * @param email       the email address
     * @param portfolioId the portfolio ID
     * @param pageable    the Pageable supporting pagination
     * @return the sublist of TradingTransaction
     */
    @Query(value = "SELECT tt FROM TradingTransaction tt " +
            "WHERE tt.email = :email and tt.portfolioId = :portfolioId " +
            "ORDER BY tt.creationDateTime DESC")
    Page<TradingTransaction> findByPortfolioId(String email, BigInteger portfolioId, Pageable pageable);

    /**
     * Returns the list of <code>TradingTransaction</code> using email address, trading deed, and trading symbol.
     *
     * @param email  the email address
     * @param deed   the trading deed
     * @param symbol the trading symbol
     * @return the list of TradingTransaction
     */
    @Query(value = "SELECT tt FROM TradingTransaction tt " +
            "WHERE tt.email = :email and tt.tradingDeed = :deed and tt.tradingSymbol = :symbol " +
            "ORDER BY tt.creationDateTime DESC")
    List<TradingTransaction> findByTradingSymbol(String email, TradingDeed deed, String symbol);

}
