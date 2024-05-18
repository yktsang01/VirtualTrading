/*
 * AccountRepository.java
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
 * Performs the database operations of the <code>Account</code>.
 * Primary key to the database table "account" is the email address.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Repository
public interface AccountRepository
        extends CrudRepository<Account, String>, PagingAndSortingRepository<Account, String> {

    /**
     * Returns the list of <code>Account</code>> that requested admin access.
     *
     * @param adminAccess the admin access indicator (always false)
     * @return the list of Account that requested admin access
     */
    @Query(value = "SELECT a FROM Account a " +
            "WHERE a.admin = :adminAccess and a.adminRequestDateTime is not null " +
            "ORDER BY a.adminRequestDateTime DESC")
    List<Account> findMembersRequestedAdminAccess(boolean adminAccess);

    /**
     * Returns the sublist of <code>Account</code>> that requested admin access with pagination support.
     *
     * @param adminAccess the admin access indicator (always false)
     * @param pageable    the Pageable supporting pagination
     * @return the sublist of Account that requested admin access
     */
    @Query(value = "SELECT a FROM Account a " +
            "WHERE a.admin = :adminAccess and a.adminRequestDateTime is not null " +
            "ORDER BY a.adminRequestDateTime DESC")
    Page<Account> findMembersRequestedAdminAccess(boolean adminAccess, Pageable pageable);

    /**
     * Returns the list of <code>Account</code> that have admin access, excluding the admin email.
     *
     * @param adminAccess the admin access indicator (always true)
     * @param adminEmail  the admin email
     * @return the list of Account that have admin access
     */
    @Query(value = "SELECT a FROM Account a " +
            "WHERE a.admin = :adminAccess and a.email != :adminEmail " +
            "ORDER BY a.adminApprovalDateTime DESC")
    List<Account> findMembersWithAdminAccess(boolean adminAccess, String adminEmail);

    /**
     * Returns the sublist of <code>Account</code> that have admin access, excluding the admin email with pagination support.
     *
     * @param adminAccess the admin access indicator (always true)
     * @param adminEmail  the admin email
     * @param pageable    the Pageable supporting pagination
     * @return the sublist of Account that have admin access
     */
    @Query(value = "SELECT a FROM Account a " +
            "WHERE a.admin = :adminAccess and a.email != :adminEmail " +
            "ORDER BY a.adminApprovalDateTime DESC")
    Page<Account> findMembersWithAdminAccess(boolean adminAccess, String adminEmail, Pageable pageable);

}
