/*
 * BankAccountServiceController.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.api.controller;

import com.yktsang.virtrade.api.jwt.JwtService;
import com.yktsang.virtrade.entity.*;
import com.yktsang.virtrade.request.AddBankAccountRequest;
import com.yktsang.virtrade.response.BankAccountResponse;
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

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The API controller for implementing <code>BankAccountService</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@RestController
public class BankAccountServiceController implements BankAccountService {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(BankAccountServiceController.class);
    /**
     * The JWT service.
     */
    @Autowired
    private JwtService jwtService;
    /**
     * The bank account repository.
     */
    @Autowired
    private BankAccountRepository bankAcctRepo;
    /**
     * The bank account transaction repository.
     */
    @Autowired
    private BankAccountTransactionRepository bankAcctTxnRepo;
    /**
     * The ISO data repository.
     */
    @Autowired
    private IsoDataRepository isoDataRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> bankAccounts(RequestEntity<Void> req, int page, int pageSize) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        Set<IsoCurrency> activeCurrencies = isoDataRepo.findActiveIsoData(true).stream()
                .map(c -> new IsoCurrency(c.getCurrencyAlphaCode(), c.getCurrencyName()))
                .sorted().collect(Collectors.toCollection(LinkedHashSet::new));

        GenericHolder holder = this.getBankAccountResults(tokenUser, "", page, Math.max(pageSize, 1), activeCurrencies);
        List<com.yktsang.virtrade.response.BankAccount> banks =
                (List<com.yktsang.virtrade.response.BankAccount>) holder.items();
        HttpHeaders respHeaderMap = holder.headers();

        if (banks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(respHeaderMap).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).headers(respHeaderMap)
                    .body(new BankAccountResponse(banks));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> bankAccounts(RequestEntity<Void> req, String currency, int page, int pageSize) {
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

        GenericHolder holder = this.getBankAccountResults(tokenUser, currency, page, Math.max(pageSize, 1), activeCurrencies);
        List<com.yktsang.virtrade.response.BankAccount> banks =
                (List<com.yktsang.virtrade.response.BankAccount>) holder.items();
        HttpHeaders respHeaderMap = holder.headers();

        if (banks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(respHeaderMap).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).headers(respHeaderMap)
                    .body(new BankAccountResponse(banks));
        }
    }

    /**
     * Returns the <code>GenericHolder</code> containing <code>BankAccount</code>>.
     *
     * @param email            the email address
     * @param currency         the currency
     * @param page             the page number to retrieve
     * @param pageSize         the number of records to retrieve
     * @param activeCurrencies the active currencies
     * @return the GenericHolder
     */
    private GenericHolder getBankAccountResults(String email, String currency,
                                                int page, int pageSize, Set<IsoCurrency> activeCurrencies) {
        List<com.yktsang.virtrade.entity.BankAccount> dbBanks;
        if (currency.isEmpty()) {
            dbBanks = bankAcctRepo.findByEmail(email, true);
        } else {
            dbBanks = bankAcctRepo.findByEmailAndCurrency(email, currency, true);
        }
        List<com.yktsang.virtrade.response.BankAccount> respBanks = dbBanks.stream()
                //filter with active currencies
                .filter(c -> activeCurrencies.contains(new IsoCurrency(c.getCurrency())))
                //map to response format
                .map(b -> new com.yktsang.virtrade.response.BankAccount(b.getBankAccountId(),
                        b.getEmail(), b.getCurrency(), b.getBankName(), b.getBankAccountNumber()))
                .toList();

        Page<com.yktsang.virtrade.response.BankAccount> respPage;
        if (page == 0) {
            respPage = (Page<com.yktsang.virtrade.response.BankAccount>)
                    PaginationUtil.convertListToPage(respBanks, page, respBanks.isEmpty() ? 1 : respBanks.size());
        } else {
            respPage = (Page<com.yktsang.virtrade.response.BankAccount>)
                    PaginationUtil.convertListToPage(respBanks, page, pageSize);
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
    public ResponseEntity<?> addBankAccount(RequestEntity<AddBankAccountRequest> req) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        if (req.hasBody() && Objects.nonNull(req.getBody())) {
            AddBankAccountRequest actualReq = req.getBody();

            if (
                    Objects.isNull(actualReq.currency())
                            || actualReq.currency().isEmpty()
                            || Objects.isNull(actualReq.bankName())
                            || actualReq.bankName().isEmpty()
                            || Objects.isNull(actualReq.bankAccountNumber())
                            || actualReq.bankAccountNumber().isEmpty()
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
            String bankName = actualReq.bankName();
            String bankAccountNumber = actualReq.bankAccountNumber();

            BankAccount bankAccount = new BankAccount(tokenUser, currency, bankName, bankAccountNumber);
            bankAcctRepo.save(bankAccount);
            logger.info("bank account created");
            BankAccountTransaction txn = new BankAccountTransaction(tokenUser, currency,
                    "Added bank " + bankName
                            + " with account number " + bankAccountNumber
                            + " for currency " + currency);
            bankAcctTxnRepo.save(txn);
            logger.info("bank transaction created");

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new SuccessResponse("Bank account created"));

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid request"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> obsoleteBankAccount(RequestEntity<Void> req, BigInteger bankAccountId) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        if (Objects.isNull(bankAccountId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Validation failed"));
        }

        Optional<BankAccount> baOpt = bankAcctRepo.findById(bankAccountId);
        if (baOpt.isPresent()) {
            BankAccount ba = baOpt.get();

            if (!ba.getEmail().equals(tokenUser)) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(new ErrorResponse("Bank account ID not belong to caller"));
            }

            if (ba.isInUse()) {
                ba.setInUse(false);
                ba.setLastUpdatedDateTime(LocalDateTime.now());
                bankAcctRepo.save(ba);
                logger.info("bank account marked not in use");
                BankAccountTransaction txn = new BankAccountTransaction(tokenUser, ba.getCurrency(),
                        "Marked not in use for bank " + ba.getBankName()
                                + " with account number " + ba.getBankAccountNumber()
                                + " for currency " + ba.getCurrency());
                bankAcctTxnRepo.save(txn);
                logger.info("bank transaction created");
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new SuccessResponse("Bank account marked not in use"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
            }

        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Bank account not found"));
        }
    }

}
