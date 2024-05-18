/*
 * IsoDataService.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.api.controller;

import com.yktsang.virtrade.request.ActivateIsoRequest;
import com.yktsang.virtrade.request.CreateIsoRequest;
import com.yktsang.virtrade.request.DeactivateIsoRequest;
import com.yktsang.virtrade.request.UpdateIsoRequest;
import com.yktsang.virtrade.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The service for <code>IsoData</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Tag(name = "ISO Data Service", description = "The ISO Data API")
@Service
public interface IsoDataService {

    /**
     * Returns the active currencies.
     * Takes in the <code>Void</code> as input.
     * Returns the <code>IsoCurrencyResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req the request entity containing the Void
     * @return the response entity containing the IsoCurrencyResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Active currencies",
            description = "Return the active currencies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the admin access requests",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = IsoCurrencyResponse.class))}
            ),
            @ApiResponse(responseCode = "204",
                    description = "No data is returned"
            ),
            @ApiResponse(responseCode = "401",
                    description = "Any failed responses",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}
            )
    })
    @GetMapping(value = "/api/v1/member/iso/currencies")
    ResponseEntity<?> activeCurrencies(RequestEntity<Void> req);

    /**
     * Returns the ISO data.
     * Takes in the <code>Void</code> as input.
     * Returns the <code>IsoDataResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req      the request entity containing the Void
     * @param page     the page number to retrieve
     * @param pageSize the number of records to retrieve
     * @return the response entity containing the IsoDataResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "ISO data",
            description = "Return the ISO data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the admin access requests",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = IsoDataResponse.class))}
            ),
            @ApiResponse(responseCode = "204",
                    description = "No data is returned"
            ),
            @ApiResponse(responseCode = "401,403",
                    description = "Any failed responses",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}
            )
    })
    @GetMapping(value = "/api/v1/admin/iso")
    ResponseEntity<?> isoData(RequestEntity<Void> req,
                              @RequestParam("page") int page, @RequestParam("pageSize") int pageSize);

    /**
     * Returns the specific ISO data.
     * Takes in the <code>Void</code> as input.
     * Returns the <code>IsoCodeResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req         the request entity containing the Void
     * @param countryCode the country code
     * @return the response entity containing the IsoCodeResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "ISO data for specific country",
            description = "Return the specific ISO data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the admin access requests",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = IsoCodeResponse.class))}
            ),
            @ApiResponse(responseCode = "400,401,403,404",
                    description = "Any failed responses",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}
            )
    })
    @GetMapping(value = "/api/v1/admin/iso/{code}")
    ResponseEntity<?> isoData(RequestEntity<Void> req, @PathVariable("code") String countryCode);

    /**
     * Creates the ISO data.
     * Takes in the <code>CreateIsoRequest</code> as input.
     * Returns the <code>SuccessResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req the request entity containing the CreateIsoRequest
     * @return the response entity containing the SuccessResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Create ISO data",
            description = "Create ISO data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "ISO data is created",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SuccessResponse.class))}
            ),
            @ApiResponse(responseCode = "400,401,403",
                    description = "Any failed responses",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}
            )
    })
    @PostMapping(value = "/api/v1/admin/iso/create"
            , consumes = {MediaType.APPLICATION_JSON_VALUE}
            , produces = {MediaType.APPLICATION_JSON_VALUE})
    @Transactional
    ResponseEntity<?> createIsoData(@RequestBody RequestEntity<CreateIsoRequest> req);

    /**
     * Updates the ISO data.
     * Takes in the <code>UpdateIsoRequest</code> as input.
     * Returns the <code>SuccessResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req the request entity containing the UpdateIsoRequest
     * @return the response entity containing the SuccessResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Update ISO data",
            description = "Update ISO data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "ISO data is updated",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SuccessResponse.class))}
            ),
            @ApiResponse(responseCode = "400,401,403,404",
                    description = "Any failed responses",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}
            )
    })
    @PostMapping(value = "/api/v1/admin/iso/update"
            , consumes = {MediaType.APPLICATION_JSON_VALUE}
            , produces = {MediaType.APPLICATION_JSON_VALUE})
    @Transactional
    ResponseEntity<?> updateIsoData(@RequestBody RequestEntity<UpdateIsoRequest> req);

    /**
     * Activates the ISO data.
     * Takes in the <code>ActivateIsoRequest</code> as input.
     * Returns the <code>SuccessResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req the request entity containing the ActivateIsoRequest
     * @return the response entity containing the SuccessResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Activate ISO data",
            description = "Activate ISO data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "ISO data is activated",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SuccessResponse.class))}
            ),
            @ApiResponse(responseCode = "304",
                    description = "Nothing was done, no changes"
            ),
            @ApiResponse(responseCode = "400,401,403",
                    description = "Any failed responses",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}
            )
    })
    @PostMapping(value = "/api/v1/admin/iso/activate"
            , consumes = {MediaType.APPLICATION_JSON_VALUE}
            , produces = {MediaType.APPLICATION_JSON_VALUE})
    @Transactional
    ResponseEntity<?> activateIsoData(@RequestBody RequestEntity<ActivateIsoRequest> req);

    /**
     * Deactivates the ISO data.
     * Takes in the <code>DeactivateIsoRequest</code> as input.
     * Returns the <code>SuccessResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req the request entity containing the DeactivateIsoRequest
     * @return the response entity containing the SuccessResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Deactivate ISO data",
            description = "Deactivate ISO data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "ISO data is deactivated",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SuccessResponse.class))}
            ),
            @ApiResponse(responseCode = "304",
                    description = "Nothing was done, no changes"
            ),
            @ApiResponse(responseCode = "400,401,403",
                    description = "Any failed responses",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}
            )
    })
    @PostMapping(value = "/api/v1/admin/iso/deactivate"
            , consumes = {MediaType.APPLICATION_JSON_VALUE}
            , produces = {MediaType.APPLICATION_JSON_VALUE})
    @Transactional
    ResponseEntity<?> deactivateIsoData(@RequestBody RequestEntity<DeactivateIsoRequest> req);

}
