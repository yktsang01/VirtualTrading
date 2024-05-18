/*
 * TradingServiceTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.api.controller;

import com.yktsang.virtrade.api.controller.TradingService;
import com.yktsang.virtrade.request.BuyRequest;
import com.yktsang.virtrade.request.SearchRequest;
import com.yktsang.virtrade.request.SellRequest;
import com.yktsang.virtrade.response.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;

import java.net.URI;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Provides the test cases for <code>TradingService</code> and <code>TradingServiceController</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class TradingServiceTests {

    /**
     * The indices API endpoint.
     */
    private static final URI INDICES_URI = URI.create("/api/v1/member/trading/indices");
    /**
     * The indices with currency API endpoint.
     */
    private static final URI INDICES_CURRENCY_URI = URI.create("/api/v1/member/trading/indices/XXX");
    /**
     * The equities API endpoint.
     */
    private static final URI EQUITIES_URI = URI.create("/api/v1/member/trading/equities");
    /**
     * The equities with currency API endpoint.
     */
    private static final URI EQUITIES_CURRENCY_URI = URI.create("/api/v1/member/trading/equities/XXX");
    /**
     * The search API endpoint.
     */
    private static final URI SEARCH_URI = URI.create("/api/v1/member/trading/search");
    /**
     * The buy API endpoint.
     */
    private static final URI BUY_URI = URI.create("/api/v1/member/trading/buy");
    /**
     * The sell API endpoint.
     */
    private static final URI SELL_URI = URI.create("/api/v1/member/trading/sell");
    /**
     * The outstanding transactions API endpoint.
     */
    private static final URI OS_TRANSACTIONS_URI = URI.create("/api/v1/member/trading/transactions/outstanding");
    /**
     * The outstanding transactions with currency API endpoint.
     */
    private static final URI OS_TRANSACTIONS_CURRENCY_URI = URI.create("/api/v1/member/trading/transactions/outstanding/XXX");
    /**
     * The mocked trading service.
     */
    @MockBean
    private TradingService tradingService;

    /**
     * Tests indices for HTTP 200.
     */
    @Test
    public void indices200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, INDICES_URI);
        YahooStockResponse mockedResp = new YahooStockResponse(new ArrayList<>());
        when(tradingService.indices(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = tradingService.indices(req, 1, 5);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests indices for HTTP 204.
     */
    @Test
    public void indices204() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, INDICES_URI);
        when(tradingService.indices(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        ResponseEntity<?> resp = tradingService.indices(req, 1, 5);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    /**
     * Tests indices for HTTP 401.
     */
    @Test
    public void indices401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, INDICES_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(tradingService.indices(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = tradingService.indices(req, 1, 5);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests indices with currency for HTTP 200.
     */
    @Test
    public void indicesWithCurrency200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, INDICES_CURRENCY_URI);
        YahooStockResponse mockedResp = new YahooStockResponse(new ArrayList<>());
        when(tradingService.indices(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = tradingService.indices(req, "XXX", 1, 5);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests indices with currency for HTTP 204.
     */
    @Test
    public void indicesWithCurrency204() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, INDICES_CURRENCY_URI);
        when(tradingService.indices(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        ResponseEntity<?> resp = tradingService.indices(req, "XXX", 1, 5);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    /**
     * Tests indices with currency for HTTP 400.
     */
    @Test
    public void indicesWithCurrency400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, INDICES_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(tradingService.indices(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = tradingService.indices(req, "XXX", 1, 5);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests indices with currency for HTTP 401.
     */
    @Test
    public void indicesWithCurrency401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, INDICES_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(tradingService.indices(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = tradingService.indices(req, "XXX", 1, 5);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests indices with currency for HTTP 404.
     */
    @Test
    public void indicesWithCurrency404() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, INDICES_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(tradingService.indices(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = tradingService.indices(req, "XXX", 1, 5);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    /**
     * Tests equities for HTTP 200.
     */
    @Test
    public void equities200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, EQUITIES_URI);
        YahooStockResponse mockedResp = new YahooStockResponse(new ArrayList<>());
        when(tradingService.equities(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = tradingService.equities(req, 1, 5);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests equities for HTTP 204.
     */
    @Test
    public void equities204() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, EQUITIES_URI);
        when(tradingService.equities(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        ResponseEntity<?> resp = tradingService.equities(req, 1, 5);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    /**
     * Tests equities for HTTP 401.
     */
    @Test
    public void equities401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, EQUITIES_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(tradingService.equities(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = tradingService.equities(req, 1, 5);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests equities with currency for HTTP 200.
     */
    @Test
    public void equitiesWithCurrency200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, EQUITIES_CURRENCY_URI);
        YahooStockResponse mockedResp = new YahooStockResponse(new ArrayList<>());
        when(tradingService.equities(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = tradingService.equities(req, "XXX", 1, 5);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests equities with currency for HTTP 204.
     */
    @Test
    public void equitiesWithCurrency204() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, EQUITIES_CURRENCY_URI);
        when(tradingService.equities(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        ResponseEntity<?> resp = tradingService.equities(req, "XXX", 1, 5);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    /**
     * Tests equities with currency for HTTP 400.
     */
    @Test
    public void equitiesWithCurrency400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, EQUITIES_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(tradingService.equities(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = tradingService.equities(req, "XXX", 1, 5);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests equities with currency for HTTP 401.
     */
    @Test
    public void equitiesWithCurrency401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, EQUITIES_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(tradingService.equities(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = tradingService.equities(req, "XXX", 1, 5);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests equities with currency for HTTP 404.
     */
    @Test
    public void equitiesWithCurrency404() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, EQUITIES_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(tradingService.equities(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = tradingService.equities(req, "XXX", 1, 5);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    /**
     * Tests search for HTTP 200.
     */
    @Test
    public void search200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        SearchRequest mockedReq = new SearchRequest("symbol", "SYM");
        RequestEntity<SearchRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, SEARCH_URI);
        SearchResultResponse mockedResp = new SearchResultResponse(new ArrayList<>());
        when(tradingService.search(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = tradingService.search(req, 1, 5);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests search for HTTP 204.
     */
    @Test
    public void search204() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        SearchRequest mockedReq = new SearchRequest("symbol", "SYM");
        RequestEntity<SearchRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, SEARCH_URI);
        when(tradingService.search(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        ResponseEntity<?> resp = tradingService.search(req, 1, 5);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    /**
     * Tests search for HTTP 400.
     */
    @Test
    public void search400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        SearchRequest mockedReq = new SearchRequest("symbol", "SYM");
        RequestEntity<SearchRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, SEARCH_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(tradingService.search(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = tradingService.search(req, 1, 5);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests search for HTTP 401.
     */
    @Test
    public void search401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        SearchRequest mockedReq = new SearchRequest("symbol", "SYM");
        RequestEntity<SearchRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, SEARCH_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(tradingService.search(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = tradingService.search(req, 1, 5);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests buy for HTTP 200.
     */
    @Test
    public void buy200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        BuyRequest mockedReq = new BuyRequest("SYM", 10);
        RequestEntity<BuyRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, BUY_URI);
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(tradingService.buy(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = tradingService.buy(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests buy for HTTP 400.
     */
    @Test
    public void buy400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        BuyRequest mockedReq = new BuyRequest("SYM", 10);
        RequestEntity<BuyRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, BUY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(tradingService.buy(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = tradingService.buy(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests buy for HTTP 401.
     */
    @Test
    public void buy401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        BuyRequest mockedReq = new BuyRequest("SYM", 10);
        RequestEntity<BuyRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, BUY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(tradingService.buy(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = tradingService.buy(req);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests buy for HTTP 404.
     */
    @Test
    public void buy404() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        BuyRequest mockedReq = new BuyRequest("SYM", 10);
        RequestEntity<BuyRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, BUY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(tradingService.buy(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = tradingService.buy(req);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    /**
     * Tests buy for HTTP 406.
     */
    @Test
    public void buy406() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        BuyRequest mockedReq = new BuyRequest("SYM", 10);
        RequestEntity<BuyRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, BUY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(tradingService.buy(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(mockedResp));

        ResponseEntity<?> resp = tradingService.buy(req);
        assertEquals(HttpStatus.NOT_ACCEPTABLE, resp.getStatusCode());
    }

    /**
     * Tests sell for HTTP 200.
     */
    @Test
    public void sell200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        SellRequest mockedReq = new SellRequest("SYM", 10, false, null);
        RequestEntity<SellRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, SELL_URI);
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(tradingService.sell(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = tradingService.sell(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests sell for HTTP 400.
     */
    @Test
    public void sell400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        SellRequest mockedReq = new SellRequest("SYM", 10, false, null);
        RequestEntity<SellRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, SELL_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(tradingService.sell(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = tradingService.sell(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests sell for HTTP 401.
     */
    @Test
    public void sell401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        SellRequest mockedReq = new SellRequest("SYM", 10, false, null);
        RequestEntity<SellRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, SELL_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(tradingService.sell(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = tradingService.sell(req);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests sell for HTTP 404.
     */
    @Test
    public void sell404() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        SellRequest mockedReq = new SellRequest("SYM", 10, false, null);
        RequestEntity<SellRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, SELL_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(tradingService.sell(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = tradingService.sell(req);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    /**
     * Tests sell for HTTP 406.
     */
    @Test
    public void sell406() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        SellRequest mockedReq = new SellRequest("SYM", 10, false, null);
        RequestEntity<SellRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, SELL_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(tradingService.sell(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(mockedResp));

        ResponseEntity<?> resp = tradingService.sell(req);
        assertEquals(HttpStatus.NOT_ACCEPTABLE, resp.getStatusCode());
    }

    /**
     * Tests outstanding transactions for HTTP 200.
     */
    @Test
    public void outstandingTransactions200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, OS_TRANSACTIONS_URI);
        OutstandingTransactionResponse mockedResp = new OutstandingTransactionResponse(new ArrayList<>());
        when(tradingService.outstandingTransactions(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = tradingService.outstandingTransactions(req, 1, 5);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests outstanding transactions for HTTP 204.
     */
    @Test
    public void outstandingTransactions204() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, OS_TRANSACTIONS_URI);
        when(tradingService.outstandingTransactions(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        ResponseEntity<?> resp = tradingService.outstandingTransactions(req, 1, 5);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    /**
     * Tests outstanding transactions for HTTP 401.
     */
    @Test
    public void outstandingTransactions401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, OS_TRANSACTIONS_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(tradingService.outstandingTransactions(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = tradingService.outstandingTransactions(req, 1, 5);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests outstanding transactions with currency for HTTP 200.
     */
    @Test
    public void outstandingTransactionsWithCurrency200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, OS_TRANSACTIONS_CURRENCY_URI);
        OutstandingTransactionResponse mockedResp = new OutstandingTransactionResponse(new ArrayList<>());
        when(tradingService.outstandingTransactions(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = tradingService.outstandingTransactions(req, "XXX", 1, 5);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests outstanding transactions with currency for HTTP 204.
     */
    @Test
    public void outstandingTransactionsWithCurrency204() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, OS_TRANSACTIONS_CURRENCY_URI);
        when(tradingService.outstandingTransactions(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        ResponseEntity<?> resp = tradingService.outstandingTransactions(req, "XXX", 1, 5);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    /**
     * Tests outstanding transactions with currency for HTTP 400.
     */
    @Test
    public void outstandingTransactionsWithCurrency400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, OS_TRANSACTIONS_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(tradingService.outstandingTransactions(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = tradingService.outstandingTransactions(req, "XXX", 1, 5);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests outstanding transactions with currency for HTTP 401.
     */
    @Test
    public void outstandingTransactionsWithCurrency401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, OS_TRANSACTIONS_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(tradingService.outstandingTransactions(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = tradingService.outstandingTransactions(req, "XXX", 1, 5);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests outstanding transactions with currency for HTTP 404.
     */
    @Test
    public void outstandingTransactionsWithCurrency404() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, OS_TRANSACTIONS_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(tradingService.outstandingTransactions(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = tradingService.outstandingTransactions(req, "XXX", 1, 5);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

}
