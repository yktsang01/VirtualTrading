/*
 * TradingTransactionServiceTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.api.controller;

import com.yktsang.virtrade.api.controller.TradingTransactionService;
import com.yktsang.virtrade.response.ErrorResponse;
import com.yktsang.virtrade.response.TradingTransactionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;

import java.net.URI;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Provides the test cases for <code>TradingTransactionService</code> and <code>TradingTransactionServiceController</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class TradingTransactionServiceTests {

    /**
     * The trading transactions API endpoint.
     */
    private static final URI TRADING_TRANSACTIONS_URI = URI.create("/api/v1/member/trading/transactions");
    /**
     * The trading transactions with currency API endpoint.
     */
    private static final URI TRADING_TRANSACTIONS_CURRENCY_URI = URI.create("/api/v1/member/trading/transactions/XXX");
    /**
     * The mocked trading transaction service.
     */
    @MockBean
    private TradingTransactionService tradingTxnService;

    /**
     * Tests trading transactions for HTTP 200.
     */
    @Test
    public void tradingTransactions200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, TRADING_TRANSACTIONS_URI);
        TradingTransactionResponse mockedResp = new TradingTransactionResponse(new ArrayList<>());
        when(tradingTxnService.tradingTransactions(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = tradingTxnService.tradingTransactions(req, 1, 5);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests trading transactions for HTTP 204.
     */
    @Test
    public void tradingTransactions204() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, TRADING_TRANSACTIONS_URI);
        when(tradingTxnService.tradingTransactions(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        ResponseEntity<?> resp = tradingTxnService.tradingTransactions(req, 1, 5);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    /**
     * Tests trading transactions for HTTP 401.
     */
    @Test
    public void tradingTransactions401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, TRADING_TRANSACTIONS_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(tradingTxnService.tradingTransactions(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = tradingTxnService.tradingTransactions(req, 1, 5);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests trading transactions with currency for HTTP 200.
     */
    @Test
    public void tradingTransactionsWithCurrency200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, TRADING_TRANSACTIONS_CURRENCY_URI);
        TradingTransactionResponse mockedResp = new TradingTransactionResponse(new ArrayList<>());
        when(tradingTxnService.tradingTransactions(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = tradingTxnService.tradingTransactions(req, "XXX", 1, 5);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests trading transactions with currency for HTTP 204.
     */
    @Test
    public void tradingTransactionsWithCurrency204() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, TRADING_TRANSACTIONS_CURRENCY_URI);
        when(tradingTxnService.tradingTransactions(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        ResponseEntity<?> resp = tradingTxnService.tradingTransactions(req, "XXX", 1, 5);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    /**
     * Tests trading transactions with currency for HTTP 400.
     */
    @Test
    public void tradingTransactionsWithCurrency400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, TRADING_TRANSACTIONS_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(tradingTxnService.tradingTransactions(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = tradingTxnService.tradingTransactions(req, "XXX", 1, 5);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests trading transactions with currency for HTTP 401.
     */
    @Test
    public void tradingTransactionsWithCurrency401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, TRADING_TRANSACTIONS_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(tradingTxnService.tradingTransactions(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = tradingTxnService.tradingTransactions(req, "XXX", 1, 5);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests trading transactions with currency for HTTP 404.
     */
    @Test
    public void tradingTransactionsWithCurrency404() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, TRADING_TRANSACTIONS_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(tradingTxnService.tradingTransactions(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = tradingTxnService.tradingTransactions(req, "XXX", 1, 5);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

}
