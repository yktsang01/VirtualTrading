/*
 * AccountBalanceServiceController.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.api.controller;

import com.yktsang.virtrade.api.jwt.JwtService;
import com.yktsang.virtrade.entity.*;
import com.yktsang.virtrade.request.DepositFundRequest;
import com.yktsang.virtrade.response.AccountBalanceResponse;
import com.yktsang.virtrade.response.ErrorResponse;
import com.yktsang.virtrade.response.SuccessResponse;
import com.yktsang.virtrade.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The API controller for implementing <code>AccountBalanceService</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@RestController
public class AccountBalanceServiceController implements AccountBalanceService {

    /**
     * The maximum account balance limit (1 trillion).
     */
    private static final BigDecimal AMOUNT_LIMIT_TRILLION = new BigDecimal("1000000000000");
    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(AccountBalanceServiceController.class);
    /**
     * The JWT service.
     */
    @Autowired
    private JwtService jwtService;
    /**
     * The account balance repository.
     */
    @Autowired
    private AccountBalanceRepository acctBalRepo;
    /**
     * The account transaction repository.
     */
    @Autowired
    private AccountTransactionRepository acctTxnRepo;
    /**
     * The ISO data repository.
     */
    @Autowired
    private IsoDataRepository isoDataRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> accountBalances(RequestEntity<Void> req, int page, int pageSize) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        Set<IsoCurrency> activeCurrencies = isoDataRepo.findActiveIsoData(true).stream()
                .map(c -> new IsoCurrency(c.getCurrencyAlphaCode(), c.getCurrencyName()))
                .sorted().collect(Collectors.toCollection(LinkedHashSet::new));

        GenericHolder holder = this.getAccountBalanceResults(tokenUser, "", page, Math.max(pageSize, 1), activeCurrencies);
        List<com.yktsang.virtrade.response.AccountBalance> balances =
                (List<com.yktsang.virtrade.response.AccountBalance>) holder.items();
        HttpHeaders respHeaderMap = holder.headers();

        if (balances.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(respHeaderMap).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).headers(respHeaderMap)
                    .body(new AccountBalanceResponse(balances));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> accountBalances(RequestEntity<Void> req, String currency) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        if (Objects.isNull(currency)
                || currency.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Validation failed"));
        }

        Set<IsoCurrency> activeCurrencies = isoDataRepo.findActiveIsoData(true).stream()
                .map(c -> new IsoCurrency(c.getCurrencyAlphaCode(), c.getCurrencyName()))
                .sorted().collect(Collectors.toCollection(LinkedHashSet::new));

        if (!activeCurrencies.contains(new IsoCurrency(currency))) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Currency not found"));
        }

        GenericHolder holder = this.getAccountBalanceResults(tokenUser, currency, 0, 1, activeCurrencies);
        List<com.yktsang.virtrade.response.AccountBalance> balances =
                (List<com.yktsang.virtrade.response.AccountBalance>) holder.items();
        HttpHeaders respHeaderMap = holder.headers();

        if (balances.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(respHeaderMap).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).headers(respHeaderMap)
                    .body(new AccountBalanceResponse(balances));
        }
    }

    /**
     * Returns the <code>GenericHolder</code> containing <code>AccountBalance</code>>.
     *
     * @param email            the email address
     * @param currency         the currency
     * @param page             the page number to retrieve
     * @param pageSize         the number of records to retrieve
     * @param activeCurrencies the active currencies
     * @return the GenericHolder
     */
    private GenericHolder getAccountBalanceResults(String email, String currency,
                                                   int page, int pageSize, Set<IsoCurrency> activeCurrencies) {
        List<com.yktsang.virtrade.entity.AccountBalance> dbBalances;
        if (currency.isEmpty()) {
            dbBalances = acctBalRepo.findByEmail(email);
        } else {
            dbBalances = acctBalRepo.findById(new AccountBalancePK(email, currency)).stream().toList();
        }
        for (com.yktsang.virtrade.entity.AccountBalance bal : dbBalances) {
            Integer dpNum = isoDataRepo.findMinorUnits(bal.getCurrency());
            bal.setDecimalPlacesToDisplay(dpNum);
        }
        List<com.yktsang.virtrade.response.AccountBalance> respBalances = dbBalances.stream()
                //filter with active currencies
                .filter(c -> activeCurrencies.contains(new IsoCurrency(c.getCurrency())))
                //map to response format
                .map(b -> new com.yktsang.virtrade.response.AccountBalance(b.getEmail(), b.getCurrency(),
                        b.getTradingAmount(), b.getNonTradingAmount(), b.getDecimalPlacesToDisplay()))
                .toList();

        Page<com.yktsang.virtrade.response.AccountBalance> respPage;
        if (page == 0) {
            respPage = (Page<com.yktsang.virtrade.response.AccountBalance>)
                    PaginationUtil.convertListToPage(respBalances, page, respBalances.isEmpty() ? 1 : respBalances.size());
        } else {
            respPage = (Page<com.yktsang.virtrade.response.AccountBalance>)
                    PaginationUtil.convertListToPage(respBalances, page, pageSize);
        }
        HttpHeaders respHeaderMap = PaginationUtil.populateResponseHeader(respPage.getTotalElements(),
                respPage.getTotalPages(), page, respPage.getPageable().getPageSize(),
                respPage.hasPrevious(), respPage.hasNext(), currency);

        return new GenericHolder(respPage.getContent(), respHeaderMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> depositFunds(RequestEntity<DepositFundRequest> req) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        if (req.hasBody() && Objects.nonNull(req.getBody())) {
            DepositFundRequest actualReq = req.getBody();

            if (
                    Objects.isNull(actualReq.currency())
                            || actualReq.currency().isEmpty()
                            || Objects.isNull(actualReq.depositAmount())

                            // depositAmount not positive (>0)
                            || actualReq.depositAmount().signum() != 1

                            // request depositAmount is greater than or equals to one trillion limit
                            || actualReq.depositAmount().compareTo(AMOUNT_LIMIT_TRILLION) >= 0
            ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Validation failed"));
            }

            Set<IsoCurrency> activeCurrencies = isoDataRepo.findActiveIsoData(true).stream()
                    .map(c -> new IsoCurrency(c.getCurrencyAlphaCode(), c.getCurrencyName()))
                    .sorted().collect(Collectors.toCollection(LinkedHashSet::new));

            if (!activeCurrencies.contains(new IsoCurrency(actualReq.currency()))) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Currency not found"));
            }

            String currency = actualReq.currency();
            BigDecimal amount = actualReq.depositAmount();

            Optional<AccountBalance> balanceOpt =
                    acctBalRepo.findById(new AccountBalancePK(tokenUser, currency));
            if (balanceOpt.isPresent()) {
                AccountBalance existingBalance = balanceOpt.get();
                BigDecimal newAmount = existingBalance.getNonTradingAmount().add(amount);
                // newAmount is less than one trillion limit
                if (newAmount.compareTo(AMOUNT_LIMIT_TRILLION) < 0) {
                    existingBalance.setNonTradingAmount(newAmount);
                    existingBalance.setLastUpdatedDateTime(LocalDateTime.now());
                    acctBalRepo.save(existingBalance);
                    logger.info("account balance updated");
                    AccountTransaction txn = new AccountTransaction(tokenUser, currency,
                            "Deposited " + currency + " "
                                    + new DecimalFormat("#,###.0000").format(amount));
                    acctTxnRepo.save(txn);
                    logger.info("account transaction created");

                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new SuccessResponse("Account balance updated"));
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(new ErrorResponse("New account balance will be above "
                                    + currency + " " + AMOUNT_LIMIT_TRILLION));
                }
            } else {
                AccountBalance balance = new AccountBalance(tokenUser,
                        currency, BigDecimal.ZERO, amount);
                acctBalRepo.save(balance);
                logger.info("account balance created");
                AccountTransaction txn = new AccountTransaction(tokenUser, currency,
                        "Deposited " + currency + " "
                                + new DecimalFormat("#,###.0000").format(amount));
                acctTxnRepo.save(txn);
                logger.info("account transaction created");

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new SuccessResponse("Account balance created"));
            }

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid request"));
        }
    }

}
