/*
 * RefreshTokenServiceTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.api.controller;

import com.yktsang.virtrade.api.controller.RefreshTokenService;
import com.yktsang.virtrade.request.RefreshTokenRequest;
import com.yktsang.virtrade.response.ErrorResponse;
import com.yktsang.virtrade.response.JwtResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Provides the test cases for <code>RefreshTokenService</code> and <code>RefreshTokenServiceController</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class RefreshTokenServiceTests {

    /**
     * The refresh token API endpoint.
     */
    private static final URI REFRESH_TOKEN_URI = URI.create("/api/v1/refreshToken");
    /**
     * The mocked refresh token service.
     */
    @MockBean
    private RefreshTokenService refreshTokenService;

    /**
     * Tests refresh token for HTTP 200.
     */
    @Test
    public void refreshToken200() {
        RefreshTokenRequest mockedReq = new RefreshTokenRequest("user@domain.com", "existingtoken");
        JwtResponse mockedResp = new JwtResponse("newtoken");

        RequestEntity<RefreshTokenRequest> req =
                new RequestEntity<>(mockedReq, HttpMethod.POST, REFRESH_TOKEN_URI);

        when(refreshTokenService.refreshToken(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = refreshTokenService.refreshToken(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests refresh token for HTTP 304.
     */
    @Test
    public void refreshToken304() {
        RefreshTokenRequest mockedReq = new RefreshTokenRequest("user@domain.com", "existingtoken");

        RequestEntity<RefreshTokenRequest> req =
                new RequestEntity<>(mockedReq, HttpMethod.POST, REFRESH_TOKEN_URI);

        when(refreshTokenService.refreshToken(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_MODIFIED).build());

        ResponseEntity<?> resp = refreshTokenService.refreshToken(req);
        assertEquals(HttpStatus.NOT_MODIFIED, resp.getStatusCode());
    }

    /**
     * Tests refresh token for HTTP 400.
     */
    @Test
    public void refreshToken400() {
        RefreshTokenRequest mockedReq = new RefreshTokenRequest("user@domain.com", "");
        ErrorResponse mockedResp = new ErrorResponse("error");

        RequestEntity<RefreshTokenRequest> req =
                new RequestEntity<>(mockedReq, HttpMethod.POST, REFRESH_TOKEN_URI);

        when(refreshTokenService.refreshToken(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = refreshTokenService.refreshToken(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

}
