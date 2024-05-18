/*
 * UserDetailsServiceImpl.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.api.jwt;

import com.yktsang.virtrade.entity.Account;
import com.yktsang.virtrade.entity.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * The implementation to <code>org.springframework.security.core.userdetails.UserDetailsService</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    /**
     * The account repository.
     */
    @Autowired
    private AccountRepository accountRepo;

    /**
     * Returns the user details using the username.
     *
     * @param username the username
     * @return the user details
     * @throws UsernameNotFoundException when the username is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> accountOpt = accountRepo.findById(username);
        if (accountOpt.isEmpty()) {
            throw new UsernameNotFoundException(username + " not found");
        }
        return new CustomUserDetails(accountOpt.get());
    }

}
