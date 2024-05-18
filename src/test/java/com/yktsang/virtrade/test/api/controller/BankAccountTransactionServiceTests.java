/*
 * BankAccountTransactionServiceTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.api.controller;

import com.yktsang.virtrade.api.controller.BankAccountTransactionService;
import com.yktsang.virtrade.response.AccountTransactionResponse;
import com.yktsang.virtrade.response.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;

import java.net.URI;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Provides the test cases for <code>BankAccountTransactionService</code> and <code>BankAccountTransactionServiceController</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class BankAccountTransactionServiceTests {

    /**
     * The bank account transactions API endpoint.
     */
    private static final URI BANK_ACCOUNT_TRANSACTIONS_URI = URI.create("/api/v1/member/banks/transactions");
    /**
     * The bank account transactions with currency API endpoint.
     */
    private static final URI BANK_ACCOUNT_TRANSACTIONS_CURRENCY_URI = URI.create("/api/v1/member/banks/transactions/XXX");
    /**
     * The mocked bank account transaction service.
     */
    @MockBean
    private BankAccountTransactionService bankAcctTxnService;

    /**
     * Tests bank account transactions for HTTP 200.
     */
    @Test
    public void bankAccountTransactions200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, BANK_ACCOUNT_TRANSACTIONS_URI);
        AccountTransactionResponse mockedResp = new AccountTransactionResponse(new ArrayList<>());
        when(bankAcctTxnService.bankAccountTransactions(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = bankAcctTxnService.bankAccountTransactions(req, 1, 5);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests bank account transactions for HTTP 204.
     */
    @Test
    public void bankAccountTransactions204() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, BANK_ACCOUNT_TRANSACTIONS_URI);
        when(bankAcctTxnService.bankAccountTransactions(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        ResponseEntity<?> resp = bankAcctTxnService.bankAccountTransactions(req, 1, 5);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    /**
     * Tests bank account transactions for HTTP 401.
     */
    @Test
    public void bankAccountTransactions401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, BANK_ACCOUNT_TRANSACTIONS_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(bankAcctTxnService.bankAccountTransactions(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = bankAcctTxnService.bankAccountTransactions(req, 1, 5);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests bank account transactions with currency for HTTP 200.
     */
    @Test
    public void bankAccountTransactionsWithCurrency200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, BANK_ACCOUNT_TRANSACTIONS_CURRENCY_URI);
        AccountTransactionResponse mockedResp = new AccountTransactionResponse(new ArrayList<>());
        when(bankAcctTxnService.bankAccountTransactions(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = bankAcctTxnService.bankAccountTransactions(req, "XXX", 1, 5);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests bank account transactions with currency for HTTP 204.
     */
    @Test
    public void bankAccountTransactionsWithCurrency204() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, BANK_ACCOUNT_TRANSACTIONS_CURRENCY_URI);
        when(bankAcctTxnService.bankAccountTransactions(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        ResponseEntity<?> resp = bankAcctTxnService.bankAccountTransactions(req, "XXX", 1, 5);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    /**
     * Tests bank account transactions with currency for HTTP 400.
     */
    @Test
    public void bankAccountTransactionsWithCurrency400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, BANK_ACCOUNT_TRANSACTIONS_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(bankAcctTxnService.bankAccountTransactions(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = bankAcctTxnService.bankAccountTransactions(req, "XXX", 1, 5);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests bank account transactions with currency for HTTP 401.
     */
    @Test
    public void bankAccountTransactionsWithCurrency401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, BANK_ACCOUNT_TRANSACTIONS_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(bankAcctTxnService.bankAccountTransactions(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = bankAcctTxnService.bankAccountTransactions(req, "XXX", 1, 5);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests bank account transactions with currency for HTTP 404.
     */
    @Test
    public void bankAccountTransactionsWithCurrency404() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, BANK_ACCOUNT_TRANSACTIONS_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(bankAcctTxnService.bankAccountTransactions(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = bankAcctTxnService.bankAccountTransactions(req, "XXX", 1, 5);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

}
