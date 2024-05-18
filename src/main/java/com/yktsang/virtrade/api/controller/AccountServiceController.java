/*
 * AccountServiceController.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.api.controller;

import com.yktsang.virtrade.api.jwt.JwtService;
import com.yktsang.virtrade.entity.Account;
import com.yktsang.virtrade.entity.AccountRepository;
import com.yktsang.virtrade.request.*;
import com.yktsang.virtrade.response.*;
import com.yktsang.virtrade.util.PaginationUtil;
import com.yktsang.virtrade.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The API controller for implementing <code>AccountService</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@RestController
public class AccountServiceController implements AccountService {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(AccountServiceController.class);
    /**
     * The JWT service.
     */
    @Autowired
    private JwtService jwtService;
    /**
     * The authentication manager.
     */
    @Autowired
    private AuthenticationManager authManager;
    /**
     * The account repository.
     */
    @Autowired
    private AccountRepository accountRepo;
    /**
     * The admin email address.
     */
    @Value("${admin.email}")
    private String adminEmail; // from application.properties

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkAdminLastUpdated() {
        boolean passwordAlreadyReset = false;
        Optional<Account> accountOpt = accountRepo.findById(adminEmail);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            if (Objects.nonNull(account.getLastUpdatedDateTime())) {
                passwordAlreadyReset = true;
            }
        }
        return passwordAlreadyReset;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasAdminRight(String username) {
        boolean admin = false;
        Optional<Account> acctOpt = accountRepo.findById(username);
        if (acctOpt.isPresent()) {
            Account acct = acctOpt.get();
            admin = acct.isAdmin();
        }
        return admin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> login(RequestEntity<AuthRequest> req) {
        if (req.hasBody() && Objects.nonNull(req.getBody())) {
            AuthRequest actualReq = req.getBody();

            if (
                    Objects.isNull(actualReq.getUsername())
                            || actualReq.getUsername().isEmpty()
                            || Objects.isNull(actualReq.getPassword())
                            || actualReq.getPassword().isEmpty()
                            || Objects.isNull(actualReq.isAdminLogin())

                            || SecurityUtil.isEmailNotValid(actualReq.getUsername())
            ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Validation failed"));
            }

            String username = actualReq.getUsername();
            String password = actualReq.getPassword();
            boolean adminLogin = actualReq.isAdminLogin();

            try {
                Authentication authentication = authManager.authenticate(
                        new UsernamePasswordAuthenticationToken(username, password));
                if (authentication.isAuthenticated()) {
                    // check for admin access right for admin login
                    if (adminLogin && !this.hasAdminRight(username)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(new ErrorResponse("No admin access for account"));
                    }
                    String token = jwtService.generateToken(username);
                    logger.info("JWT generated");
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new JwtResponse(token));
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new ErrorResponse(""));
                }
            } catch (AuthenticationException ae) {
                Optional<Account> accountOpt = accountRepo.findById(username);
                if (accountOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ErrorResponse("Account not found"));
                } else if (!accountOpt.get().isActive()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new ErrorResponse("Account deactivated"));
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new ErrorResponse("Credentials not match"));
                }
            }

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid request"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> resetPassword(RequestEntity<ResetPasswordRequest> req) {
        if (req.hasBody() && Objects.nonNull(req.getBody())) {
            ResetPasswordRequest actualReq = req.getBody();

            if (
                    Objects.isNull(actualReq.username())
                            || actualReq.username().isEmpty()
                            || Objects.isNull(actualReq.password())
                            || actualReq.password().isEmpty()
                            || Objects.isNull(actualReq.confirmPassword())
                            || actualReq.confirmPassword().isEmpty()

                            || !actualReq.password().equals(actualReq.confirmPassword())

                            || SecurityUtil.isEmailNotValid(actualReq.username())
            ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Validation failed"));
            }

            String email = actualReq.username();
            String newRawPassword = actualReq.password();

            Optional<Account> accountOpt = accountRepo.findById(email);
            if (accountOpt.isPresent()) {
                Account account = accountOpt.get();
                account.setPassword(newRawPassword);
                //re-activate if inactive
                if (!account.isActive()) {
                    account.setActive(true);
                    account.setDeactivationReason(null);
                    account.setDeactivationDateTime(null);
                }
                account.setLastUpdatedDateTime(LocalDateTime.now());
                accountRepo.save(account);
                logger.info("account updated, password is reset");
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new SuccessResponse("Account updated, password is reset"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Account not found"));
            }

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid request"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> updateAccount(RequestEntity<ResetPasswordRequest> req) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        if (req.hasBody() && Objects.nonNull(req.getBody())) {
            ResetPasswordRequest actualReq = req.getBody();

            if (
                    Objects.isNull(actualReq.username())
                            || actualReq.username().isEmpty()
                            || Objects.isNull(actualReq.password())
                            || actualReq.password().isEmpty()
                            || Objects.isNull(actualReq.confirmPassword())
                            || actualReq.confirmPassword().isEmpty()

                            || !actualReq.password().equals(actualReq.confirmPassword())

                            || SecurityUtil.isEmailNotValid(actualReq.username())

                            // check token user against email in request
                            || !tokenUser.equals(actualReq.username())
            ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Validation failed"));
            }

            String email = actualReq.username();
            String newRawPassword = actualReq.password();

            Optional<Account> accountOpt = accountRepo.findById(email);
            if (accountOpt.isPresent()) {
                Account account = accountOpt.get();
                account.setPassword(newRawPassword);
                account.setLastUpdatedDateTime(LocalDateTime.now());
                accountRepo.save(account);
                logger.info("account updated, password is reset");
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new SuccessResponse("Account updated, password is reset"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Account not found"));
            }

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid request"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> requestAdminAccess(RequestEntity<RequestAdminAccessRequest> req) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        if (req.hasBody() && Objects.nonNull(req.getBody())) {
            RequestAdminAccessRequest actualReq = req.getBody();

            if (Objects.isNull(actualReq.adminAccessRequest())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Validation failed"));
            }

            boolean requestAdmin = actualReq.adminAccessRequest();

            Optional<Account> accountOpt = accountRepo.findById(tokenUser);
            if (accountOpt.isPresent()) {
                if (requestAdmin) {
                    Account account = accountOpt.get();
                    account.setAdminRequestDateTime(LocalDateTime.now());
                    accountRepo.save(account);
                    logger.info("account requested admin access");
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new SuccessResponse("Account requested admin access"));
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Account not found"));
            }

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid request"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> adminRequests(RequestEntity<Void> req, int page, int pageSize) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        if (this.hasAdminRight(tokenUser)) {
            GenericHolder holder = this.getAdminRequestResults(page, Math.max(pageSize, 1));
            List<AdminAccessRequest> adminRequests = (List<AdminAccessRequest>) holder.items();
            HttpHeaders respHeaderMap = holder.headers();

            if (adminRequests.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(respHeaderMap).build();
            } else {
                return ResponseEntity.status(HttpStatus.OK).headers(respHeaderMap)
                        .body(new AdminRequestResponse(adminRequests));
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("No admin access for account"));
        }
    }

    /**
     * Returns the <code>GenericHolder</code> containing <code>AdminAccessRequest</code>.
     *
     * @param page     the page number to retrieve
     * @param pageSize the number of records to retrieve
     * @return the GenericHolder
     */
    private GenericHolder getAdminRequestResults(int page, int pageSize) {
        List<Account> accounts = accountRepo.findMembersRequestedAdminAccess(false);
        List<AdminAccessRequest> adminRequests = accounts.stream()
                // map to response format
                .map(account -> new AdminAccessRequest(account.getEmail(),
                        account.getCreationDateTime(),
                        account.getAdminRequestDateTime()))
                .toList();

        Page<AdminAccessRequest> respPage;
        if (page == 0) {
            respPage = (Page<AdminAccessRequest>)
                    PaginationUtil.convertListToPage(adminRequests, page, adminRequests.isEmpty() ? 1 : adminRequests.size());
        } else {
            respPage = (Page<AdminAccessRequest>)
                    PaginationUtil.convertListToPage(adminRequests, page, pageSize);
        }
        HttpHeaders respHeaderMap = PaginationUtil.populateResponseHeader(respPage.getTotalElements(),
                respPage.getTotalPages(), page, respPage.getPageable().getPageSize(),
                respPage.hasPrevious(), respPage.hasNext(), "");

        return new GenericHolder(respPage.getContent(), respHeaderMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> adminAccesses(RequestEntity<Void> req, int page, int pageSize) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        if (this.hasAdminRight(tokenUser)) {
            GenericHolder holder = this.getAdminAccessResults(page, Math.max(pageSize, 1));
            List<AdminAccess> adminAccesses = (List<AdminAccess>) holder.items();
            HttpHeaders respHeaderMap = holder.headers();

            if (adminAccesses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(respHeaderMap).build();
            } else {
                return ResponseEntity.status(HttpStatus.OK).headers(respHeaderMap)
                        .body(new AdminAccessResponse(adminAccesses));
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("No admin access for account"));
        }
    }

    /**
     * Returns the <code>GenericHolder</code> containing <code>AdminAccess</code>.
     *
     * @param page     the page number to retrieve
     * @param pageSize the number of records to retrieve
     * @return the GenericHolder
     */
    private GenericHolder getAdminAccessResults(int page, int pageSize) {
        List<Account> accounts = accountRepo.findMembersWithAdminAccess(true, adminEmail);
        List<AdminAccess> adminAccesses = accounts.stream()
                // map to response format
                .map(account -> new AdminAccess(account.getEmail(), account.getCreationDateTime(),
                        account.getAdminRequestDateTime(), account.getAdminApprovalDateTime(),
                        account.getAdminApproveBy()))
                .toList();

        Page<AdminAccess> respPage;
        if (page == 0) {
            respPage = (Page<AdminAccess>)
                    PaginationUtil.convertListToPage(adminAccesses, page, adminAccesses.isEmpty() ? 1 : adminAccesses.size());
        } else {
            respPage = (Page<AdminAccess>)
                    PaginationUtil.convertListToPage(adminAccesses, page, pageSize);
        }
        HttpHeaders respHeaderMap = PaginationUtil.populateResponseHeader(respPage.getTotalElements(),
                respPage.getTotalPages(), page, respPage.getPageable().getPageSize(),
                respPage.hasPrevious(), respPage.hasNext(), "");

        return new GenericHolder(respPage.getContent(), respHeaderMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> grantAdminAccesses(RequestEntity<GrantAdminAccessRequest> req) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        if (this.hasAdminRight(tokenUser)) {
            if (req.hasBody() && Objects.nonNull(req.getBody())) {
                GrantAdminAccessRequest actualReq = req.getBody();

                if (Objects.isNull(actualReq.emailsToGrant())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ErrorResponse("Validation failed"));
                }

                List<String> emailsToGrant = actualReq.emailsToGrant();

                if (!emailsToGrant.isEmpty()) {
                    List<Account> adminRequests = accountRepo.findMembersRequestedAdminAccess(false);
                    List<Account> accountsToGrant = adminRequests.stream()
                            .filter(account -> emailsToGrant.contains(account.getEmail())
                                    && !account.getEmail().equals(tokenUser)) // cannot grant self
                            .toList();
                    logger.info("something to grant? {}", accountsToGrant.size());

                    for (Account a : accountsToGrant) {
                        a.setAdmin(true);
                        a.setAdminApproveBy(tokenUser);
                        a.setAdminApprovalDateTime(LocalDateTime.now());
                        accountRepo.save(a);
                        logger.info("granted {}", a.getEmail());
                    }

                    if (accountsToGrant.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
                    } else {
                        return ResponseEntity.status(HttpStatus.OK)
                                .body(new SuccessResponse("Successfully granted "
                                        + accountsToGrant.size()));
                    }

                } else {
                    return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
                }

            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Invalid request"));
            }

        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("No admin access for account"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> revokeAdminAccesses(RequestEntity<RevokeAdminAccessRequest> req) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        if (this.hasAdminRight(tokenUser)) {
            if (req.hasBody() && Objects.nonNull(req.getBody())) {
                RevokeAdminAccessRequest actualReq = req.getBody();

                if (Objects.isNull(actualReq.emailsToRevoke())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ErrorResponse("Validation failed"));
                }

                List<String> emailsToRevoke = actualReq.emailsToRevoke();

                if (!emailsToRevoke.isEmpty()) {
                    List<Account> adminAccesses =
                            accountRepo.findMembersWithAdminAccess(true, adminEmail);
                    List<Account> accountsToRevoke = adminAccesses.stream()
                            .filter(account -> emailsToRevoke.contains(account.getEmail())
                                    && !account.getEmail().equals(tokenUser)) // cannot revoke self
                            .toList();
                    logger.info("something to revoke? {}", accountsToRevoke.size());

                    for (Account a : accountsToRevoke) {
                        a.setAdmin(false);
                        a.setAdminApproveBy(null);
                        a.setAdminApprovalDateTime(null);
                        a.setAdminRequestDateTime(null);
                        accountRepo.save(a);
                        logger.info("revoked {}", a.getEmail());
                    }

                    if (accountsToRevoke.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
                    } else {
                        return ResponseEntity.status(HttpStatus.OK)
                                .body(new SuccessResponse("Successfully revoked "
                                        + accountsToRevoke.size()));
                    }

                } else {
                    return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
                }

            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Invalid request"));
            }

        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("No admin access for account"));
        }
    }

}
