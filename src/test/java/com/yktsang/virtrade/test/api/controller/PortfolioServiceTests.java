/*
 * PortfolioServiceTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.api.controller;

import com.yktsang.virtrade.api.controller.PortfolioService;
import com.yktsang.virtrade.request.CreatePortfolioRequest;
import com.yktsang.virtrade.request.LinkTransactionRequest;
import com.yktsang.virtrade.request.UnlinkTransactionRequest;
import com.yktsang.virtrade.response.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Provides the test cases for <code>PortfolioService</code> and <code>PortfolioServiceController</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class PortfolioServiceTests {

    /**
     * The portfolios API endpoint.
     */
    private static final URI PORTFOLIOS_URI = URI.create("/api/v1/member/portfolios");
    /**
     * The portfolios with currency API endpoint.
     */
    private static final URI PORTFOLIOS_CURRENCY_URI = URI.create("/api/v1/member/portfolios/XXX");
    /**
     * The create portfolio API endpoint.
     */
    private static final URI CREATE_PORTFOLIO_URI = URI.create("/api/v1/member/portfolios/create");
    /**
     * The portfolio details API endpoint.
     */
    private static final URI PORTFOLIO_DETAILS_URI = URI.create("/api/v1/member/portfolios/details//1");
    /**
     * The link to portfolio API endpoint.
     */
    private static final URI LINK_PORTFOLIO_URI = URI.create("/api/v1/member/portfolios/link");
    /**
     * The unlink from portfolio API endpoint.
     */
    private static final URI UNLINK_PORTFOLIO_URI = URI.create("/api/v1/member/portfolios/unlink");
    /**
     * The mocked portfolio service.
     */
    @MockBean
    private PortfolioService portService;

    /**
     * Tests portfolios for HTTP 200.
     */
    @Test
    public void portfolios200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, PORTFOLIOS_URI);
        PortfolioResponse mockedResp = new PortfolioResponse(new ArrayList<>());
        when(portService.portfolios(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = portService.portfolios(req, 1, 5);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests portfolios for HTTP 204.
     */
    @Test
    public void portfolios204() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, PORTFOLIOS_URI);
        when(portService.portfolios(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        ResponseEntity<?> resp = portService.portfolios(req, 1, 5);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    /**
     * Tests portfolios for HTTP 401.
     */
    @Test
    public void portfolios401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, PORTFOLIOS_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(portService.portfolios(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = portService.portfolios(req, 1, 5);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests portfolios with currency for HTTP 200.
     */
    @Test
    public void portfoliosWithCurrency200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, PORTFOLIOS_CURRENCY_URI);
        PortfolioResponse mockedResp = new PortfolioResponse(new ArrayList<>());
        when(portService.portfolios(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = portService.portfolios(req, "XXX", 1, 5);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests portfolios with currency for HTTP 204.
     */
    @Test
    public void portfoliosWithCurrency204() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, PORTFOLIOS_CURRENCY_URI);
        when(portService.portfolios(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        ResponseEntity<?> resp = portService.portfolios(req, "XXX", 1, 5);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    /**
     * Tests portfolios with currency for HTTP 400.
     */
    @Test
    public void portfoliosWithCurrency400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, PORTFOLIOS_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(portService.portfolios(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = portService.portfolios(req, "XXX", 1, 5);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests portfolios with currency for HTTP 401.
     */
    @Test
    public void portfoliosWithCurrency401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, PORTFOLIOS_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(portService.portfolios(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = portService.portfolios(req, "XXX", 1, 5);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests portfolios with currency for HTTP 404.
     */
    @Test
    public void portfoliosWithCurrency404() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, PORTFOLIOS_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(portService.portfolios(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = portService.portfolios(req, "XXX", 1, 5);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    /**
     * Tests create portfolio for HTTP 201.
     */
    @Test
    public void createPortfolio201() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        CreatePortfolioRequest mockedReq = new CreatePortfolioRequest("name", "XXX");
        RequestEntity<CreatePortfolioRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, CREATE_PORTFOLIO_URI);
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(portService.createPortfolio(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.CREATED).body(mockedResp));

        ResponseEntity<?> resp = portService.createPortfolio(req);
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
    }

    /**
     * Tests create portfolio for HTTP 400.
     */
    @Test
    public void createPortfolio400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        CreatePortfolioRequest mockedReq = new CreatePortfolioRequest("name", "XXX");
        RequestEntity<CreatePortfolioRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, CREATE_PORTFOLIO_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(portService.createPortfolio(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = portService.createPortfolio(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests create portfolio for HTTP 401.
     */
    @Test
    public void createPortfolio401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        CreatePortfolioRequest mockedReq = new CreatePortfolioRequest("name", "XXX");
        RequestEntity<CreatePortfolioRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, CREATE_PORTFOLIO_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(portService.createPortfolio(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = portService.createPortfolio(req);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests create portfolio for HTTP 404.
     */
    @Test
    public void createPortfolio404() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        CreatePortfolioRequest mockedReq = new CreatePortfolioRequest("name", "XXX");
        RequestEntity<CreatePortfolioRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, CREATE_PORTFOLIO_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(portService.createPortfolio(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = portService.createPortfolio(req);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    /**
     * Tests portfolio details for HTTP 200.
     */
    @Test
    public void portfolioDetails200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, PORTFOLIO_DETAILS_URI);
        PortfolioDetailResponse mockedResp = new PortfolioDetailResponse(
                new Portfolio(BigInteger.ONE, "user@domain.com", "name", "XXX",
                        BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE), new ArrayList<>());
        when(portService.portfolioDetails(req, BigInteger.ONE, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = portService.portfolioDetails(req, BigInteger.ONE, 1, 5);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests portfolio details for HTTP 400.
     */
    @Test
    public void portfolioDetails400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, PORTFOLIO_DETAILS_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(portService.portfolioDetails(req, BigInteger.ONE, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = portService.portfolioDetails(req, BigInteger.ONE, 1, 5);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests portfolio details for HTTP 401.
     */
    @Test
    public void portfolioDetails401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, PORTFOLIO_DETAILS_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(portService.portfolioDetails(req, BigInteger.ONE, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = portService.portfolioDetails(req, BigInteger.ONE, 1, 5);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests portfolio details for HTTP 404.
     */
    @Test
    public void portfolioDetails404() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, PORTFOLIO_DETAILS_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(portService.portfolioDetails(req, BigInteger.ONE, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = portService.portfolioDetails(req, BigInteger.ONE, 1, 5);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    /**
     * Tests portfolio details for HTTP 406.
     */
    @Test
    public void portfolioDetails406() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, PORTFOLIO_DETAILS_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(portService.portfolioDetails(req, BigInteger.ONE, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(mockedResp));

        ResponseEntity<?> resp = portService.portfolioDetails(req, BigInteger.ONE, 1, 5);
        assertEquals(HttpStatus.NOT_ACCEPTABLE, resp.getStatusCode());
    }

    /**
     * Tests link to portfolio for HTTP 200.
     */
    @Test
    public void linkToPortfolio200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        LinkTransactionRequest mockedReq =
                new LinkTransactionRequest(false, null, BigInteger.ONE, new ArrayList<>());
        RequestEntity<LinkTransactionRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, LINK_PORTFOLIO_URI);
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(portService.linkToPortfolio(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = portService.linkToPortfolio(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests link to portfolio for HTTP 304.
     */
    @Test
    public void linkToPortfolio304() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        LinkTransactionRequest mockedReq =
                new LinkTransactionRequest(false, null, BigInteger.ONE, new ArrayList<>());
        RequestEntity<LinkTransactionRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, LINK_PORTFOLIO_URI);

        when(portService.linkToPortfolio(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_MODIFIED).build());

        ResponseEntity<?> resp = portService.linkToPortfolio(req);
        assertEquals(HttpStatus.NOT_MODIFIED, resp.getStatusCode());
    }

    /**
     * Tests link to portfolio for HTTP 400.
     */
    @Test
    public void linkToPortfolio400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        LinkTransactionRequest mockedReq =
                new LinkTransactionRequest(false, null, BigInteger.ONE, new ArrayList<>());
        RequestEntity<LinkTransactionRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, LINK_PORTFOLIO_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(portService.linkToPortfolio(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = portService.linkToPortfolio(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests link to portfolio for HTTP 401.
     */
    @Test
    public void linkToPortfolio401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        LinkTransactionRequest mockedReq =
                new LinkTransactionRequest(false, null, BigInteger.ONE, new ArrayList<>());
        RequestEntity<LinkTransactionRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, LINK_PORTFOLIO_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(portService.linkToPortfolio(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = portService.linkToPortfolio(req);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests link to portfolio for HTTP 404.
     */
    @Test
    public void linkToPortfolio404() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        LinkTransactionRequest mockedReq =
                new LinkTransactionRequest(false, null, BigInteger.ONE, new ArrayList<>());
        RequestEntity<LinkTransactionRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, LINK_PORTFOLIO_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(portService.linkToPortfolio(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = portService.linkToPortfolio(req);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    /**
     * Tests link to portfolio for HTTP 406.
     */
    @Test
    public void linkToPortfolio406() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        LinkTransactionRequest mockedReq =
                new LinkTransactionRequest(false, null, BigInteger.ONE, new ArrayList<>());
        RequestEntity<LinkTransactionRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, LINK_PORTFOLIO_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(portService.linkToPortfolio(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(mockedResp));

        ResponseEntity<?> resp = portService.linkToPortfolio(req);
        assertEquals(HttpStatus.NOT_ACCEPTABLE, resp.getStatusCode());
    }

    /**
     * Tests unlink from portfolio for HTTP 200.
     */
    @Test
    public void unlinkFromPortfolio200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        UnlinkTransactionRequest mockedReq =
                new UnlinkTransactionRequest(BigInteger.ONE, new ArrayList<>());
        RequestEntity<UnlinkTransactionRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, UNLINK_PORTFOLIO_URI);
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(portService.unlinkFromPortfolio(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = portService.unlinkFromPortfolio(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests unlink from portfolio for HTTP 304.
     */
    @Test
    public void unlinkFromPortfolio304() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        UnlinkTransactionRequest mockedReq =
                new UnlinkTransactionRequest(BigInteger.ONE, new ArrayList<>());
        RequestEntity<UnlinkTransactionRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, UNLINK_PORTFOLIO_URI);

        when(portService.unlinkFromPortfolio(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_MODIFIED).build());

        ResponseEntity<?> resp = portService.unlinkFromPortfolio(req);
        assertEquals(HttpStatus.NOT_MODIFIED, resp.getStatusCode());
    }

    /**
     * Tests unlink from portfolio for HTTP 400.
     */
    @Test
    public void unlinkFromPortfolio400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        UnlinkTransactionRequest mockedReq =
                new UnlinkTransactionRequest(BigInteger.ONE, new ArrayList<>());
        RequestEntity<UnlinkTransactionRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, UNLINK_PORTFOLIO_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(portService.unlinkFromPortfolio(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = portService.unlinkFromPortfolio(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests unlink from portfolio for HTTP 401.
     */
    @Test
    public void unlinkFromPortfolio401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        UnlinkTransactionRequest mockedReq =
                new UnlinkTransactionRequest(BigInteger.ONE, new ArrayList<>());
        RequestEntity<UnlinkTransactionRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, UNLINK_PORTFOLIO_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(portService.unlinkFromPortfolio(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = portService.unlinkFromPortfolio(req);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests unlink from portfolio for HTTP 404.
     */
    @Test
    public void unlinkFromPortfolio404() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        UnlinkTransactionRequest mockedReq =
                new UnlinkTransactionRequest(BigInteger.ONE, new ArrayList<>());
        RequestEntity<UnlinkTransactionRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, UNLINK_PORTFOLIO_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(portService.unlinkFromPortfolio(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = portService.unlinkFromPortfolio(req);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    /**
     * Tests unlink from portfolio for HTTP 406.
     */
    @Test
    public void unlinkFromPortfolio406() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        UnlinkTransactionRequest mockedReq =
                new UnlinkTransactionRequest(BigInteger.ONE, new ArrayList<>());
        RequestEntity<UnlinkTransactionRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, UNLINK_PORTFOLIO_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(portService.unlinkFromPortfolio(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(mockedResp));

        ResponseEntity<?> resp = portService.unlinkFromPortfolio(req);
        assertEquals(HttpStatus.NOT_ACCEPTABLE, resp.getStatusCode());
    }

}
