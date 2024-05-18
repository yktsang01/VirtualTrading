/*
 * ResetServiceTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.api.controller;

import com.yktsang.virtrade.api.controller.ResetService;
import com.yktsang.virtrade.request.ResetPortfolioRequest;
import com.yktsang.virtrade.response.ErrorResponse;
import com.yktsang.virtrade.response.SuccessResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Provides the test cases for <code>ResetService</code> and <code>ResetServiceController</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class ResetServiceTests {

    /**
     * The reset portfolio API endpoint.
     */
    private static final URI RESET_PORTFOLIO_URI = URI.create("/api/v1/member/portfolios/reset");
    /**
     * The mocked reset service.
     */
    @MockBean
    private ResetService resetService;

    /**
     * Tests reset portfolio for HTTP 200.
     */
    @Test
    public void resetPortfolio200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        ResetPortfolioRequest mockedReq = new ResetPortfolioRequest(false, "XXX");
        RequestEntity<ResetPortfolioRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, RESET_PORTFOLIO_URI);
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(resetService.resetPortfolio(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = resetService.resetPortfolio(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests reset portfolio for HTTP 400.
     */
    @Test
    public void resetPortfolio400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        ResetPortfolioRequest mockedReq = new ResetPortfolioRequest(false, "XXX");
        RequestEntity<ResetPortfolioRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, RESET_PORTFOLIO_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(resetService.resetPortfolio(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = resetService.resetPortfolio(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests reset portfolio for HTTP 401.
     */
    @Test
    public void resetPortfolio401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        ResetPortfolioRequest mockedReq = new ResetPortfolioRequest(false, "XXX");
        RequestEntity<ResetPortfolioRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, RESET_PORTFOLIO_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(resetService.resetPortfolio(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = resetService.resetPortfolio(req);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests reset portfolio for HTTP 404.
     */
    @Test
    public void resetPortfolio404() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        ResetPortfolioRequest mockedReq = new ResetPortfolioRequest(false, "XXX");
        RequestEntity<ResetPortfolioRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, RESET_PORTFOLIO_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(resetService.resetPortfolio(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = resetService.resetPortfolio(req);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

}
