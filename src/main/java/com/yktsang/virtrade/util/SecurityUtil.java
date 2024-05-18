/*
 * SecurityUtil.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.util;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

/**
 * Provides the security-related utility functions.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public class SecurityUtil {

    /**
     * Returns the hashed password, given the raw password.
     * Uses Spring Security <code>BCryptPasswordEncoder</code>.
     *
     * @param rawPassword the raw password
     * @return the hashed password
     */
    public static String hashPassword(String rawPassword) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10, random);
        return encoder.encode(rawPassword);
    }

    /**
     * Returns true if the provided passwords match, false otherwise.
     * Uses Spring Security <code>BCryptPasswordEncoder</code>.
     *
     * @param rawPassword    the raw password
     * @param hashedPassword the hashed password
     * @return true if the provided passwords match, false otherwise
     */
    public static boolean checkPassword(String rawPassword, String hashedPassword) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10, random);
        return encoder.matches(rawPassword, hashedPassword);
    }

    /**
     * Returns true if the email address is not valid, false otherwise.
     * Uses Apaches Commons validator <code>EmailValidator</code> following the RFC 822 standard.
     *
     * @param emailAddress the email address
     * @return true if the email address is not valid, false otherwise
     */
    public static boolean isEmailNotValid(String emailAddress) {
        return !EmailValidator.getInstance().isValid(emailAddress);
    }

}
