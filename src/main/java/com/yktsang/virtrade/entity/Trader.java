/*
 * Trader.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * The trader. Represents the database table "trader".
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Entity
@Table(name = "trader")
public class Trader {

    /**
     * The email address.
     */
    @Id
    @Column(name = "email")
    private String email;
    /**
     * The full name.
     */
    @Column(name = "full_name")
    private String fullName;
    /**
     * The date of birth.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    /**
     * The show/hide date of birth indicator.
     */
    @Convert(converter = BooleanConverter.class)
    @Column(name = "hide_date_of_birth")
    private boolean hideDateOfBirth;
    /**
     * The risk tolerance level.
     */
    @Convert(converter = RiskToleranceLevelEnumConverter.class)
    @Column(name = "risk_tolerance")
    private RiskToleranceLevel riskTolerance;
    /**
     * The auto transfer to bank indicator.
     */
    @Convert(converter = BooleanConverter.class)
    @Column(name = "auto_transfer_to_bank")
    private boolean autoTransferToBank;
    /**
     * The allow reset portfolio indicator.
     */
    @Convert(converter = BooleanConverter.class)
    @Column(name = "allow_reset")
    private boolean allowReset;
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
     * The deactivation datetime.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deactivation_datetime")
    private LocalDateTime deactivationDateTime;

    /**
     * Constructs a <code>Trader</code>.
     */
    public Trader() {
    }

    /**
     * Constructs a <code>Trader</code> with email address, full name, date of birth,
     * show/hide date of birth indicator, risk tolerance level,
     * auto transfer to bank indicator, and allow reset portfolio indicator.
     *
     * @param email              the email address
     * @param fullName           the full name
     * @param dateOfBirth        the date of birth
     * @param hideDateOfBirth    the show/hide date of birth indicator
     * @param riskTolerance      the risk tolerance level
     * @param autoTransferToBank the auto transfer to bank indicator
     * @param allowReset         the allow reset portfolio indicator
     */
    public Trader(String email, String fullName,
                  LocalDate dateOfBirth, boolean hideDateOfBirth,
                  RiskToleranceLevel riskTolerance,
                  boolean autoTransferToBank, boolean allowReset) {
        this.email = email;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.hideDateOfBirth = hideDateOfBirth;
        this.riskTolerance = riskTolerance;
        this.autoTransferToBank = autoTransferToBank;
        this.allowReset = allowReset;
        this.creationDateTime = LocalDateTime.now();
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
     * Returns the full name.
     *
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Assigns the full name.
     *
     * @param fullName the full name
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Returns the date of birth.
     *
     * @return the date of birth
     */
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Assigns the date of birth.
     *
     * @param dateOfBirth the date of birth
     */
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Returns the show/hide date of birth indicator.
     *
     * @return the show/hide date of birth indicator
     */
    public boolean isHideDateOfBirth() {
        return hideDateOfBirth;
    }

    /**
     * Assigns the show/hide date of birth indicator.
     *
     * @param hideDateOfBirth the show/hide date of birth indicator
     */
    public void setHideDateOfBirth(boolean hideDateOfBirth) {
        this.hideDateOfBirth = hideDateOfBirth;
    }

    /**
     * Returns the risk tolerance level.
     *
     * @return the risk tolerance level
     */
    public RiskToleranceLevel getRiskTolerance() {
        return riskTolerance;
    }

    /**
     * Assigns the risk tolerance level.
     *
     * @param riskTolerance the risk tolerance
     */
    public void setRiskTolerance(RiskToleranceLevel riskTolerance) {
        this.riskTolerance = riskTolerance;
    }

    /**
     * Returns the auto transfer to bank indicator.
     *
     * @return the auto transfer to bank indicator
     */
    public boolean isAutoTransferToBank() {
        return autoTransferToBank;
    }

    /**
     * Assigns the auto transfer to bank indicator.
     *
     * @param autoTransferToBank the auto transfer to bank indicator
     */
    public void setAutoTransferToBank(boolean autoTransferToBank) {
        this.autoTransferToBank = autoTransferToBank;
    }

    /**
     * Returns the allow reset portfolio indicator.
     *
     * @return the allow reset portfolio indicator
     */
    public boolean isAllowReset() {
        return allowReset;
    }

    /**
     * Assigns the allow reset portfolio indicator.
     *
     * @param allowReset the allow reset portfolio indicator
     */
    public void setAllowReset(boolean allowReset) {
        this.allowReset = allowReset;
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

    /**
     * Returns the deactivation datetime.
     *
     * @return the deactivation datetime
     */
    public LocalDateTime getDeactivationDateTime() {
        return deactivationDateTime;
    }

    /**
     * Assigns the deactivation datetime.
     *
     * @param deactivationDateTime the deactivation datetime
     */
    public void setDeactivationDateTime(LocalDateTime deactivationDateTime) {
        this.deactivationDateTime = deactivationDateTime;
    }

}
