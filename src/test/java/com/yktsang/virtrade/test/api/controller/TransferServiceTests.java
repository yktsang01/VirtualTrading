/*
 * TransferServiceTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.api.controller;

import com.yktsang.virtrade.api.controller.TransferService;
import com.yktsang.virtrade.request.TransferFundRequest;
import com.yktsang.virtrade.response.ErrorResponse;
import com.yktsang.virtrade.response.SuccessResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Provides the test cases for <code>TransferService</code> and <code>TransferServiceController</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class TransferServiceTests {

    /**
     * The transfer funds API endpoint.
     */
    private static final URI TRANSFER_FUNDS_URI = URI.create("/api/v1/member/transfer");
    /**
     * The mocked transfer service.
     */
    @MockBean
    private TransferService transferService;

    /**
     * Tests transfer funds for HTTP 200.
     */
    @Test
    public void transferFunds200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        TransferFundRequest mockedReq =
                new TransferFundRequest("XXX", BigInteger.ONE, BigDecimal.ONE);
        RequestEntity<TransferFundRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, TRANSFER_FUNDS_URI);
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(transferService.transferFunds(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = transferService.transferFunds(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests transfer funds for HTTP 400.
     */
    @Test
    public void transferFunds400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        TransferFundRequest mockedReq =
                new TransferFundRequest("XXX", BigInteger.ONE, BigDecimal.ONE);
        RequestEntity<TransferFundRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, TRANSFER_FUNDS_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(transferService.transferFunds(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = transferService.transferFunds(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests transfer funds for HTTP 401.
     */
    @Test
    public void transferFunds401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        TransferFundRequest mockedReq =
                new TransferFundRequest("XXX", BigInteger.ONE, BigDecimal.ONE);
        RequestEntity<TransferFundRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, TRANSFER_FUNDS_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(transferService.transferFunds(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = transferService.transferFunds(req);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests transfer funds for HTTP 404.
     */
    @Test
    public void transferFunds404() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        TransferFundRequest mockedReq =
                new TransferFundRequest("XXX", BigInteger.ONE, BigDecimal.ONE);
        RequestEntity<TransferFundRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, TRANSFER_FUNDS_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(transferService.transferFunds(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = transferService.transferFunds(req);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    /**
     * Tests transfer funds for HTTP 406.
     */
    @Test
    public void transferFunds406() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        TransferFundRequest mockedReq =
                new TransferFundRequest("XXX", BigInteger.ONE, BigDecimal.ONE);
        RequestEntity<TransferFundRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, TRANSFER_FUNDS_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(transferService.transferFunds(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(mockedResp));

        ResponseEntity<?> resp = transferService.transferFunds(req);
        assertEquals(HttpStatus.NOT_ACCEPTABLE, resp.getStatusCode());
    }

}
