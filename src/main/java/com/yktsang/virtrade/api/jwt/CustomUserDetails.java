/*
 * CustomUserDetails.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.api.jwt;

import com.yktsang.virtrade.entity.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The custom user details for authentication and authorization purposes.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public class CustomUserDetails implements UserDetails {

    /**
     * The username.
     */
    private final String username;
    /**
     * The password.
     */
    private final String password;
    /**
     * The active indicator.
     */
    private final boolean active;
    /**
     * The authorities.
     */
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * Creates a <code>CustomUserDetails</code> with <code>Account</code>.
     *
     * @param acct the Account
     */
    public CustomUserDetails(Account acct) {
        this.username = acct.getEmail();
        this.password = acct.getPassword();
        this.active = acct.isActive();
        List<GrantedAuthority> auths = new ArrayList<>();
        auths.add(new SimpleGrantedAuthority("USER"));
        if (acct.isAdmin()) {
            auths.add(new SimpleGrantedAuthority("ADMIN"));
        }
        this.authorities = auths;
    }

    /**
     * Returns the authorities.
     *
     * @return the authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Returns the password.
     *
     * @return the password
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns the username.
     *
     * @return the username
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Returns true if account is valid, false otherwise.
     *
     * @return true if account is valid, false otherwise
     */
    @Override
    public boolean isAccountNonExpired() {
        return active;
    }

    /**
     * Returns true if account is active, false otherwise.
     *
     * @return true if account is active, false otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    /**
     * Returns true if user credentials are valid, false otherwise.
     *
     * @return true if user credentials are valid, false otherwise
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return active;
    }

    /**
     * Returns true if user is enabled, false otherwise.
     *
     * @return true if user is enabled, false otherwise
     */
    @Override
    public boolean isEnabled() {
        return active;
    }

}