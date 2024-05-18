/*
 * WatchListServiceTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.api.controller;

import com.yktsang.virtrade.api.controller.WatchListService;
import com.yktsang.virtrade.request.AddWatchListStockRequest;
import com.yktsang.virtrade.request.RemoveWatchListStockRequest;
import com.yktsang.virtrade.response.ErrorResponse;
import com.yktsang.virtrade.response.SuccessResponse;
import com.yktsang.virtrade.response.WatchListResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;

import java.net.URI;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Provides the test cases for <code>WatchListService</code> and <code>WatchListServiceController</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class WatchListServiceTests {

    /**
     * The watch list API endpoint.
     */
    private static final URI WATCH_LIST_URI = URI.create("/api/v1/member/watchList");
    /**
     * The watch list with currency API endpoint.
     */
    private static final URI WATCH_LIST_CURRENCY_URI = URI.create("/api/v1/member/watchList/XXX");
    /**
     * The add to watch list API endpoint.
     */
    private static final URI ADD_WATCH_LIST_URI = URI.create("/api/v1/member/watchList/add");
    /**
     * The remove from watch list API endpoint.
     */
    private static final URI REMOVE_WATCH_LIST_URI = URI.create("/api/v1/member/watchList/remove");
    /**
     * The mocked watch list service.
     */
    @MockBean
    private WatchListService watchListService;

    /**
     * Tests watch list for HTTP 200.
     */
    @Test
    public void watchList200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, WATCH_LIST_URI);
        WatchListResponse mockedResp = new WatchListResponse(new ArrayList<>());
        when(watchListService.watchList(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = watchListService.watchList(req, 1, 5);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests watch list for HTTP 204.
     */
    @Test
    public void watchList204() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, WATCH_LIST_URI);
        when(watchListService.watchList(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        ResponseEntity<?> resp = watchListService.watchList(req, 1, 5);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    /**
     * Tests watch list for HTTP 401.
     */
    @Test
    public void watchList401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, WATCH_LIST_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(watchListService.watchList(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = watchListService.watchList(req, 1, 5);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests watch list with currency for HTTP 200.
     */
    @Test
    public void watchListWithCurrency200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, WATCH_LIST_CURRENCY_URI);
        WatchListResponse mockedResp = new WatchListResponse(new ArrayList<>());
        when(watchListService.watchList(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = watchListService.watchList(req, "XXX", 1, 5);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests watch list with currency for HTTP 204.
     */
    @Test
    public void watchListWithCurrency204() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, WATCH_LIST_CURRENCY_URI);
        when(watchListService.watchList(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        ResponseEntity<?> resp = watchListService.watchList(req, "XXX", 1, 5);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    /**
     * Tests watch list with currency for HTTP 400.
     */
    @Test
    public void watchListWithCurrency400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, WATCH_LIST_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(watchListService.watchList(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = watchListService.watchList(req, "XXX", 1, 5);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests watch list with currency for HTTP 401.
     */
    @Test
    public void watchListWithCurrency401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, WATCH_LIST_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(watchListService.watchList(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = watchListService.watchList(req, "XXX", 1, 5);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests watch list with currency for HTTP 404.
     */
    @Test
    public void watchListWithCurrency404() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, WATCH_LIST_CURRENCY_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(watchListService.watchList(req, "XXX", 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = watchListService.watchList(req, "XXX", 1, 5);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    /**
     * Tests add to watch list for HTTP 200.
     */
    @Test
    public void addToWatchList200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        AddWatchListStockRequest mockedReq = new AddWatchListStockRequest(new ArrayList<>());
        RequestEntity<AddWatchListStockRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, ADD_WATCH_LIST_URI);
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(watchListService.addToWatchList(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = watchListService.addToWatchList(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests add to watch list for HTTP 304.
     */
    @Test
    public void addToWatchList304() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        AddWatchListStockRequest mockedReq = new AddWatchListStockRequest(new ArrayList<>());
        RequestEntity<AddWatchListStockRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, ADD_WATCH_LIST_URI);

        when(watchListService.addToWatchList(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_MODIFIED).build());

        ResponseEntity<?> resp = watchListService.addToWatchList(req);
        assertEquals(HttpStatus.NOT_MODIFIED, resp.getStatusCode());
    }

    /**
     * Tests add to watch list for HTTP 400.
     */
    @Test
    public void addToWatchList400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        AddWatchListStockRequest mockedReq = new AddWatchListStockRequest(new ArrayList<>());
        RequestEntity<AddWatchListStockRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, ADD_WATCH_LIST_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(watchListService.addToWatchList(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = watchListService.addToWatchList(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests add to watch list for HTTP 401.
     */
    @Test
    public void addToWatchList401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        AddWatchListStockRequest mockedReq = new AddWatchListStockRequest(new ArrayList<>());
        RequestEntity<AddWatchListStockRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, ADD_WATCH_LIST_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(watchListService.addToWatchList(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = watchListService.addToWatchList(req);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests remove from watch list for HTTP 200.
     */
    @Test
    public void removeFromWatchList200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RemoveWatchListStockRequest mockedReq = new RemoveWatchListStockRequest(new ArrayList<>());
        RequestEntity<RemoveWatchListStockRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, REMOVE_WATCH_LIST_URI);
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(watchListService.removeFromWatchList(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = watchListService.removeFromWatchList(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests remove from watch list for HTTP 304.
     */
    @Test
    public void removeFromWatchList304() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RemoveWatchListStockRequest mockedReq = new RemoveWatchListStockRequest(new ArrayList<>());
        RequestEntity<RemoveWatchListStockRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, REMOVE_WATCH_LIST_URI);

        when(watchListService.removeFromWatchList(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_MODIFIED).build());

        ResponseEntity<?> resp = watchListService.removeFromWatchList(req);
        assertEquals(HttpStatus.NOT_MODIFIED, resp.getStatusCode());
    }

    /**
     * Tests remove from watch list for HTTP 400.
     */
    @Test
    public void removeFromWatchList400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RemoveWatchListStockRequest mockedReq = new RemoveWatchListStockRequest(new ArrayList<>());
        RequestEntity<RemoveWatchListStockRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, REMOVE_WATCH_LIST_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(watchListService.removeFromWatchList(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = watchListService.removeFromWatchList(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests remove from watch list for HTTP 401.
     */
    @Test
    public void removeFromWatchList401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RemoveWatchListStockRequest mockedReq = new RemoveWatchListStockRequest(new ArrayList<>());
        RequestEntity<RemoveWatchListStockRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, REMOVE_WATCH_LIST_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(watchListService.removeFromWatchList(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = watchListService.removeFromWatchList(req);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

}
