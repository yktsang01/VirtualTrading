/*
 * BankAccountServiceTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.api.controller;

import com.yktsang.virtrade.api.controller.BankAccountService;
import com.yktsang.virtrade.request.AddBankAccountRequest;
import com.yktsang.virtrade.response.BankAccountResponse;
import com.yktsang.virtrade.response.ErrorResponse;
import com.yktsang.virtrade.response.SuccessResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;

import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Provides the test cases for <code>BankAccountService</code> and <code>BankAccountServiceController</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class BankAccountServiceTests {

    /**
     * The bank accounts API endpoint.
     */
    private static final URI BANK_ACCOUNTS_URI = URI.create("/api/v1/member/banks");
    /**
     * The bank accounts with currency API endpoint.
     */
    private static final URI BANK_ACCOUNTS_CURRENCY_URI = URI.create("/api/v1/member/banks/XXX");
    /**
     * The add bank account API endpoint.
     */
    private static final URI ADD_BANK_ACCOUNT_URI = URI.create("/api/v1/member/banks/add");
    /**
     * The obsolete bank account API endpoint.
     */
    private static final URI OBSOLETE_BANK_ACCOUNT_URI = URI.create("/api/v1/member/banks/obsolete/1");
    /**
     * The mocked bank account service.
     */
    @MockBean
    private BankAccountService bankAcctService;

    /**
     * Tests bank accounts for HTTP 200.
     */
    @Test
    public void bankAccounts200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, BANK_ACCOUNTS_URI);
        BankAccountResponse mockedResp = new BankAccountResponse(new ArrayList<>());
        when(bankAcctService.bankAccounts(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = bankAcctService.bankAccounts(req, 1, 5);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests bank accounts for HTTP 204.
     */
    @Test
    public void bankAccounts204() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, BANK_ACCOUNTS_URI);
        when(bankAcctService.bankAccounts(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        ResponseEntity<?> resp = bankAcctService.bankAccounts(req, 1, 5);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    /**
     * Tests bank accounts for HTTP 401.
     */
    @Test
    public void bankAccounts401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, BANK_ACCOUNTS_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(bankAcctService.bankAccounts(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = bankAcctService.bankAccounts(req, 1, 5);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests bank accounts with currency for HTTP 200.
     */
    @Test
    public void bankAccountsWithCurrency200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, BANK_ACCOUNTS_CURRENCY_URI);
        BankAccountResponse mockedResp = new BankAccountResponse(new ArrayList<>());
        when(bankAcctService.bankAccounts(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = bankAcctService.bankAccounts(req, "XXX", 1, 5);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests bank accounts with currency for HTTP 204.
     */
    @Test
    public void bankAccountsWithCurrency204() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, BANK_ACCOUNTS_CURRENCY_URI);
        when(bankAcctService.bankAccounts(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        ResponseEntity<?> resp = bankAcctService.bankAccounts(req, "XXX", 1, 5);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    /**
     * Tests bank accounts with currency for HTTP 400.
     */
    @Test
    public void bankAccountsWithCurrency400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, BANK_ACCOUNTS_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(bankAcctService.bankAccounts(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = bankAcctService.bankAccounts(req, "XXX", 1, 5);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests bank accounts with currency for HTTP 401.
     */
    @Test
    public void bankAccountsWithCurrency401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, BANK_ACCOUNTS_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(bankAcctService.bankAccounts(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = bankAcctService.bankAccounts(req, "XXX", 1, 5);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests bank accounts with currency for HTTP 404.
     */
    @Test
    public void bankAccountsWithCurrency404() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, BANK_ACCOUNTS_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(bankAcctService.bankAccounts(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = bankAcctService.bankAccounts(req, "XXX", 1, 5);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    /**
     * Tests add bank account for HTTP 201.
     */
    @Test
    public void addBankAccount201() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        AddBankAccountRequest mockedReq = new AddBankAccountRequest("XXX", "name", "number");
        RequestEntity<AddBankAccountRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, ADD_BANK_ACCOUNT_URI);
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(bankAcctService.addBankAccount(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.CREATED).body(mockedResp));

        ResponseEntity<?> resp = bankAcctService.addBankAccount(req);
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
    }

    /**
     * Tests add bank account for HTTP 400.
     */
    @Test
    public void addBankAccount400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        AddBankAccountRequest mockedReq = new AddBankAccountRequest("XXX", "name", "");
        RequestEntity<AddBankAccountRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, ADD_BANK_ACCOUNT_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(bankAcctService.addBankAccount(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = bankAcctService.addBankAccount(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests add bank account for HTTP 401.
     */
    @Test
    public void addBankAccount401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        AddBankAccountRequest mockedReq = new AddBankAccountRequest("XXX", "name", "number");
        RequestEntity<AddBankAccountRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, ADD_BANK_ACCOUNT_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(bankAcctService.addBankAccount(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = bankAcctService.addBankAccount(req);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests add bank account for HTTP 404.
     */
    @Test
    public void addBankAccount404() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        AddBankAccountRequest mockedReq = new AddBankAccountRequest("XXX", "name", "number");
        RequestEntity<AddBankAccountRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, ADD_BANK_ACCOUNT_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(bankAcctService.addBankAccount(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = bankAcctService.addBankAccount(req);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    /**
     * Tests obsolete bank account for HTTP 200.
     */
    @Test
    public void obsoleteBankAccount200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.POST, OBSOLETE_BANK_ACCOUNT_URI);
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(bankAcctService.obsoleteBankAccount(req, BigInteger.ONE))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = bankAcctService.obsoleteBankAccount(req, BigInteger.ONE);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests obsolete bank account for HTTP 302.
     */
    @Test
    public void obsoleteBankAccount302() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.POST, OBSOLETE_BANK_ACCOUNT_URI);

        when(bankAcctService.obsoleteBankAccount(req, BigInteger.ONE))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_MODIFIED).build());

        ResponseEntity<?> resp = bankAcctService.obsoleteBankAccount(req, BigInteger.ONE);
        assertEquals(HttpStatus.NOT_MODIFIED, resp.getStatusCode());
    }

    /**
     * Tests obsolete bank account for HTTP 400.
     */
    @Test
    public void obsoleteBankAccount400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.POST, OBSOLETE_BANK_ACCOUNT_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(bankAcctService.obsoleteBankAccount(req, BigInteger.ONE))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = bankAcctService.obsoleteBankAccount(req, BigInteger.ONE);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests obsolete bank account for HTTP 401.
     */
    @Test
    public void obsoleteBankAccount401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.POST, OBSOLETE_BANK_ACCOUNT_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(bankAcctService.obsoleteBankAccount(req, BigInteger.ONE))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = bankAcctService.obsoleteBankAccount(req, BigInteger.ONE);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests obsolete bank account for HTTP 404.
     */
    @Test
    public void obsoleteBankAccount404() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.POST, OBSOLETE_BANK_ACCOUNT_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(bankAcctService.obsoleteBankAccount(req, BigInteger.ONE))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = bankAcctService.obsoleteBankAccount(req, BigInteger.ONE);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    /**
     * Tests obsolete bank account for HTTP 406.
     */
    @Test
    public void obsoleteBankAccount406() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.POST, OBSOLETE_BANK_ACCOUNT_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(bankAcctService.obsoleteBankAccount(req, BigInteger.ONE))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(mockedResp));

        ResponseEntity<?> resp = bankAcctService.obsoleteBankAccount(req, BigInteger.ONE);
        assertEquals(HttpStatus.NOT_ACCEPTABLE, resp.getStatusCode());
    }

}
