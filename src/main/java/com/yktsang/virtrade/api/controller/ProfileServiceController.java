/*
 * ProfileServiceController.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.api.controller;

import com.yktsang.virtrade.api.jwt.JwtService;
import com.yktsang.virtrade.entity.*;
import com.yktsang.virtrade.request.DeactivateAccountRequest;
import com.yktsang.virtrade.request.ProfileRequest;
import com.yktsang.virtrade.request.RegistrationRequest;
import com.yktsang.virtrade.response.*;
import com.yktsang.virtrade.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * The API controller for implementing <code>ProfileService</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@RestController
public class ProfileServiceController implements ProfileService {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(ProfileServiceController.class);
    /**
     * The JWT service.
     */
    @Autowired
    private JwtService jwtService;
    /**
     * The trader repository.
     */
    @Autowired
    private TraderRepository traderRepo;
    /**
     * The account repository.
     */
    @Autowired
    private AccountRepository accountRepo;

    /**
     * Returns the string array containing the risk tolerance levels.
     *
     * @return the string array
     */
    private String[] riskToleranceArray() {
        RiskToleranceLevel[] riskAry = RiskToleranceLevel.values();
        String[] strAry = new String[riskAry.length]; // length=3
        for (int i = 0; i < riskAry.length; i++) {
            strAry[i] = riskAry[i].name();
        }
        return strAry;
    }

    /**
     * Returns true if date of birth is either invalid or today and after, false otherwise.
     *
     * @param year  the year of birth
     * @param month the month of birth
     * @param day   the day of birth
     * @return true if date of birth is either invalid or today and after, false otherwise
     */
    private boolean isDateOfBirthInvalidOrTodayAndAfter(int year, int month, int day) {
        try {
            LocalDate dob = LocalDate.of(year, month, day);
            LocalDate today = LocalDate.now();
            return !dob.isBefore(today);
        } catch (DateTimeException dte) {
            // date is invalid
            return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> register(RequestEntity<RegistrationRequest> req) {
        if (req.hasBody() && Objects.nonNull(req.getBody())) {
            RegistrationRequest actualReq = req.getBody();

            if (
                    Objects.isNull(actualReq.getEmail())
                            || actualReq.getEmail().isEmpty()
                            || Objects.isNull(actualReq.getPassword())
                            || actualReq.getPassword().isEmpty()
                            || Objects.isNull(actualReq.getConfirmPassword())
                            || actualReq.getConfirmPassword().isEmpty()
                            || Objects.isNull(actualReq.getFullName())
                            || actualReq.getFullName().isEmpty()
                            || Objects.isNull(actualReq.getBirthYear())
                            || Objects.isNull(actualReq.getBirthMonth())
                            || Objects.isNull(actualReq.getBirthDay())
                            || Objects.isNull(actualReq.isHideDateOfBirth())
                            || Objects.isNull(actualReq.getRiskTolerance())
                            || actualReq.getRiskTolerance().isEmpty()
                            || Objects.isNull(actualReq.isAutoTransferToBank())
                            || Objects.isNull(actualReq.isAllowReset())

                            || !actualReq.getPassword().equals(actualReq.getConfirmPassword())

                            || SecurityUtil.isEmailNotValid(actualReq.getEmail())

                            || actualReq.getBirthYear() < 1 // birthYear not null
                            || (actualReq.getBirthMonth() < 1 || actualReq.getBirthMonth() > 12) // birthMonth not null
                            || (actualReq.getBirthDay() < 1 || actualReq.getBirthDay() > 31) // birthDay not null

                            // check date of birth is valid (e.g. before today)
                            || this.isDateOfBirthInvalidOrTodayAndAfter(actualReq.getBirthYear(), actualReq.getBirthMonth(), actualReq.getBirthDay())

                            // check risk tolerance is "low", "medium", or "high"
                            || Arrays.stream(this.riskToleranceArray()).noneMatch(actualReq.getRiskTolerance()::equalsIgnoreCase)
            ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Validation failed"));
            }

            String email = actualReq.getEmail();
            String rawPassword = actualReq.getPassword();
            String fullName = actualReq.getFullName();
            LocalDate dateOfBirth = LocalDate.of(actualReq.getBirthYear(), actualReq.getBirthMonth(), actualReq.getBirthDay());
            boolean hideDateOfBirth = actualReq.isHideDateOfBirth();
            RiskToleranceLevel riskTolerance = RiskToleranceLevel.valueOf(actualReq.getRiskTolerance().toUpperCase());
            boolean autoTransferToBank = actualReq.isAutoTransferToBank();
            boolean allowReset = actualReq.isAllowReset();

            //insert trader
            Trader trader = new Trader(email, fullName, dateOfBirth, hideDateOfBirth,
                    riskTolerance, autoTransferToBank, allowReset);
            traderRepo.save(trader);
            logger.info("trader created");

            //insert account
            Account account = new Account(email, rawPassword);
            accountRepo.save(account);
            logger.info("account created");

            String token = jwtService.generateToken(email);
            logger.info("JWT generated after registration");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new JwtResponse(token));

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid request"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> privateProfile(RequestEntity<Void> req) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        Optional<Trader> traderOpt = traderRepo.findById(tokenUser);
        Optional<Account> accountOpt = accountRepo.findById(tokenUser);

        if (traderOpt.isPresent() && accountOpt.isPresent()) {
            Trader trader = traderOpt.get();
            TraderProfile traderPrf = new TraderProfile(trader.getEmail(), trader.getFullName(),
                    trader.getDateOfBirth(), trader.isHideDateOfBirth(), trader.getRiskTolerance(),
                    trader.isAutoTransferToBank(), trader.isAllowReset(), trader.getCreationDateTime());
            Account account = accountOpt.get();
            AccountProfile accountPrf = new AccountProfile(account.getEmail(),
                    account.isAdmin(), account.hasRequestAdmin());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new CompleteProfileResponse(traderPrf, accountPrf));

        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Account not found"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> publicProfile(RequestEntity<Void> req) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        Optional<Trader> traderOpt = traderRepo.findById(tokenUser);

        if (traderOpt.isPresent()) {
            Trader trader = traderOpt.get();
            TraderProfile traderPrf = new TraderProfile(trader.getEmail(), trader.getFullName(),
                    trader.getDateOfBirth(), trader.isHideDateOfBirth(), trader.getRiskTolerance(),
                    trader.isAutoTransferToBank(), trader.isAllowReset(), trader.getCreationDateTime());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ProfileResponse(traderPrf));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Account not found"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> updateProfile(RequestEntity<ProfileRequest> req) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        if (req.hasBody() && Objects.nonNull(req.getBody())) {
            ProfileRequest actualReq = req.getBody();

            if (
                    Objects.isNull(actualReq.getEmail())
                            || actualReq.getEmail().isEmpty()
                            || Objects.isNull(actualReq.getFullName())
                            || actualReq.getFullName().isEmpty()
                            || Objects.isNull(actualReq.getBirthYear())
                            || Objects.isNull(actualReq.getBirthMonth())
                            || Objects.isNull(actualReq.getBirthDay())
                            || Objects.isNull(actualReq.isHideDateOfBirth())
                            || Objects.isNull(actualReq.getRiskTolerance())
                            || actualReq.getRiskTolerance().isEmpty()
                            || Objects.isNull(actualReq.isAutoTransferToBank())
                            || Objects.isNull(actualReq.isAllowReset())

                            || SecurityUtil.isEmailNotValid(actualReq.getEmail())

                            || actualReq.getBirthYear() < 1 // birthYear not null
                            || (actualReq.getBirthMonth() < 1 || actualReq.getBirthDay() > 12) // birthMonth not null
                            || (actualReq.getBirthDay() < 1 || actualReq.getBirthDay() > 31) // birthDay not null

                            // check date of birth is valid (e.g. before today)
                            || this.isDateOfBirthInvalidOrTodayAndAfter(actualReq.getBirthYear(), actualReq.getBirthMonth(), actualReq.getBirthDay())

                            // check risk tolerance is "low", "medium" or "high"
                            || Arrays.stream(this.riskToleranceArray()).noneMatch(actualReq.getRiskTolerance()::equalsIgnoreCase)

                            // check token user against email in request
                            || !tokenUser.equals(actualReq.getEmail())
            ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Validation failed"));
            }

            String fullName = actualReq.getFullName();
            LocalDate dateOfBirth = LocalDate.of(actualReq.getBirthYear(), actualReq.getBirthMonth(), actualReq.getBirthDay());
            boolean hideDateOfBirth = actualReq.isHideDateOfBirth();
            RiskToleranceLevel riskTolerance = RiskToleranceLevel.valueOf(actualReq.getRiskTolerance().toUpperCase());
            boolean autoTransferToBank = actualReq.isAutoTransferToBank();
            boolean allowReset = actualReq.isAllowReset();

            Optional<Trader> traderOpt = traderRepo.findById(tokenUser);
            if (traderOpt.isPresent()) {
                Trader trader = traderOpt.get();
                trader.setFullName(fullName);
                trader.setDateOfBirth(dateOfBirth);
                trader.setHideDateOfBirth(hideDateOfBirth);
                trader.setRiskTolerance(riskTolerance);
                trader.setAutoTransferToBank(autoTransferToBank);
                trader.setAllowReset(allowReset);
                trader.setLastUpdatedDateTime(LocalDateTime.now());
                traderRepo.save(trader);
                logger.info("trader updated");

                TraderProfile traderPrf = new TraderProfile(trader.getEmail(), trader.getFullName(),
                        trader.getDateOfBirth(), trader.isHideDateOfBirth(), trader.getRiskTolerance(),
                        trader.isAutoTransferToBank(), trader.isAllowReset(), trader.getCreationDateTime());

                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ProfileResponse(traderPrf));

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
    public ResponseEntity<?> deactivateProfile(RequestEntity<DeactivateAccountRequest> req) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        if (req.hasBody() && Objects.nonNull(req.getBody())) {
            DeactivateAccountRequest actualReq = req.getBody();

            if (
                    Objects.isNull(actualReq.deactivate())

                            //if deactivate indicator is true, check the reason
                            || (actualReq.deactivate()
                            && (Objects.isNull(actualReq.deactivationReason()) || actualReq.deactivationReason().isEmpty()))
            ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Validation failed"));
            }

            if (!actualReq.deactivate()) {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
            }

            //deactivate indicator is true
            String reason = actualReq.deactivationReason();

            Optional<Trader> traderOpt = traderRepo.findById(tokenUser);
            Optional<Account> accountOpt = accountRepo.findById(tokenUser);

            if (traderOpt.isPresent() && accountOpt.isPresent()) {
                Trader trader = traderOpt.get();
                Account account = accountOpt.get();
                // revoke admin access right
                account.setAdmin(false);
                account.setAdminApproveBy(null);
                account.setAdminApprovalDateTime(null);
                account.setAdminRequestDateTime(null);
                // deactivate
                account.setActive(false);
                account.setDeactivationReason(reason);
                account.setDeactivationDateTime(LocalDateTime.now());
                trader.setDeactivationDateTime(LocalDateTime.now());
                //update trader
                traderRepo.save(trader);
                logger.info("trader deactivated");
                //update account
                accountRepo.save(account);
                logger.info("account deactivated");

                return ResponseEntity.status(HttpStatus.OK)
                        .body(new SuccessResponse("Account deactivated"));

            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Account not found"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid request"));
        }
    }

}
