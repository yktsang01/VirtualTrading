/*
 * ProfileService.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.api.controller;

import com.yktsang.virtrade.request.DeactivateAccountRequest;
import com.yktsang.virtrade.request.ProfileRequest;
import com.yktsang.virtrade.request.RegistrationRequest;
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
import org.springframework.web.bind.annotation.PostMapping;

/**
 * The service for <code>Trader</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Tag(name = "Profile Service", description = "The Profile API")
@Service
public interface ProfileService {

    /**
     * Registers users.
     * Takes in the <code>RegistrationRequest</code> as input.
     * Returns the <code>JwtResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req the request entity containing the RegistrationRequest
     * @return the response entity containing the JwtResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "User registration",
            description = "Register user and return the JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "User is created and the JWT is returned",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JwtResponse.class))}
            ),
            @ApiResponse(responseCode = "400",
                    description = "Any failed responses",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}
            )
    })
    @PostMapping(value = "/api/v1/registration"
            , consumes = {MediaType.APPLICATION_JSON_VALUE}
            , produces = {MediaType.APPLICATION_JSON_VALUE})
    @Transactional
    ResponseEntity<?> register(@RequestBody RequestEntity<RegistrationRequest> req);

    /**
     * Returns the user profile.
     * Takes in the <code>Void</code> as input.
     * Returns the <code>CompleteProfileResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req the request entity containing the Void
     * @return the response entity containing the CompleteProfileResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "User profile",
            description = "Return the user profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the user profile",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CompleteProfileResponse.class))}
            ),
            @ApiResponse(responseCode = "401,404",
                    description = "Any failed responses",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}
            )
    })
    @GetMapping(value = "/api/v1/member/profile/private")
    ResponseEntity<?> privateProfile(RequestEntity<Void> req);

    /**
     * Returns the user public profile.
     * Takes in the <code>Void</code> as input.
     * Returns the <code>ProfileResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req the request entity containing the Void
     * @return the response entity containing the ProfileResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "User public profile",
            description = "Return the user public profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the user public profile",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProfileResponse.class))}
            ),
            @ApiResponse(responseCode = "401,404",
                    description = "Any failed responses",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}
            )
    })
    @GetMapping(value = "/api/v1/member/profile/public")
    ResponseEntity<?> publicProfile(RequestEntity<Void> req);

    /**
     * Updates user profile.
     * Takes in the <code>ProfileRequest</code> as input.
     * Returns the <code>ProfileResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req the request entity containing the ProfileRequest
     * @return the response entity containing the ProfileResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "User profile update",
            description = "Update user profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "User profile is updated",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProfileResponse.class))}
            ),
            @ApiResponse(responseCode = "400,401,404",
                    description = "Any failed responses",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}
            )
    })
    @PostMapping(value = "/api/v1/member/profile/update"
            , consumes = {MediaType.APPLICATION_JSON_VALUE}
            , produces = {MediaType.APPLICATION_JSON_VALUE})
    @Transactional
    ResponseEntity<?> updateProfile(@RequestBody RequestEntity<ProfileRequest> req);

    /**
     * Deactivates user profile.
     * Takes in the <code>DeactivateAccountRequest</code> as input.
     * Returns the <code>SuccessResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req the request entity containing the DeactivateAccountRequest
     * @return the response entity containing the SuccessResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "User profile deactivation",
            description = "Deactivate user profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "User profile is deactivated",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SuccessResponse.class))}
            ),
            @ApiResponse(responseCode = "304",
                    description = "Nothing was done, no changes"
            ),
            @ApiResponse(responseCode = "400,401,404",
                    description = "Any failed responses",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}
            )
    })
    @PostMapping(value = "/api/v1/member/profile/deactivate"
            , consumes = {MediaType.APPLICATION_JSON_VALUE}
            , produces = {MediaType.APPLICATION_JSON_VALUE})
    @Transactional
    ResponseEntity<?> deactivateProfile(@RequestBody RequestEntity<DeactivateAccountRequest> req);

}
