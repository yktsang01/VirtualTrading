/*
 * ProfileServiceTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.api.controller;

import com.yktsang.virtrade.api.controller.ProfileService;
import com.yktsang.virtrade.entity.RiskToleranceLevel;
import com.yktsang.virtrade.request.DeactivateAccountRequest;
import com.yktsang.virtrade.request.ProfileRequest;
import com.yktsang.virtrade.request.RegistrationRequest;
import com.yktsang.virtrade.response.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Provides the test cases for <code>ProfileService</code> and <code>ProfileServiceController</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class ProfileServiceTests {

    /**
     * The registration API endpoint.
     */
    private static final URI REGISTRATION_URI = URI.create("/api/v1/registration");
    /**
     * The private profile API endpoint.
     */
    private static final URI PRIVATE_PROFILE_URI = URI.create("/api/v1/member/profile/private");
    /**
     * The public profile API endpoint.
     */
    private static final URI PUBLIC_PROFILE_URI = URI.create("/api/v1/member/profile/public");
    /**
     * The update profile API endpoint.
     */
    private static final URI UPDATE_PROFILE_URI = URI.create("/api/v1/member/profile/update");
    /**
     * The deactivate profile API endpoint.
     */
    private static final URI DEACTIVATE_PROFILE_URI = URI.create("/api/v1/member/profile/deactivate");
    /**
     * The mocked profile service.
     */
    @MockBean
    private ProfileService profileService;

    /**
     * Tests registration for HTTP 200.
     */
    @Test
    public void registration200() {
        RegistrationRequest mockedReq = new RegistrationRequest("user@domain.com",
                "abcd1234", "abcd1234",
                "name", 1990, 10, 15,
                true, "MEDIUM", false, true);
        RequestEntity<RegistrationRequest> req =
                new RequestEntity<>(mockedReq, HttpMethod.POST, REGISTRATION_URI);
        JwtResponse mockedResp = new JwtResponse("spmetoken");
        when(profileService.register(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = profileService.register(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests registration for HTTP 400.
     */
    @Test
    public void registration400() {
        RegistrationRequest mockedReq = new RegistrationRequest("user@domain.com",
                "abcd1234", "pqrs7890",
                "name", 1990, 10, 15,
                true, "MEDIUM", false, true);
        RequestEntity<RegistrationRequest> req =
                new RequestEntity<>(mockedReq, HttpMethod.POST, REGISTRATION_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(profileService.register(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = profileService.register(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests private profile for HTTP 200.
     */
    @Test
    public void privateProfile200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, PRIVATE_PROFILE_URI);
        CompleteProfileResponse mockedResp = new CompleteProfileResponse(
                new TraderProfile("user@domain.com", "name",
                        LocalDate.now(), true, RiskToleranceLevel.MEDIUM,
                        false, true, LocalDateTime.now()),
                new AccountProfile("user@domain.com", false, false));
        when(profileService.privateProfile(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = profileService.privateProfile(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests private profile for HTTP 401.
     */
    @Test
    public void privateProfile401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, PRIVATE_PROFILE_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(profileService.privateProfile(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = profileService.privateProfile(req);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests private profile for HTTP 404.
     */
    @Test
    public void privateProfile404() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, PRIVATE_PROFILE_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(profileService.privateProfile(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = profileService.privateProfile(req);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    /**
     * Tests public profile for HTTP 200.
     */
    @Test
    public void publicProfile200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, PUBLIC_PROFILE_URI);
        ProfileResponse mockedResp = new ProfileResponse(
                new TraderProfile("user@domain.com", "name",
                        LocalDate.now(), true, RiskToleranceLevel.MEDIUM,
                        false, true, LocalDateTime.now()));
        when(profileService.publicProfile(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = profileService.publicProfile(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests public profile for HTTP 401.
     */
    @Test
    public void publicProfile401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, PUBLIC_PROFILE_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(profileService.publicProfile(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = profileService.publicProfile(req);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests public profile for HTTP 404.
     */
    @Test
    public void publicProfile404() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, PUBLIC_PROFILE_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(profileService.publicProfile(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = profileService.publicProfile(req);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    /**
     * Tests update profile for HTTP 200.
     */
    @Test
    public void updateProfile200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        ProfileRequest mockedReq = new ProfileRequest("user@domain.com", "name",
                1990, 10, 15, true,
                "MEDIUM", false, true);
        RequestEntity<ProfileRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, UPDATE_PROFILE_URI);
        ProfileResponse mockedResp = new ProfileResponse(
                new TraderProfile("user@domain.com", "name",
                        LocalDate.now(), true, RiskToleranceLevel.MEDIUM,
                        false, true, LocalDateTime.now()));
        when(profileService.updateProfile(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = profileService.updateProfile(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests update profile for HTTP 400.
     */
    @Test
    public void updateProfile400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        ProfileRequest mockedReq = new ProfileRequest("user@domain.com", "name",
                1990, 10, 15, true,
                "", false, true);
        RequestEntity<ProfileRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, UPDATE_PROFILE_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(profileService.updateProfile(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = profileService.updateProfile(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests update profile for HTTP 401.
     */
    @Test
    public void updateProfile401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        ProfileRequest mockedReq = new ProfileRequest("user@domain.com", "name",
                1990, 10, 15, true,
                "MEDIUM", false, true);
        RequestEntity<ProfileRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, UPDATE_PROFILE_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(profileService.updateProfile(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = profileService.updateProfile(req);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests update profile for HTTP 404.
     */
    @Test
    public void updateProfile404() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        ProfileRequest mockedReq = new ProfileRequest("nobody@domain.com", "name",
                1990, 10, 15, true,
                "MEDIUM", false, true);
        RequestEntity<ProfileRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, UPDATE_PROFILE_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(profileService.updateProfile(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = profileService.updateProfile(req);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    /**
     * Tests deactivate profile for HTTP 200.
     */
    @Test
    public void deactivateProfile200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        DeactivateAccountRequest mockedReq = new DeactivateAccountRequest(true, "reason");
        RequestEntity<DeactivateAccountRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, DEACTIVATE_PROFILE_URI);
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(profileService.deactivateProfile(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = profileService.deactivateProfile(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests deactivate profile for HTTP 304.
     */
    @Test
    public void deactivateProfile304() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        DeactivateAccountRequest mockedReq = new DeactivateAccountRequest(false, null);
        RequestEntity<DeactivateAccountRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, DEACTIVATE_PROFILE_URI);

        when(profileService.deactivateProfile(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_MODIFIED).build());

        ResponseEntity<?> resp = profileService.deactivateProfile(req);
        assertEquals(HttpStatus.NOT_MODIFIED, resp.getStatusCode());
    }

    /**
     * Tests deactivate profile for HTTP 400.
     */
    @Test
    public void deactivateProfile400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        DeactivateAccountRequest mockedReq = new DeactivateAccountRequest(true, "reason");
        RequestEntity<DeactivateAccountRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, DEACTIVATE_PROFILE_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(profileService.deactivateProfile(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = profileService.deactivateProfile(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests deactivate profile for HTTP 401.
     */
    @Test
    public void deactivateProfile401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        DeactivateAccountRequest mockedReq = new DeactivateAccountRequest(true, "reason");
        RequestEntity<DeactivateAccountRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, DEACTIVATE_PROFILE_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(profileService.deactivateProfile(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = profileService.deactivateProfile(req);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests deactivate profile for HTTP 404.
     */
    @Test
    public void deactivateProfile404() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        DeactivateAccountRequest mockedReq = new DeactivateAccountRequest(true, "reason");
        RequestEntity<DeactivateAccountRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, DEACTIVATE_PROFILE_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(profileService.deactivateProfile(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = profileService.deactivateProfile(req);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

}
