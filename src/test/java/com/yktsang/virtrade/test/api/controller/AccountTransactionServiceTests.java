/*
 * AccountTransactionServiceTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.api.controller;

import com.yktsang.virtrade.api.controller.AccountTransactionService;
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
 * Provides the test cases for <code>AccountTransactionService</code> and <code>AccountTransactionServiceController</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class AccountTransactionServiceTests {

    /**
     * The account transactions API endpoint.
     */
    private static final URI ACCOUNT_TRANSACTIONS_URI = URI.create("/api/v1/member/account/transactions");
    /**
     * The account transactions with currency API endpoint.
     */
    private static final URI ACCOUNT_TRANSACTIONS_CURRENCY_URI = URI.create("/api/v1/member/account/transactions/XXX");
    /**
     * The mocked account transaction service.
     */
    @MockBean
    private AccountTransactionService acctTxnService;

    /**
     * Tests account transactions for HTTP 200.
     */
    @Test
    public void accountTransactions200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ACCOUNT_TRANSACTIONS_URI);
        AccountTransactionResponse mockedResp = new AccountTransactionResponse(new ArrayList<>());
        when(acctTxnService.accountTransactions(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = acctTxnService.accountTransactions(req, 1, 5);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests account transactions for HTTP 204.
     */
    @Test
    public void accountTransactions204() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ACCOUNT_TRANSACTIONS_URI);
        when(acctTxnService.accountTransactions(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        ResponseEntity<?> resp = acctTxnService.accountTransactions(req, 1, 5);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    /**
     * Tests account transactions for HTTP 401.
     */
    @Test
    public void accountTransactions401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ACCOUNT_TRANSACTIONS_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(acctTxnService.accountTransactions(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = acctTxnService.accountTransactions(req, 1, 5);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests account transactions with currency for HTTP 200.
     */
    @Test
    public void accountTransactionsWithCurrency200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ACCOUNT_TRANSACTIONS_CURRENCY_URI);
        AccountTransactionResponse mockedResp = new AccountTransactionResponse(new ArrayList<>());
        when(acctTxnService.accountTransactions(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = acctTxnService.accountTransactions(req, "XXX", 1, 5);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests account transactions with currency for HTTP 204.
     */
    @Test
    public void accountTransactionsWithCurrency204() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ACCOUNT_TRANSACTIONS_CURRENCY_URI);
        when(acctTxnService.accountTransactions(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        ResponseEntity<?> resp = acctTxnService.accountTransactions(req, "XXX", 1, 5);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    /**
     * Tests account transactions with currency for HTTP 400.
     */
    @Test
    public void accountTransactionsWithCurrency400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ACCOUNT_TRANSACTIONS_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(acctTxnService.accountTransactions(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = acctTxnService.accountTransactions(req, "XXX", 1, 5);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests account transactions with currency for HTTP 401.
     */
    @Test
    public void accountTransactionsWithCurrency401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ACCOUNT_TRANSACTIONS_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(acctTxnService.accountTransactions(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = acctTxnService.accountTransactions(req, "XXX", 1, 5);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests account transactions with currency for HTTP 404.
     */
    @Test
    public void accountTransactionsWithCurrency404() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ACCOUNT_TRANSACTIONS_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(acctTxnService.accountTransactions(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = acctTxnService.accountTransactions(req, "XXX", 1, 5);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

}
