/*
 * AccountTransactionServiceController.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.api.controller;

import com.yktsang.virtrade.api.jwt.JwtService;
import com.yktsang.virtrade.entity.BankAccountTransactionRepository;
import com.yktsang.virtrade.entity.IsoCurrency;
import com.yktsang.virtrade.entity.IsoDataRepository;
import com.yktsang.virtrade.response.BankAccountTransactionResponse;
import com.yktsang.virtrade.response.ErrorResponse;
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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The API controller for implementing <code>BankAccountTransactionService</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@RestController
public class BankAccountTransactionServiceController implements BankAccountTransactionService {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(BankAccountTransactionServiceController.class);
    /**
     * The JWT service.
     */
    @Autowired
    private JwtService jwtService;
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
    public ResponseEntity<?> bankAccountTransactions(RequestEntity<Void> req, int page, int pageSize) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        Set<IsoCurrency> activeCurrencies = isoDataRepo.findActiveIsoData(true).stream()
                .map(c -> new IsoCurrency(c.getCurrencyAlphaCode(), c.getCurrencyName()))
                .sorted().collect(Collectors.toCollection(LinkedHashSet::new));

        GenericHolder holder = this.getBankAccountTransactionResults(tokenUser, "", page, Math.max(pageSize, 1), activeCurrencies);
        List<com.yktsang.virtrade.response.BankAccountTransaction> bankTxns =
                (List<com.yktsang.virtrade.response.BankAccountTransaction>) holder.items();
        HttpHeaders respHeaderMap = holder.headers();

        if (bankTxns.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(respHeaderMap).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).headers(respHeaderMap)
                    .body(new BankAccountTransactionResponse(bankTxns));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> bankAccountTransactions(RequestEntity<Void> req, String currency, int page, int pageSize) {
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

        GenericHolder holder = this.getBankAccountTransactionResults(tokenUser, currency, page, Math.max(pageSize, 1), activeCurrencies);
        List<com.yktsang.virtrade.response.BankAccountTransaction> bankTxns =
                (List<com.yktsang.virtrade.response.BankAccountTransaction>) holder.items();
        HttpHeaders respHeaderMap = holder.headers();

        if (bankTxns.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(respHeaderMap).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).headers(respHeaderMap)
                    .body(new BankAccountTransactionResponse(bankTxns));
        }
    }

    /**
     * Returns the <code>GenericHolder</code> containing <code>BankAccountTransaction</code>>.
     *
     * @param email            the email address
     * @param currency         the currency
     * @param page             the page number to retrieve
     * @param pageSize         the number of records to retrieve
     * @param activeCurrencies the active currencies
     * @return the GenericHolder
     */
    private GenericHolder getBankAccountTransactionResults(String email, String currency,
                                                           int page, int pageSize, Set<IsoCurrency> activeCurrencies) {
        List<com.yktsang.virtrade.entity.BankAccountTransaction> dbBankAcctTxns;
        if (currency.isEmpty()) {
            dbBankAcctTxns = bankAcctTxnRepo.findByEmail(email);
        } else {
            dbBankAcctTxns = bankAcctTxnRepo.findByEmailAndCurrency(email, currency);
        }
        List<com.yktsang.virtrade.response.BankAccountTransaction> respBankAcctTxns = dbBankAcctTxns.stream()
                //filter with active currencies
                .filter(c -> activeCurrencies.contains(new IsoCurrency(c.getCurrency())))
                //map to response format
                .map(bat -> new com.yktsang.virtrade.response.BankAccountTransaction(bat.getEmail(),
                        bat.getCurrency(), bat.getTransactionDateTime(), bat.getTransactionDescription()))
                .toList();

        Page<com.yktsang.virtrade.response.BankAccountTransaction> respPage;
        if (page == 0) {
            respPage = (Page<com.yktsang.virtrade.response.BankAccountTransaction>)
                    PaginationUtil.convertListToPage(respBankAcctTxns, page, respBankAcctTxns.isEmpty() ? 1 : respBankAcctTxns.size());
        } else {
            respPage = (Page<com.yktsang.virtrade.response.BankAccountTransaction>)
                    PaginationUtil.convertListToPage(respBankAcctTxns, page, pageSize);
        }
        HttpHeaders respHeaderMap = PaginationUtil.populateResponseHeader(respPage.getTotalElements(),
                respPage.getTotalPages(), page, respPage.getPageable().getPageSize(),
                respPage.hasPrevious(), respPage.hasNext(), currency);

        return new GenericHolder(respPage.getContent(), respHeaderMap);
    }

}
