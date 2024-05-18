/*
 * JwtService.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.api.jwt;

import com.yktsang.virtrade.entity.Account;
import com.yktsang.virtrade.entity.AccountRepository;
import com.yktsang.virtrade.util.DateTimeUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

/**
 * The JWT service.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Service
public class JwtService {

    /**
     * The issuer of the JWT.
     */
    private static final String ISSUER = "yktsang.com";
    /**
     * The default JWT expiry in seconds.
     */
    private static final long TOKEN_EXPIRES_SECONDS = 1800L; // 30 minutes
    /**
     * The secret key for signing and verifying.
     */
    private static final String SECRET =
            "VmlydHVhbFRyYWRpbmdJc0Nvb2xBbmRBd2Vzb21lV2ViQXBwbGljYXRpb25TaW11bGF0aW5nT25saW5lU3RvY2tUcmFkaW5n";

    /**
     * The account repository.
     */
    @Autowired
    private AccountRepository accountRepo;

    /**
     * Returns the JWT, given the username.
     *
     * @param username the username
     * @return the JWT
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return this.createToken(claims, username);
    }

    /**
     * Creates the JWT.
     * Includes the following JWT standard claim names:
     * sub = subject
     * iss = issuer
     * jti = JWT ID
     * iat = issue at
     * exp = expiry
     * Uses HS256 (HMAC + SHA256) algorithm to sign data.
     *
     * @param claims   the map of custom data
     * @param username the username
     * @return the JWT
     */
    private String createToken(Map<String, Object> claims, String username) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusSeconds(TOKEN_EXPIRES_SECONDS);
        return Jwts.builder()
                .claims(claims)
                .subject(username) // sub
                .issuer(ISSUER) //iss
                .id(UUID.randomUUID().toString()) // jti
                .issuedAt(DateTimeUtil.toDate(now)) //iat
                .expiration(DateTimeUtil.toDate(expiry)) // exp
                .signWith(this.getSignKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Returns the signing key.
     *
     * @return the signing key
     */
    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Returns true if the JWT is valid, false otherwise.
     * Checks whether the username of the JWT matches the username in the user details,
     * the username has an active account or not
     * and the JWT is not expired.
     *
     * @param token       the JWT
     * @param userDetails the user details
     * @return true if the JWT is valid, false otherwise
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = this.extractUsername(token);
        return username.equals(userDetails.getUsername())
                && this.isAccountActive(username)
                && !this.isTokenExpired(token);
    }

    /**
     * Returns true if the username's account is active, false otherwise.
     *
     * @param username the username
     * @return true if the username's account is active, false otherwise
     */
    private boolean isAccountActive(String username) {
        boolean activeAccount = false;
        Optional<Account> accountOpt = accountRepo.findById(username);
        if (accountOpt.isPresent()) {
            activeAccount = accountOpt.get().isActive();
        }
        return activeAccount;
    }

    /**
     * Returns true if the JWT is expired, false otherwise.
     * Checks whether the expiration date of the JWT is before the current date.
     *
     * @param token the JWT
     * @return true if the JWT is expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return this.extractExpiration(token).before(new Date());
    }

    /**
     * Returns the username from the HTTP headers.
     *
     * @param headers the HTTP headers
     * @return the username
     */
    public String extractUsernameFromHeaders(HttpHeaders headers) {
        String tokenUser = null;
        if (headers.containsKey("Authorization")) {
            String authHeader = Objects.requireNonNull(headers.get("Authorization")).get(0);
            String token = authHeader.substring(7);
            tokenUser = this.extractUsername(token);
        }
        return tokenUser;
    }

    /**
     * Returns the username of the JWT.
     *
     * @param token the JWT
     * @return the username
     */
    public String extractUsername(String token) {
        return this.extractClaim(token, Claims::getSubject);
    }

    /**
     * Returns the expiration date of the JWT.
     *
     * @param token the JWT
     * @return the expiration date
     */
    public Date extractExpiration(String token) {
        return this.extractClaim(token, Claims::getExpiration);
    }

    /**
     * Returns the claim value from the JWT standard claim names.
     *
     * @param token          the JWT
     * @param claimsResolver the function to get the claim value
     * @param <T>            the type of the claim value
     * @return the claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Returns the claim with all the JWT standard claim names.
     *
     * @param token the JWT
     * @return the claim
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(this.getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
