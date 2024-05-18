/*
 * AccountBalanceRepository.java
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

import java.util.List;

/**
 * Performs the database operations of the <code>AccountBalance</code>.
 * Primary composite key (<code>AccountBalancePK</code>) to the database table "account_balance" is
 * the email address and the currency.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Repository
public interface AccountBalanceRepository
        extends CrudRepository<AccountBalance, AccountBalancePK>, PagingAndSortingRepository<AccountBalance, AccountBalancePK> {

    /**
     * Returns the list of <code>AccountBalance</code> using email address.
     *
     * @param email the email address
     * @return the list of AccountBalance
     */
    @Query(value = "SELECT ab FROM AccountBalance ab " +
            "WHERE ab.email = :email " +
            "ORDER BY ab.creationDateTime DESC")
    List<AccountBalance> findByEmail(String email);

    /**
     * Returns the sublist of <code>AccountBalance</code> using email address with pagination support.
     *
     * @param email    the email address
     * @param pageable the Pageable supporting pagination
     * @return the sublist of AccountBalance
     */
    @Query(value = "SELECT ab FROM AccountBalance ab " +
            "WHERE ab.email = :email " +
            "ORDER BY ab.creationDateTime DESC")
    Page<AccountBalance> findByEmail(String email, Pageable pageable);

}
