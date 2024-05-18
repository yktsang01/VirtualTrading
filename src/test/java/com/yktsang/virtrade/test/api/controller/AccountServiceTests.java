/*
 * AccountServiceTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.api.controller;

import com.yktsang.virtrade.api.controller.AccountService;
import com.yktsang.virtrade.request.*;
import com.yktsang.virtrade.response.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import java.net.URI;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Provides the test cases for <code>AccountService</code> and <code>AccountServiceController</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class AccountServiceTests {

    /**
     * The login API endpoint.
     */
    private static final URI LOGIN_URI = URI.create("/api/v1/login");
    /**
     * The reset password API endpoint.
     */
    private static final URI RESET_PASSWORD_URI = URI.create("/api/v1/password/reset");
    /**
     * The update account API endpoint.
     */
    private static final URI UPDATE_ACCOUNT_URI = URI.create("/api/v1/member/account/update");
    /**
     * The request admin access API endpoint.
     */
    private static final URI REQUEST_ADMIN_URI = URI.create("/api/v1/member/admin/request");
    /**
     * The admin requests API endpoint.
     */
    private static final URI ADMIN_REQUESTS_URI = URI.create("/api/v1/admin/requests");
    /**
     * The admin accesses API endpoint.
     */
    private static final URI ADMIN_ACCESSES_URI = URI.create("/api/v1/admin/accesses");
    /**
     * The grant admin accesses API endpoint.
     */
    private static final URI GRANT_ADMIN_ACCESSES_URI = URI.create("/api/v1/admin/accesses/grant");
    /**
     * The revoke admin accesses API endpoint.
     */
    private static final URI REVOKE_ADMIN_ACCESSES_URI = URI.create("/api/v1/admin/accesses/revoke");
    /**
     * The mocked account service.
     */
    @MockBean
    private AccountService accountService;
    /**
     * The mocked authentication manager.
     */
    @MockBean
    private AuthenticationManager authenticationManager;

    /**
     * Tests login for HTTP 200.
     */
    @Test
    public void login200() {
        Authentication auth = mock(Authentication.class);
        auth.setAuthenticated(true);
        when(auth.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any())).thenReturn(auth);

        AuthRequest mockedReq = new AuthRequest("user@domain.com", "abcd1234");
        JwtResponse mockedResp = new JwtResponse("sometoken");

        RequestEntity<AuthRequest> req =
                new RequestEntity<>(mockedReq, HttpMethod.POST, LOGIN_URI);

        when(accountService.login(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = accountService.login(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests login for HTTP 400.
     */
    @Test
    public void login400() {
        AuthRequest mockedReq = new AuthRequest("user@domain.com", "");
        ErrorResponse mockedResp = new ErrorResponse("error");

        RequestEntity<AuthRequest> req =
                new RequestEntity<>(mockedReq, HttpMethod.POST, LOGIN_URI);

        when(accountService.login(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = accountService.login(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests login for HTTP 403.
     */
    @Test
    public void login403() {
        Authentication auth = mock(Authentication.class);
        auth.setAuthenticated(false);
        when(auth.isAuthenticated()).thenReturn(false);
        when(authenticationManager.authenticate(any())).thenReturn(auth);

        AuthRequest mockedReq = new AuthRequest("user@domain.com", "abcd1234");
        ErrorResponse mockedResp = new ErrorResponse("error");

        RequestEntity<AuthRequest> req =
                new RequestEntity<>(mockedReq, HttpMethod.POST, LOGIN_URI);

        when(accountService.login(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.FORBIDDEN).body(mockedResp));

        ResponseEntity<?> resp = accountService.login(req);
        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    }

    /**
     * Tests login for HTTP 404.
     */
    @Test
    public void login404() {
        Authentication auth = mock(Authentication.class);
        auth.setAuthenticated(false);
        when(auth.isAuthenticated()).thenReturn(false);
        when(authenticationManager.authenticate(any())).thenReturn(auth);

        AuthRequest mockedReq = new AuthRequest("nobody@domain.com", "abcd1234");
        ErrorResponse mockedResp = new ErrorResponse("error");

        RequestEntity<AuthRequest> req =
                new RequestEntity<>(mockedReq, HttpMethod.POST, LOGIN_URI);

        when(accountService.login(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = accountService.login(req);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    /**
     * Tests reset password for HTTP 200.
     */
    @Test
    public void resetPassword200() {
        ResetPasswordRequest mockedReq = new ResetPasswordRequest("user@domain.com",
                "abcd1234", "abcd1234");
        SuccessResponse mockedResp = new SuccessResponse("success");

        RequestEntity<ResetPasswordRequest> req =
                new RequestEntity<>(mockedReq, HttpMethod.POST, RESET_PASSWORD_URI);

        when(accountService.resetPassword(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = accountService.resetPassword(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests reset password for HTTP 400.
     */
    @Test
    public void resetPassword400() {
        ResetPasswordRequest mockedReq = new ResetPasswordRequest("user@domain.com",
                "abcd1234", "pqrs7890");
        ErrorResponse mockedResp = new ErrorResponse("error");

        RequestEntity<ResetPasswordRequest> req =
                new RequestEntity<>(mockedReq, HttpMethod.POST, RESET_PASSWORD_URI);

        when(accountService.resetPassword(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = accountService.resetPassword(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests reset password for HTTP 400.
     */
    @Test
    public void resetPassword404() {
        ResetPasswordRequest mockedReq = new ResetPasswordRequest("nobody@domain.com",
                "abcd1234", "abcd1234");
        ErrorResponse mockedResp = new ErrorResponse("error");

        RequestEntity<ResetPasswordRequest> req =
                new RequestEntity<>(mockedReq, HttpMethod.POST, RESET_PASSWORD_URI);

        when(accountService.resetPassword(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = accountService.resetPassword(req);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    /**
     * Tests update account for HTTP 200.
     */
    @Test
    public void updateAccount200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        ResetPasswordRequest mockedReq = new ResetPasswordRequest("user@domain.com",
                "abcd1234", "abcd1234");
        SuccessResponse mockedResp = new SuccessResponse("success");

        RequestEntity<ResetPasswordRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, UPDATE_ACCOUNT_URI);

        when(accountService.updateAccount(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = accountService.updateAccount(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests update account for HTTP 400.
     */
    @Test
    public void updateAccount400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        ResetPasswordRequest mockedReq = new ResetPasswordRequest("user@domain.com",
                "abcd1234", "pqrs7890");
        ErrorResponse mockedResp = new ErrorResponse("error");

        RequestEntity<ResetPasswordRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, UPDATE_ACCOUNT_URI);

        when(accountService.updateAccount(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = accountService.updateAccount(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests update account for HTTP 401.
     */
    @Test
    public void updateAccount401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        ResetPasswordRequest mockedReq = new ResetPasswordRequest("user@domain.com",
                "abcd1234", "abcd1234");
        ErrorResponse mockedResp = new ErrorResponse("error");

        RequestEntity<ResetPasswordRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, UPDATE_ACCOUNT_URI);

        when(accountService.updateAccount(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = accountService.updateAccount(req);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests update account for HTTP 404.
     */
    @Test
    public void updateAccount404() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        ResetPasswordRequest mockedReq = new ResetPasswordRequest("nobody@domain.com",
                "abcd1234", "abcd1234");
        ErrorResponse mockedResp = new ErrorResponse("error");

        RequestEntity<ResetPasswordRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, UPDATE_ACCOUNT_URI);

        when(accountService.updateAccount(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = accountService.updateAccount(req);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    /**
     * Tests request admin access for HTTP 200.
     */
    @Test
    public void requestAdminAccess200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestAdminAccessRequest mockedReq = new RequestAdminAccessRequest(true);
        SuccessResponse mockedResp = new SuccessResponse("success");

        RequestEntity<RequestAdminAccessRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, REQUEST_ADMIN_URI);

        when(accountService.requestAdminAccess(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = accountService.requestAdminAccess(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests request admin access for HTTP 304.
     */
    @Test
    public void requestAdminAccess304() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestAdminAccessRequest mockedReq = new RequestAdminAccessRequest(false);

        RequestEntity<RequestAdminAccessRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, REQUEST_ADMIN_URI);

        when(accountService.requestAdminAccess(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_MODIFIED).build());

        ResponseEntity<?> resp = accountService.requestAdminAccess(req);
        assertEquals(HttpStatus.NOT_MODIFIED, resp.getStatusCode());
    }

    /**
     * Tests request admin access for HTTP 400.
     */
    @Test
    public void requestAdminAccess400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestAdminAccessRequest mockedReq = new RequestAdminAccessRequest(null);
        ErrorResponse mockedResp = new ErrorResponse("error");

        RequestEntity<RequestAdminAccessRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, REQUEST_ADMIN_URI);

        when(accountService.requestAdminAccess(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = accountService.requestAdminAccess(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests request admin access for HTTP 401.
     */
    @Test
    public void requestAdminAccess401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestAdminAccessRequest mockedReq = new RequestAdminAccessRequest(true);
        ErrorResponse mockedResp = new ErrorResponse("error");

        RequestEntity<RequestAdminAccessRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, REQUEST_ADMIN_URI);

        when(accountService.requestAdminAccess(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = accountService.requestAdminAccess(req);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests request admin access for HTTP 404.
     */
    @Test
    public void requestAdminAccess404() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestAdminAccessRequest mockedReq = new RequestAdminAccessRequest(true);
        ErrorResponse mockedResp = new ErrorResponse("error");

        RequestEntity<RequestAdminAccessRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, REQUEST_ADMIN_URI);

        when(accountService.requestAdminAccess(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = accountService.requestAdminAccess(req);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    /**
     * Tests admin requests for HTTP 200.
     */
    @Test
    public void adminRequests200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        AdminRequestResponse mockedResp = new AdminRequestResponse(new ArrayList<>());

        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ADMIN_REQUESTS_URI);

        when(accountService.adminRequests(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = accountService.adminRequests(req, 1, 5);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests admin requests for HTTP 204.
     */
    @Test
    public void adminRequests204() {
        HttpHeaders mockedHeaders = new HttpHeaders();

        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ADMIN_REQUESTS_URI);

        when(accountService.adminRequests(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        ResponseEntity<?> resp = accountService.adminRequests(req, 1, 5);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    /**
     * Tests admin requests for HTTP 401.
     */
    @Test
    public void adminRequests401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        AdminRequestResponse mockedResp = new AdminRequestResponse(new ArrayList<>());

        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ADMIN_REQUESTS_URI);

        when(accountService.adminRequests(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = accountService.adminRequests(req, 1, 5);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests admin requests for HTTP 403.
     */
    @Test
    public void adminRequests403() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        AdminRequestResponse mockedResp = new AdminRequestResponse(new ArrayList<>());

        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ADMIN_REQUESTS_URI);

        when(accountService.adminRequests(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.FORBIDDEN).body(mockedResp));

        ResponseEntity<?> resp = accountService.adminRequests(req, 1, 5);
        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    }

    /**
     * Tests admin accesses for HTTP 200.
     */
    @Test
    public void adminAccesses200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        AdminAccessResponse mockedResp = new AdminAccessResponse(new ArrayList<>());

        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ADMIN_ACCESSES_URI);

        when(accountService.adminAccesses(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = accountService.adminAccesses(req, 1, 5);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests admin accesses for HTTP 204.
     */
    @Test
    public void adminAccesses204() {
        HttpHeaders mockedHeaders = new HttpHeaders();

        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ADMIN_ACCESSES_URI);

        when(accountService.adminAccesses(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        ResponseEntity<?> resp = accountService.adminAccesses(req, 1, 5);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    /**
     * Tests admin accesses for HTTP 401.
     */
    @Test
    public void adminAccesses401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        AdminAccessResponse mockedResp = new AdminAccessResponse(new ArrayList<>());

        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ADMIN_ACCESSES_URI);

        when(accountService.adminAccesses(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = accountService.adminAccesses(req, 1, 5);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests admin accesses for HTTP 403.
     */
    @Test
    public void adminAccesses403() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        AdminAccessResponse mockedResp = new AdminAccessResponse(new ArrayList<>());

        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ADMIN_ACCESSES_URI);

        when(accountService.adminAccesses(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.FORBIDDEN).body(mockedResp));

        ResponseEntity<?> resp = accountService.adminAccesses(req, 1, 5);
        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    }

    /**
     * Tests grant admin accesses for HTTP 200.
     */
    @Test
    public void grantAdminAccesses200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        GrantAdminAccessRequest mockedReq = new GrantAdminAccessRequest(new ArrayList<>());
        SuccessResponse mockedResp = new SuccessResponse("success");

        RequestEntity<GrantAdminAccessRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, GRANT_ADMIN_ACCESSES_URI);

        when(accountService.grantAdminAccesses(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = accountService.grantAdminAccesses(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests grant admin accesses for HTTP 304.
     */
    @Test
    public void grantAdminAccesses304() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        GrantAdminAccessRequest mockedReq = new GrantAdminAccessRequest(new ArrayList<>());

        RequestEntity<GrantAdminAccessRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, GRANT_ADMIN_ACCESSES_URI);

        when(accountService.grantAdminAccesses(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_MODIFIED).build());

        ResponseEntity<?> resp = accountService.grantAdminAccesses(req);
        assertEquals(HttpStatus.NOT_MODIFIED, resp.getStatusCode());
    }

    /**
     * Tests grant admin accesses for HTTP 400.
     */
    @Test
    public void grantAdminAccesses400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        GrantAdminAccessRequest mockedReq = new GrantAdminAccessRequest(null);
        ErrorResponse mockedResp = new ErrorResponse("error");

        RequestEntity<GrantAdminAccessRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, GRANT_ADMIN_ACCESSES_URI);

        when(accountService.grantAdminAccesses(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = accountService.grantAdminAccesses(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests grant admin accesses for HTTP 401.
     */
    @Test
    public void grantAdminAccesses401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        GrantAdminAccessRequest mockedReq = new GrantAdminAccessRequest(new ArrayList<>());
        ErrorResponse mockedResp = new ErrorResponse("error");

        RequestEntity<GrantAdminAccessRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, GRANT_ADMIN_ACCESSES_URI);

        when(accountService.grantAdminAccesses(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = accountService.grantAdminAccesses(req);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests grant admin accesses for HTTP 403.
     */
    @Test
    public void grantAdminAccesses403() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        GrantAdminAccessRequest mockedReq = new GrantAdminAccessRequest(new ArrayList<>());
        ErrorResponse mockedResp = new ErrorResponse("error");

        RequestEntity<GrantAdminAccessRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, GRANT_ADMIN_ACCESSES_URI);

        when(accountService.grantAdminAccesses(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.FORBIDDEN).body(mockedResp));

        ResponseEntity<?> resp = accountService.grantAdminAccesses(req);
        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    }

    /**
     * Tests revoke admin accesses for HTTP 200.
     */
    @Test
    public void revokeAdminAccesses200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RevokeAdminAccessRequest mockedReq = new RevokeAdminAccessRequest(new ArrayList<>());
        SuccessResponse mockedResp = new SuccessResponse("success");

        RequestEntity<RevokeAdminAccessRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, REVOKE_ADMIN_ACCESSES_URI);

        when(accountService.revokeAdminAccesses(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = accountService.revokeAdminAccesses(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests revoke admin accesses for HTTP 304.
     */
    @Test
    public void revokeAdminAccesses304() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RevokeAdminAccessRequest mockedReq = new RevokeAdminAccessRequest(new ArrayList<>());

        RequestEntity<RevokeAdminAccessRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, REVOKE_ADMIN_ACCESSES_URI);

        when(accountService.revokeAdminAccesses(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_MODIFIED).build());

        ResponseEntity<?> resp = accountService.revokeAdminAccesses(req);
        assertEquals(HttpStatus.NOT_MODIFIED, resp.getStatusCode());
    }

    /**
     * Tests revoke admin accesses for HTTP 400.
     */
    @Test
    public void revokeAdminAccesses400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RevokeAdminAccessRequest mockedReq = new RevokeAdminAccessRequest(null);
        ErrorResponse mockedResp = new ErrorResponse("error");

        RequestEntity<RevokeAdminAccessRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, REVOKE_ADMIN_ACCESSES_URI);

        when(accountService.revokeAdminAccesses(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = accountService.revokeAdminAccesses(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests revoke admin accesses for HTTP 401.
     */
    @Test
    public void revokeAdminAccesses401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RevokeAdminAccessRequest mockedReq = new RevokeAdminAccessRequest(new ArrayList<>());
        ErrorResponse mockedResp = new ErrorResponse("error");

        RequestEntity<RevokeAdminAccessRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, REVOKE_ADMIN_ACCESSES_URI);

        when(accountService.revokeAdminAccesses(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = accountService.revokeAdminAccesses(req);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests revoke admin accesses for HTTP 403.
     */
    @Test
    public void revokeAdminAccesses403() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RevokeAdminAccessRequest mockedReq = new RevokeAdminAccessRequest(new ArrayList<>());
        ErrorResponse mockedResp = new ErrorResponse("error");

        RequestEntity<RevokeAdminAccessRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, REVOKE_ADMIN_ACCESSES_URI);

        when(accountService.revokeAdminAccesses(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.FORBIDDEN).body(mockedResp));

        ResponseEntity<?> resp = accountService.revokeAdminAccesses(req);
        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    }

}
