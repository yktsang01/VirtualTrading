/*
 * IsoData.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * The ISO data. Represents the database table "iso_data".
 * ISO-3166 (country codes) and ISO-4217 (currency codes) make up the data for each record.
 * The country alpha-2 code and country name come from ISO-3166.
 * The currency alpha code, currency name, currency minor units come from ISO-4217.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Entity
@Table(name = "iso_data")
public class IsoData {

    /**
     * The country alpha-2 code.
     */
    @Id
    @Column(name = "country_alpha2_code")
    private String countryAlpha2Code;
    /**
     * The country name.
     */
    @Column(name = "country_name")
    private String countryName;
    /**
     * The currency alphabetical code.
     */
    @Column(name = "currency_alpha_code")
    private String currencyAlphaCode;
    /**
     * The currency name.
     */
    @Column(name = "currency_name")
    private String currencyName;
    /**
     * The currency minor units (number of decimal places).
     */
    @Column(name = "currency_minor_units")
    private Integer currencyMinorUnits;
    /**
     * The active indicator.
     */
    @Convert(converter = BooleanConverter.class)
    @Column(name = "active")
    private boolean active;
    /**
     * The creation datetime.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_datetime")
    private LocalDateTime creationDateTime;
    /**
     * The created by.
     */
    @Column(name = "created_by")
    private String createdBy;
    /**
     * The last updated datetime.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_updated_datetime")
    private LocalDateTime lastUpdatedDateTime;
    /**
     * The last updated by.
     */
    @Column(name = "last_updated_by")
    private String lastUpdatedBy;
    /**
     * The activation datetime.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "activation_datetime")
    private LocalDateTime activationDateTime;
    /**
     * The activated by.
     */
    @Column(name = "activated_by")
    private String activatedBy;
    /**
     * The deactivation datetime.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deactivation_datetime")
    private LocalDateTime deactivationDateTime;
    /**
     * The deactivated by.
     */
    @Column(name = "deactivated_by")
    private String deactivatedBy;

    /**
     * Constructs a <code>IsoData</code>.
     */
    public IsoData() {
    }

    /**
     * Constructs a <code>IsoData</code> with country code, country name,
     * currency code, currency name, currency minor units, active indicator.
     *
     * @param countryAlpha2Code  the country alpha-2 code
     * @param countryName        the country name
     * @param currencyAlphaCode  the currency alpha code
     * @param currencyName       the currency name
     * @param currencyMinorUnits the currency minor units
     * @param active             the active indicator
     * @param createdBy          the created by
     */
    public IsoData(String countryAlpha2Code, String countryName,
                   String currencyAlphaCode, String currencyName, Integer currencyMinorUnits,
                   boolean active, String createdBy) {
        this.countryAlpha2Code = countryAlpha2Code.toUpperCase();
        this.countryName = countryName;
        this.currencyAlphaCode = currencyAlphaCode.toUpperCase();
        this.currencyName = currencyName;
        this.currencyMinorUnits = currencyMinorUnits;
        this.active = active;
        if (active) {
            this.activatedBy = createdBy;
            this.activationDateTime = LocalDateTime.now();
        } else {
            this.deactivatedBy = createdBy;
            this.deactivationDateTime = LocalDateTime.now();
        }
        this.createdBy = createdBy;
        this.creationDateTime = LocalDateTime.now();
    }

    /**
     * Returns the country alpha-2code.
     *
     * @return the country alpha-2 code
     */
    public String getCountryAlpha2Code() {
        return countryAlpha2Code;
    }

    /**
     * Assigns the country alpha-2 code.
     *
     * @param countryAlpha2Code the country alpha-2 code
     */
    public void setCountryAlpha2Code(String countryAlpha2Code) {
        this.countryAlpha2Code = countryAlpha2Code.toUpperCase();
    }

    /**
     * Returns the country name.
     *
     * @return the country name
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * Assigns the country name.
     *
     * @param countryName the country name
     */
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    /**
     * Returns the currency alphabetical code.
     *
     * @return the currency alphabetical code
     */
    public String getCurrencyAlphaCode() {
        return currencyAlphaCode;
    }

    /**
     * Assigns the currency alphabetical code.
     *
     * @param currencyAlphaCode the currency alphabetical code
     */
    public void setCurrencyAlphaCode(String currencyAlphaCode) {
        this.currencyAlphaCode = currencyAlphaCode.toUpperCase();
    }

    /**
     * Returns the currency name.
     *
     * @return the currency name
     */
    public String getCurrencyName() {
        return currencyName;
    }

    /**
     * Assigns the currency name.
     *
     * @param currencyName the currency name
     */
    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    /**
     * Returns the currency minor units.
     *
     * @return the currency minor units
     */
    public Integer getCurrencyMinorUnits() {
        return currencyMinorUnits;
    }

    /**
     * Assigns the currency minor units.
     *
     * @param currencyMinorUnits the currency minor units
     */
    public void setCurrencyMinorUnits(Integer currencyMinorUnits) {
        this.currencyMinorUnits = currencyMinorUnits;
    }

    /**
     * Returns the active indicator.
     *
     * @return the active indicator
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Assigns the active indicator.
     *
     * @param active the active indicator
     */
    public void setActive(boolean active) {
        this.active = active;
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
     * Returns the created by.
     *
     * @return the created by
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Assigns the created by.
     *
     * @param createdBy the created by
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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
     * Returns the last updated by.
     *
     * @return the last updated by
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * Assigns the last updated by.
     *
     * @param lastUpdatedBy the last updated by
     */
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * Returns the activation datetime.
     *
     * @return the activation datetime
     */
    public LocalDateTime getActivationDateTime() {
        return activationDateTime;
    }

    /**
     * Assigns the activation datetime.
     *
     * @param activationDateTime the activation datetime
     */
    public void setActivationDateTime(LocalDateTime activationDateTime) {
        this.activationDateTime = activationDateTime;
    }

    /**
     * Returns the activated by.
     *
     * @return the activated by
     */
    public String getActivatedBy() {
        return activatedBy;
    }

    /**
     * Assigns the activated by.
     *
     * @param activatedBy the activated by
     */
    public void setActivatedBy(String activatedBy) {
        this.activatedBy = activatedBy;
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

    /**
     * Returns the deactivated by.
     *
     * @return the deactivated by
     */
    public String getDeactivatedBy() {
        return deactivatedBy;
    }

    /**
     * Assigns the deactivated by.
     *
     * @param deactivatedBy the deactivated by
     */
    public void setDeactivatedBy(String deactivatedBy) {
        this.deactivatedBy = deactivatedBy;
    }

}
