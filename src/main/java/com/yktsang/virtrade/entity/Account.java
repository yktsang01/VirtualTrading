/*
 * Account.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yktsang.virtrade.util.SecurityUtil;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * The account. Represents the database table "account".
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Entity
@Table(name = "account")
public class Account {

    /**
     * The email address.
     */
    @Id
    @Column(name = "email")
    private String email;
    /**
     * The hashed password.
     */
    @Column(name = "password")
    private String password;
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
     * The admin request datetime.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "admin_request_datetime")
    private LocalDateTime adminRequestDateTime;
    /**
     * The admin approval datetime
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "admin_approval_datetime")
    private LocalDateTime adminApprovalDateTime;
    /**
     * The admin approve by.
     */
    @Column(name = "admin_approve_by")
    private String adminApproveBy;
    /**
     * The admin indicator.
     */
    @Convert(converter = BooleanConverter.class)
    @Column(name = "admin_access")
    private boolean admin;
    /**
     * The deactivation datetime.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deactivation_datetime")
    private LocalDateTime deactivationDateTime;
    /**
     * The deactivation reason.
     */
    @Column(name = "deactivation_reason")
    private String deactivationReason;
    /**
     * The active indicator.
     */
    @Convert(converter = BooleanConverter.class)
    @Column(name = "active")
    private boolean active;

    /**
     * Constructs a <code>Account</code>.
     */
    public Account() {
    }

    /**
     * Constructs a <code>Account</code> with email address and hashed password.
     *
     * @param email    the email address
     * @param password the raw password
     */
    public Account(String email, String password) {
        this.email = email;
        this.password = SecurityUtil.hashPassword(password);
        this.active = true;
        this.admin = false;
        this.creationDateTime = LocalDateTime.now();
    }

    /**
     * Returns the email address
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
     * Returns the hashed password.
     *
     * @return the hashed password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Assigns the hashed password.
     *
     * @param password the raw password
     */
    public void setPassword(String password) {
        this.password = SecurityUtil.hashPassword(password);
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
     * Returns the admin request datetime.
     *
     * @return the admin request datetime
     */
    public LocalDateTime getAdminRequestDateTime() {
        return adminRequestDateTime;
    }

    /**
     * Assigns the admin request datetime.
     *
     * @param adminRequestDateTime the admin request datetime
     */
    public void setAdminRequestDateTime(LocalDateTime adminRequestDateTime) {
        this.adminRequestDateTime = adminRequestDateTime;
    }

    /**
     * Returns true if request admin is submitted, false otherwise.
     *
     * @return true if request admin is submitted, false otherwise
     */
    public boolean hasRequestAdmin() {
        return Objects.nonNull(adminRequestDateTime);
    }

    /**
     * Returns the admin approval datetime.
     *
     * @return the admin approval datetime
     */
    public LocalDateTime getAdminApprovalDateTime() {
        return adminApprovalDateTime;
    }

    /**
     * Assigns the admin approval datetime.
     *
     * @param adminApprovalDateTime the admin approval datetime
     */
    public void setAdminApprovalDateTime(LocalDateTime adminApprovalDateTime) {
        this.adminApprovalDateTime = adminApprovalDateTime;
    }

    /**
     * Returns the admin approve by.
     *
     * @return the admin approve by
     */
    public String getAdminApproveBy() {
        return adminApproveBy;
    }

    /**
     * Assigns the admin approve by.
     *
     * @param adminApproveBy the admin approve by
     */
    public void setAdminApproveBy(String adminApproveBy) {
        this.adminApproveBy = adminApproveBy;
    }

    /**
     * Returns the admin indicator.
     *
     * @return the admin indicator
     */
    public boolean isAdmin() {
        return admin;
    }

    /**
     * Assigns the admin indicator.
     *
     * @param admin the admin indicator
     */
    public void setAdmin(boolean admin) {
        this.admin = admin;
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
     * Returns the deactivation reason.
     *
     * @return the deactivation reason
     */
    public String getDeactivationReason() {
        return deactivationReason;
    }

    /**
     * Assigns the deactivation reason.
     *
     * @param deactivationReason the deactivation reason
     */
    public void setDeactivationReason(String deactivationReason) {
        this.deactivationReason = deactivationReason;
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

}
