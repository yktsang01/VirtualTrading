/*
 * AccountBalanceServiceTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.api.controller;

import com.yktsang.virtrade.api.controller.AccountBalanceService;
import com.yktsang.virtrade.request.DepositFundRequest;
import com.yktsang.virtrade.response.AccountBalanceResponse;
import com.yktsang.virtrade.response.ErrorResponse;
import com.yktsang.virtrade.response.SuccessResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Provides the test cases for <code>AccountBalanceService</code> and <code>AccountBalanceServiceController</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class AccountBalanceServiceTests {

    /**
     * The account balances API endpoint.
     */
    private static final URI ACCOUNT_BALANCES_URI = URI.create("/api/v1/member/balances");
    /**
     * The account balances with currency API endpoint.
     */
    private static final URI ACCOUNT_BALANCES_CURRENCY_URI = URI.create("/api/v1/member/balances/XXX");
    /**
     * The deposit funds API endpoint.
     */
    private static final URI DEPOSIT_FUNDS_URI = URI.create("/api/v1/member/balances/deposit");
    /**
     * The mocked account balance service.
     */
    @MockBean
    private AccountBalanceService acctBalService;

    /**
     * Tests account balances for HTTP 200.
     */
    @Test
    public void accountBalances200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ACCOUNT_BALANCES_URI);
        AccountBalanceResponse mockedResp = new AccountBalanceResponse(new ArrayList<>());
        when(acctBalService.accountBalances(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = acctBalService.accountBalances(req, 1, 5);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests account balances for HTTP 204.
     */
    @Test
    public void accountBalances204() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ACCOUNT_BALANCES_URI);
        when(acctBalService.accountBalances(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        ResponseEntity<?> resp = acctBalService.accountBalances(req, 1, 5);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    /**
     * Tests account balances for HTTP 401.
     */
    @Test
    public void accountBalances401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ACCOUNT_BALANCES_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(acctBalService.accountBalances(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = acctBalService.accountBalances(req, 1, 5);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests account balances with currency for HTTP 200.
     */
    @Test
    public void accountBalancesWithCurrency200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ACCOUNT_BALANCES_CURRENCY_URI);
        AccountBalanceResponse mockedResp = new AccountBalanceResponse(new ArrayList<>());
        when(acctBalService.accountBalances(req, "XXX"))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = acctBalService.accountBalances(req, "XXX");
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests account balances with currency for HTTP 204.
     */
    @Test
    public void accountBalancesWithCurrency204() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ACCOUNT_BALANCES_CURRENCY_URI);
        when(acctBalService.accountBalances(req, "XXX"))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        ResponseEntity<?> resp = acctBalService.accountBalances(req, "XXX");
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    /**
     * Tests account balances with currency for HTTP 400.
     */
    @Test
    public void accountBalancesWithCurrency400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ACCOUNT_BALANCES_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(acctBalService.accountBalances(req, "XXX"))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = acctBalService.accountBalances(req, "XXX");
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests account balances with currency for HTTP 401.
     */
    @Test
    public void accountBalancesWithCurrency401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ACCOUNT_BALANCES_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(acctBalService.accountBalances(req, "XXX"))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = acctBalService.accountBalances(req, "XXX");
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests account balances with currency for HTTP 404.
     */
    @Test
    public void accountBalancesWithCurrency404() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ACCOUNT_BALANCES_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(acctBalService.accountBalances(req, "XXX"))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = acctBalService.accountBalances(req, "XXX");
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    /**
     * Tests deposit funds for HTTP 200.
     */
    @Test
    public void depositFunds200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        DepositFundRequest mockedReq = new DepositFundRequest("XXX", BigDecimal.ONE);
        RequestEntity<DepositFundRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, DEPOSIT_FUNDS_URI);
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(acctBalService.depositFunds(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = acctBalService.depositFunds(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests deposit funds for HTTP 201.
     */
    @Test
    public void depositFunds201() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        DepositFundRequest mockedReq = new DepositFundRequest("XXX", BigDecimal.ONE);
        RequestEntity<DepositFundRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, DEPOSIT_FUNDS_URI);
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(acctBalService.depositFunds(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.CREATED).body(mockedResp));

        ResponseEntity<?> resp = acctBalService.depositFunds(req);
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
    }

    /**
     * Tests deposit funds for HTTP 400.
     */
    @Test
    public void depositFunds400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        DepositFundRequest mockedReq = new DepositFundRequest("XXX", BigDecimal.ZERO);
        RequestEntity<DepositFundRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, DEPOSIT_FUNDS_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(acctBalService.depositFunds(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = acctBalService.depositFunds(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests deposit funds for HTTP 401.
     */
    @Test
    public void depositFunds401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        DepositFundRequest mockedReq = new DepositFundRequest("XXX", BigDecimal.ONE);
        RequestEntity<DepositFundRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, DEPOSIT_FUNDS_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(acctBalService.depositFunds(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = acctBalService.depositFunds(req);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests deposit funds for HTTP 404.
     */
    @Test
    public void depositFunds404() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        DepositFundRequest mockedReq = new DepositFundRequest("XXX", BigDecimal.ONE);
        RequestEntity<DepositFundRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, DEPOSIT_FUNDS_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(acctBalService.depositFunds(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = acctBalService.depositFunds(req);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    /**
     * Tests deposit funds for HTTP 406.
     */
    @Test
    public void depositFunds406() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        DepositFundRequest mockedReq = new DepositFundRequest("XXX", BigDecimal.ONE);
        RequestEntity<DepositFundRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, DEPOSIT_FUNDS_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(acctBalService.depositFunds(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(mockedResp));

        ResponseEntity<?> resp = acctBalService.depositFunds(req);
        assertEquals(HttpStatus.NOT_ACCEPTABLE, resp.getStatusCode());
    }

}
