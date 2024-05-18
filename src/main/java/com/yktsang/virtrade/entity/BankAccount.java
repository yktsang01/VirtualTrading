/*
 * BankAccount.java
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
 * The bank account. Represents the database table "bank_account".
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Entity
@Table(name = "bank_account")
public class BankAccount {

    /**
     * The bank account ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "baid")
    private BigInteger bankAccountId;
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
     * The bank name.
     */
    @Column(name = "bank_name")
    private String bankName;
    /**
     * The bank account number.
     */
    @Column(name = "bank_account_number")
    private String bankAccountNumber;
    /**
     * The in use indicator.
     */
    @Convert(converter = BooleanConverter.class)
    @Column(name = "in_use")
    private boolean inUse;
    /**
     * The creation datetime.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_datetime")
    private LocalDateTime creationDateTime;
    /**
     * The last updated datetime.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_updated_datetime")
    private LocalDateTime lastUpdatedDateTime;

    /**
     * Constructs a <code>BankAccount</code>.
     */
    public BankAccount() {
    }

    /**
     * Constructs a <code>BankAccount</code> with email address, currency,
     * bank name, and bank account number.
     *
     * @param email             the email address
     * @param currency          the currency
     * @param bankName          the bank name
     * @param bankAccountNumber the bank account number
     */
    public BankAccount(String email, String currency, String bankName, String bankAccountNumber) {
        this.email = email;
        this.currency = currency.toUpperCase();
        this.bankName = bankName;
        this.bankAccountNumber = bankAccountNumber;
        this.inUse = true;
        this.creationDateTime = LocalDateTime.now();
    }

    /**
     * Returns the bank account ID.
     *
     * @return the bank account ID
     */
    public BigInteger getBankAccountId() {
        return bankAccountId;
    }

    /**
     * Assigns the bank account ID.
     *
     * @param bankAccountId the bank account ID
     */
    public void setBankAccountId(BigInteger bankAccountId) {
        this.bankAccountId = bankAccountId;
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
     * Returns the bank name.
     *
     * @return the bank name
     */
    public String getBankName() {
        return bankName;
    }

    /**
     * Assigns the bank name.
     *
     * @param bankName the bank name
     */
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    /**
     * Returns the bank account number.
     *
     * @return the bank account number
     */
    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    /**
     * Assigns the bank account number.
     *
     * @param bankAccountNumber the bank account number
     */
    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    /**
     * Returns the in use indicator.
     *
     * @return the in use indicator
     */
    public boolean isInUse() {
        return inUse;
    }

    /**
     * Assigns the in use indicator.
     *
     * @param inUse the in use indicator
     */
    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    /**
     * Returns the creation datetime.
     *
     * @return the creation datetime
     */
    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    /**
     * Assigns the creation datetime.
     *
     * @param creationDateTime the creation datetime
     */
    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    /**
     * Returns the last updated datetime.
     *
     * @return the last updated datetime
     */
    public LocalDateTime getLastUpdatedDateTime() {
        return lastUpdatedDateTime;
    }

    /**
     * Assigns the last updated datetime.
     *
     * @param lastUpdatedDateTime the last updated datetime
     */
    public void setLastUpdatedDateTime(LocalDateTime lastUpdatedDateTime) {
        this.lastUpdatedDateTime = lastUpdatedDateTime;
    }

}
