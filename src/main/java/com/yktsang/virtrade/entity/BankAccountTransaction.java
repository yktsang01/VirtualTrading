/*
 * BankAccountTransaction.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * The bank account transaction. Represents the database table "bank_account_transaction".
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Entity
@Table(name = "bank_account_transaction")
public class BankAccountTransaction {

    /**
     * The bank account transaction ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batid")
    private BigInteger bankAccountTransactionId;
    /**
     * The email address.
     */
    @Column(name = "email")
    private String email;
    /**
     * The currency.
     */
    @Column(name = "currency")
    private String currency;
    /**
     * The transaction datetime.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "transaction_datetime")
    private LocalDateTime transactionDateTime;
    /**
     * The transaction description.
     */
    @Column(name = "transaction_description")
    private String transactionDescription;

    /**
     * Creates a <code>BankAccountTransaction</code>.
     */
    public BankAccountTransaction() {
    }

    /**
     * Creates a <code>BankAccountTransaction</code> with email address, currency, transaction description.
     *
     * @param email                  the email address
     * @param currency               the currency
     * @param transactionDescription the transaction description
     */
    public BankAccountTransaction(String email, String currency, String transactionDescription) {
        this.email = email;
        this.currency = currency.toUpperCase();
        this.transactionDateTime = LocalDateTime.now();
        this.transactionDescription = transactionDescription;
    }

    /**
     * Returns the bank account transaction ID.
     *
     * @return the bank account transaction ID
     */
    public BigInteger getBankAccountTransactionId() {
        return bankAccountTransactionId;
    }

    /**
     * Assigns the bank account transaction ID.
     *
     * @param bankAccountTransactionId the bank account transaction ID
     */
    public void setBankAccountTransactionId(BigInteger bankAccountTransactionId) {
        this.bankAccountTransactionId = bankAccountTransactionId;
    }

    /**
     * Returns the email address.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Assigns the email address.
     *
     * @param email the email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the currency.
     *
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Assigns the currency.
     *
     * @param currency the currency
     */
    public void setCurrency(String currency) {
        this.currency = currency.toUpperCase();
    }

    /**
     * Returns the transaction datetime.
     *
     * @return the transaction datetime
     */
    public LocalDateTime getTransactionDateTime() {
        return transactionDateTime;
    }

    /**
     * Assigns the transaction datetime.
     *
     * @param transactionDateTime the transaction datetime
     */
    public void setTransactionDateTime(LocalDateTime transactionDateTime) {
        this.transactionDateTime = transactionDateTime;
    }

    /**
     * Returns the transaction description.
     *
     * @return the transaction description
     */
    public String getTransactionDescription() {
        return transactionDescription;
    }

    /**
     * Assigns the transaction description.
     *
     * @param transactionDescription the transaction description
     */
    public void setTransactionDescription(String transactionDescription) {
        this.transactionDescription = transactionDescription;
    }

}
