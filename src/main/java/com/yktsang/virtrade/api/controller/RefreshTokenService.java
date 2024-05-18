/*
 * RefreshTokenService.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.api.controller;

import com.yktsang.virtrade.request.RefreshTokenRequest;
import com.yktsang.virtrade.response.ErrorResponse;
import com.yktsang.virtrade.response.JwtResponse;
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
import org.springframework.web.bind.annotation.PostMapping;

/**
 * The service for refreshing JSON web token (JWT).
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Tag(name = "Refresh Token Service", description = "The Refresh Token API")
@Service
public interface RefreshTokenService {

    /**
     * Refreshed the JSON web token (JWT).
     * Takes in the <code>RefreshTokenRequest</code> as input.
     * Returns the <code>JwtResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req the request entity containing the RefreshTokenRequest
     * @return the response entity containing the JwtResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Refresh token",
            description = "Refresh the JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "New JWT is returned",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JwtResponse.class))}
            ),
            @ApiResponse(responseCode = "304",
                    description = "Nothing was done, no changes"
            ),
            @ApiResponse(responseCode = "400",
                    description = "Any failed responses",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}
            )
    })
    @PostMapping(value = "/api/v1/refreshToken"
            , consumes = {MediaType.APPLICATION_JSON_VALUE}
            , produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<?> refreshToken(@RequestBody RequestEntity<RefreshTokenRequest> req);

}
