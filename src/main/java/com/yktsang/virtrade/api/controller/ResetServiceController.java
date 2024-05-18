/*
 * ResetServiceController.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.api.controller;

import com.yktsang.virtrade.api.jwt.JwtService;
import com.yktsang.virtrade.entity.*;
import com.yktsang.virtrade.request.ResetPortfolioRequest;
import com.yktsang.virtrade.response.ErrorResponse;
import com.yktsang.virtrade.response.SuccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The API controller for implementing <code>ResetService</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@RestController
public class ResetServiceController implements ResetService {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(ResetServiceController.class);
    /**
     * The JWT service.
     */
    @Autowired
    private JwtService jwtService;
    /**
     * The watch list repository.
     */
    @Autowired
    private WatchListRepository watchListRepo;
    /**
     * The trading transaction repository.
     */
    @Autowired
    private TradingTransactionRepository tradingTxnRepo;
    /**
     * The portfolio repository.
     */
    @Autowired
    private PortfolioRepository portRepo;
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
    public ResponseEntity<?> resetPortfolio(RequestEntity<ResetPortfolioRequest> req) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        if (req.hasBody() && Objects.nonNull(req.getBody())) {
            ResetPortfolioRequest actualReq = req.getBody();

            if (
                    Objects.isNull(actualReq.resetAllCurrencies())

                            // resetAllCurrencies = false then check currencyToReset
                            || (!actualReq.resetAllCurrencies()
                            && (Objects.isNull(actualReq.currencyToReset()) || actualReq.currencyToReset().isEmpty()))
            ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Validation failed"));
            }

            boolean resetAllCurrencies = actualReq.resetAllCurrencies();
            String currencyToReset = actualReq.currencyToReset();

            if (resetAllCurrencies) {
                logger.info("resetting portfolio for {}", tokenUser);
                // delete from watch list
                List<WatchList> watchListToDelete = watchListRepo.findByEmail(tokenUser);
                watchListRepo.deleteAll(watchListToDelete);
                logger.info("deleted watch list");
                // delete from trading transaction
                List<TradingTransaction> tradingTxnToDelete = tradingTxnRepo.findByEmail(tokenUser);
                tradingTxnRepo.deleteAll(tradingTxnToDelete);
                logger.info("deleted trading transactions");
                // delete from portfolio
                List<Portfolio> portToDelete = portRepo.findByEmail(tokenUser);
                portRepo.deleteAll(portToDelete);
                logger.info("deleted portfolios");
                // delete from bank account transaction
                List<BankAccountTransaction> bankTxnToDelete = bankAcctTxnRepo.findByEmail(tokenUser);
                bankAcctTxnRepo.deleteAll(bankTxnToDelete);
                logger.info("deleted bank transactions");
                // delete from bank account
                List<BankAccount> bankToDelete = bankAcctRepo.findByEmail(tokenUser);
                bankAcctRepo.deleteAll(bankToDelete);
                logger.info("deleted bank accounts");
                // delete from account transaction
                List<AccountTransaction> acctTxnToDelete = acctTxnRepo.findByEmail(tokenUser);
                acctTxnRepo.deleteAll(acctTxnToDelete);
                logger.info("deleted account transactions");
                // delete from account balance
                List<AccountBalance> acctBalToDelete = acctBalRepo.findByEmail(tokenUser);
                acctBalRepo.deleteAll(acctBalToDelete);
                logger.info("deleted account balances");
            } else {
                Set<IsoCurrency> activeCurrencies = isoDataRepo.findActiveIsoData(true).stream()
                        .map(c -> new IsoCurrency(c.getCurrencyAlphaCode(), c.getCurrencyName()))
                        .sorted().collect(Collectors.toCollection(LinkedHashSet::new));

                if (!activeCurrencies.contains(new IsoCurrency(actualReq.currencyToReset()))) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ErrorResponse("Currency not found"));
                }

                logger.info("resetting {} portfolio for {}", currencyToReset, tokenUser);
                // delete from watch list
                List<WatchList> watchListToDelete = watchListRepo.findByEmailAndCurrency(tokenUser, currencyToReset);
                watchListRepo.deleteAll(watchListToDelete);
                logger.info("deleted watch list");
                // delete from trading transaction
                List<TradingTransaction> tradingTxnToDelete = tradingTxnRepo.findByEmailAndCurrency(tokenUser, currencyToReset);
                tradingTxnRepo.deleteAll(tradingTxnToDelete);
                logger.info("deleted trading transactions");
                // delete from portfolio
                List<Portfolio> portToDelete = portRepo.findByEmailAndCurrency(tokenUser, currencyToReset);
                portRepo.deleteAll(portToDelete);
                logger.info("deleted portfolios");
                // delete from bank account transaction
                List<BankAccountTransaction> bankTxnToDelete = bankAcctTxnRepo.findByEmailAndCurrency(tokenUser, currencyToReset);
                bankAcctTxnRepo.deleteAll(bankTxnToDelete);
                logger.info("deleted bank transactions");
                // delete from bank account
                List<BankAccount> bankToDelete = bankAcctRepo.findByEmailAndCurrency(tokenUser, currencyToReset);
                bankAcctRepo.deleteAll(bankToDelete);
                logger.info("deleted bank accounts");
                // delete from account transaction
                List<AccountTransaction> acctTxnToDelete = acctTxnRepo.findByEmailAndCurrency(tokenUser, currencyToReset);
                acctTxnRepo.deleteAll(acctTxnToDelete);
                logger.info("deleted account transactions");
                // delete from account balance
                Optional<AccountBalance> acctBalOpt = acctBalRepo.findById(new AccountBalancePK(tokenUser, currencyToReset));
                if (acctBalOpt.isPresent()) {
                    AccountBalance acctBal = acctBalOpt.get();
                    acctBalRepo.delete(acctBal);
                    logger.info("deleted account balance");
                }
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new SuccessResponse("Portfolio is reset"));

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid request"));
        }
    }

}
