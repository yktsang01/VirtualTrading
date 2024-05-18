/*
 * TraderRepository.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.entity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Performs the database operations of the <code>Trader</code>.
 * Primary key to the database table "trader" is the email address.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Repository
public interface TraderRepository
        extends CrudRepository<Trader, String>, PagingAndSortingRepository<Trader, String> {
}
