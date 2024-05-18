/*
 * IsoDataServiceTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.api.controller;

import com.yktsang.virtrade.api.controller.IsoDataService;
import com.yktsang.virtrade.request.ActivateIsoRequest;
import com.yktsang.virtrade.request.CreateIsoRequest;
import com.yktsang.virtrade.request.DeactivateIsoRequest;
import com.yktsang.virtrade.request.UpdateIsoRequest;
import com.yktsang.virtrade.response.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Provides the test cases for <code>IsoDataService</code> and <code>IsoDataServiceController</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
public class IsoDataServiceTests {

    /**
     * The active currencies API endpoint.
     */
    private static final URI ACTIVE_CURRENCIES_URI = URI.create("/api/v1/member/iso/currencies");
    /**
     * The ISO data API endpoint.
     */
    private static final URI ISO_DATA_URI = URI.create("/api/v1/admin/iso");
    /**
     * The ISO data with country code API endpoint.
     */
    private static final URI ISO_DATA_CODE_URI = URI.create("/api/v1/admin/iso/XX");
    /**
     * The create ISO data API endpoint.
     */
    private static final URI CREATE_ISO_URI = URI.create("/api/v1/admin/iso/create");
    /**
     * The update ISO data API endpoint.
     */
    private static final URI UPDATE_ISO_URI = URI.create("/api/v1/admin/iso/update");
    /**
     * The activate ISO data API endpoint.
     */
    private static final URI ACTIVATE_ISO_URI = URI.create("/api/v1/admin/iso/activate");
    /**
     * The deactivate ISO data API endpoint.
     */
    private static final URI DEACTIVATE_ISO_URI = URI.create("/api/v1/admin/iso/deactivate");
    /**
     * The mocked ISO data service.
     */
    @MockBean
    private IsoDataService isoDataService;

    /**
     * Tests active currencies for HTTP 200.
     */
    @Test
    public void activeCurrencies200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ACTIVE_CURRENCIES_URI);
        IsoCurrencyResponse mockedResp = new IsoCurrencyResponse(new HashSet<>());
        when(isoDataService.activeCurrencies(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = isoDataService.activeCurrencies(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests active currencies for HTTP 204.
     */
    @Test
    public void activeCurrencies204() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ACTIVE_CURRENCIES_URI);
        when(isoDataService.activeCurrencies(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        ResponseEntity<?> resp = isoDataService.activeCurrencies(req);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    /**
     * Tests active currencies for HTTP 401.
     */
    @Test
    public void activeCurrencies401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ACTIVE_CURRENCIES_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(isoDataService.activeCurrencies(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = isoDataService.activeCurrencies(req);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests ISO data for HTTP 200.
     */
    @Test
    public void isoData200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ISO_DATA_URI);
        IsoDataResponse mockedResp = new IsoDataResponse(new ArrayList<>());
        when(isoDataService.isoData(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = isoDataService.isoData(req, 1, 5);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests ISO data for HTTP 204.
     */
    @Test
    public void isoData204() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ISO_DATA_URI);
        when(isoDataService.isoData(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        ResponseEntity<?> resp = isoDataService.isoData(req, 1, 5);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    /**
     * Tests ISO data for HTTP 401.
     */
    @Test
    public void isoData401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ISO_DATA_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(isoDataService.isoData(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = isoDataService.isoData(req, 1, 5);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests ISO data for HTTP 403.
     */
    @Test
    public void isoData403() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ISO_DATA_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(isoDataService.isoData(req, 1, 5))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.FORBIDDEN).body(mockedResp));

        ResponseEntity<?> resp = isoDataService.isoData(req, 1, 5);
        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    }

    /**
     * Tests ISO data with country code for HTTP 200.
     */
    @Test
    public void isoDataWithCountryCode200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ISO_DATA_CODE_URI);
        IsoCodeResponse mockedResp = new IsoCodeResponse(new IsoCode("XX", "name",
                "XXX", "name", null,
                true, LocalDateTime.now(), "admin@domain.com", null, null));
        when(isoDataService.isoData(req, "XX"))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = isoDataService.isoData(req, "XX");
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests ISO data with country code for HTTP 400.
     */
    @Test
    public void isoDataWithCountryCode400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ISO_DATA_CODE_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(isoDataService.isoData(req, "XX"))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = isoDataService.isoData(req, "XX");
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests ISO data with country code for HTTP 401.
     */
    @Test
    public void isoDataWithCountryCode401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ISO_DATA_CODE_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(isoDataService.isoData(req, "XX"))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = isoDataService.isoData(req, "XX");
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests ISO data with country code for HTTP 403.
     */
    @Test
    public void isoDataWithCountryCode403() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ISO_DATA_CODE_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(isoDataService.isoData(req, "XX"))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.FORBIDDEN).body(mockedResp));

        ResponseEntity<?> resp = isoDataService.isoData(req, "XX");
        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    }

    /**
     * Tests ISO data with country code for HTTP 404.
     */
    @Test
    public void isoDataWithCountryCode404() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        RequestEntity<Void> req =
                new RequestEntity<>(mockedHeaders, HttpMethod.GET, ISO_DATA_CODE_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(isoDataService.isoData(req, "XX"))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = isoDataService.isoData(req, "XX");
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    /**
     * Tests create ISO data for HTTP 201.
     */
    @Test
    public void createIso201() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        CreateIsoRequest mockedReq = new CreateIsoRequest("XX", "name",
                "XXX", "name", null, true);
        RequestEntity<CreateIsoRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, CREATE_ISO_URI);
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(isoDataService.createIsoData(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.CREATED).body(mockedResp));

        ResponseEntity<?> resp = isoDataService.createIsoData(req);
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
    }

    /**
     * Tests create ISO data for HTTP 400.
     */
    @Test
    public void createIso400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        CreateIsoRequest mockedReq = new CreateIsoRequest("XX", "name",
                "XXX", "name", null, true);
        RequestEntity<CreateIsoRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, CREATE_ISO_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(isoDataService.createIsoData(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = isoDataService.createIsoData(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests create ISO data for HTTP 401.
     */
    @Test
    public void createIso401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        CreateIsoRequest mockedReq = new CreateIsoRequest("XX", "name",
                "XXX", "name", null, true);
        RequestEntity<CreateIsoRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, CREATE_ISO_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(isoDataService.createIsoData(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = isoDataService.createIsoData(req);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests create ISO data for HTTP 403.
     */
    @Test
    public void createIso403() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        CreateIsoRequest mockedReq = new CreateIsoRequest("XX", "name",
                "XXX", "name", null, true);
        RequestEntity<CreateIsoRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, CREATE_ISO_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(isoDataService.createIsoData(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.FORBIDDEN).body(mockedResp));

        ResponseEntity<?> resp = isoDataService.createIsoData(req);
        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    }

    /**
     * Tests update ISO data for HTTP 200.
     */
    @Test
    public void updateIso200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        UpdateIsoRequest mockedReq = new UpdateIsoRequest("XX", "name",
                "XXX", "name", null, true);
        RequestEntity<UpdateIsoRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, UPDATE_ISO_URI);
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(isoDataService.updateIsoData(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.CREATED).body(mockedResp));

        ResponseEntity<?> resp = isoDataService.updateIsoData(req);
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
    }

    /**
     * Tests update ISO data for HTTP 400.
     */
    @Test
    public void updateIso400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        UpdateIsoRequest mockedReq = new UpdateIsoRequest("XX", "name",
                "XXX", "name", null, true);
        RequestEntity<UpdateIsoRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, UPDATE_ISO_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(isoDataService.updateIsoData(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = isoDataService.updateIsoData(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests update ISO data for HTTP 401.
     */
    @Test
    public void updateIso401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        UpdateIsoRequest mockedReq = new UpdateIsoRequest("XX", "name",
                "XXX", "name", null, true);
        RequestEntity<UpdateIsoRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, UPDATE_ISO_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(isoDataService.updateIsoData(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = isoDataService.updateIsoData(req);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests update ISO data for HTTP 403.
     */
    @Test
    public void updateIso403() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        UpdateIsoRequest mockedReq = new UpdateIsoRequest("XX", "name",
                "XXX", "name", null, false);
        RequestEntity<UpdateIsoRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, UPDATE_ISO_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(isoDataService.updateIsoData(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.FORBIDDEN).body(mockedResp));

        ResponseEntity<?> resp = isoDataService.updateIsoData(req);
        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    }

    /**
     * Tests update ISO data for HTTP 404.
     */
    @Test
    public void updateIso404() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        UpdateIsoRequest mockedReq = new UpdateIsoRequest("XX", "name",
                "XXX", "name", null, false);
        RequestEntity<UpdateIsoRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, UPDATE_ISO_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(isoDataService.updateIsoData(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockedResp));

        ResponseEntity<?> resp = isoDataService.updateIsoData(req);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    /**
     * Tests activate ISO data for HTTP 200.
     */
    @Test
    public void activateIso200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        ActivateIsoRequest mockedReq = new ActivateIsoRequest(new ArrayList<>());
        RequestEntity<ActivateIsoRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, ACTIVATE_ISO_URI);
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(isoDataService.activateIsoData(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = isoDataService.activateIsoData(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests activate ISO data for HTTP 304.
     */
    @Test
    public void activateIso304() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        ActivateIsoRequest mockedReq = new ActivateIsoRequest(new ArrayList<>());
        RequestEntity<ActivateIsoRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, ACTIVATE_ISO_URI);

        when(isoDataService.activateIsoData(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_MODIFIED).build());

        ResponseEntity<?> resp = isoDataService.activateIsoData(req);
        assertEquals(HttpStatus.NOT_MODIFIED, resp.getStatusCode());
    }

    /**
     * Tests activate ISO data for HTTP 400.
     */
    @Test
    public void activateIso400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        ActivateIsoRequest mockedReq = new ActivateIsoRequest(new ArrayList<>());
        RequestEntity<ActivateIsoRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, ACTIVATE_ISO_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(isoDataService.activateIsoData(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = isoDataService.activateIsoData(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests activate ISO data for HTTP 401.
     */
    @Test
    public void activateIso401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        ActivateIsoRequest mockedReq = new ActivateIsoRequest(new ArrayList<>());
        RequestEntity<ActivateIsoRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, ACTIVATE_ISO_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(isoDataService.activateIsoData(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = isoDataService.activateIsoData(req);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests activate ISO data for HTTP 403.
     */
    @Test
    public void activateIso403() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        ActivateIsoRequest mockedReq = new ActivateIsoRequest(new ArrayList<>());
        RequestEntity<ActivateIsoRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, ACTIVATE_ISO_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(isoDataService.activateIsoData(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.FORBIDDEN).body(mockedResp));

        ResponseEntity<?> resp = isoDataService.activateIsoData(req);
        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    }

    /**
     * Tests deactivate ISO data for HTTP 200.
     */
    @Test
    public void deactivateIso200() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        DeactivateIsoRequest mockedReq = new DeactivateIsoRequest(new ArrayList<>());
        RequestEntity<DeactivateIsoRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, DEACTIVATE_ISO_URI);
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(isoDataService.deactivateIsoData(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        ResponseEntity<?> resp = isoDataService.deactivateIsoData(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    /**
     * Tests deactivate ISO data for HTTP 304.
     */
    @Test
    public void deactivateIso304() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        DeactivateIsoRequest mockedReq = new DeactivateIsoRequest(new ArrayList<>());
        RequestEntity<DeactivateIsoRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, DEACTIVATE_ISO_URI);

        when(isoDataService.deactivateIsoData(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.NOT_MODIFIED).build());

        ResponseEntity<?> resp = isoDataService.deactivateIsoData(req);
        assertEquals(HttpStatus.NOT_MODIFIED, resp.getStatusCode());
    }

    /**
     * Tests deactivate ISO data for HTTP 400.
     */
    @Test
    public void deactivateIso400() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        DeactivateIsoRequest mockedReq = new DeactivateIsoRequest(new ArrayList<>());
        RequestEntity<DeactivateIsoRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, DEACTIVATE_ISO_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(isoDataService.deactivateIsoData(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockedResp));

        ResponseEntity<?> resp = isoDataService.deactivateIsoData(req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    /**
     * Tests deactivate ISO data for HTTP 401.
     */
    @Test
    public void deactivateIso401() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        DeactivateIsoRequest mockedReq = new DeactivateIsoRequest(new ArrayList<>());
        RequestEntity<DeactivateIsoRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, DEACTIVATE_ISO_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(isoDataService.deactivateIsoData(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockedResp));

        ResponseEntity<?> resp = isoDataService.deactivateIsoData(req);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    /**
     * Tests deactivate ISO data for HTTP 403.
     */
    @Test
    public void deactivateIso403() {
        HttpHeaders mockedHeaders = new HttpHeaders();
        DeactivateIsoRequest mockedReq = new DeactivateIsoRequest(new ArrayList<>());
        RequestEntity<DeactivateIsoRequest> req =
                new RequestEntity<>(mockedReq, mockedHeaders, HttpMethod.POST, DEACTIVATE_ISO_URI);
        ErrorResponse mockedResp = new ErrorResponse("error");
        when(isoDataService.deactivateIsoData(req))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.FORBIDDEN).body(mockedResp));

        ResponseEntity<?> resp = isoDataService.deactivateIsoData(req);
        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    }

}
