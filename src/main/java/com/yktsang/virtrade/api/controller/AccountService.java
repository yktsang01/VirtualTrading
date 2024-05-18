/*
 * AccountService.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.api.controller;

import com.yktsang.virtrade.request.*;
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
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The service for <code>Account</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Tag(name = "Account Service", description = "The Account API")
@Service
public interface AccountService {

    /**
     * Returns true if admin account's last updated datetime is not null, false otherwise.
     *
     * @return true if admin account's last updated datetime is not null, false otherwise
     */
    boolean checkAdminLastUpdated();

    /**
     * Returns true if the provided username has admin rights, false otherwise.
     *
     * @param username the username
     * @return true if the provided username has admin rights, false otherwise
     */
    boolean hasAdminRight(String username);

    /**
     * Performs login.
     * Takes in the <code>AuthRequest</code> as input.
     * Returns the <code>JwtResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req the request entity containing the AuthRequest
     * @return the response entity containing the JwtResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Login",
            description = "Log in user and return the JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "JWT is returned",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JwtResponse.class))}
            ),
            @ApiResponse(responseCode = "400,403,404",
                    description = "Any failed responses",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}
            )
    })
    @PostMapping(value = "/api/v1/login"
            , consumes = {MediaType.APPLICATION_JSON_VALUE}
            , produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<?> login(@RequestBody RequestEntity<AuthRequest> req);

    /**
     * Resets password.
     * Takes in the <code>ResetPasswordRequest</code> as input.
     * Returns the <code>SuccessResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req the request entity containing the ResetPasswordRequest
     * @return the response entity containing the SuccessResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Password reset",
            description = "Reset password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successful operation",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SuccessResponse.class))}
            ),
            @ApiResponse(responseCode = "400,404",
                    description = "Any failed responses",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}
            )
    })
    @PostMapping(value = "/api/v1/password/reset"
            , consumes = {MediaType.APPLICATION_JSON_VALUE}
            , produces = {MediaType.APPLICATION_JSON_VALUE})
    @Transactional
    ResponseEntity<?> resetPassword(@RequestBody RequestEntity<ResetPasswordRequest> req);

    /**
     * Updates account.
     * Same as reset password but for already logged-in users.
     * Takes in the <code>ResetPasswordRequest</code> as input.
     * Returns the <code>SuccessResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req the request entity containing the ResetPasswordRequest
     * @return the response entity containing the SuccessResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Password reset for logged-in users",
            description = "Reset password for logged-in users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successful operation",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SuccessResponse.class))}
            ),
            @ApiResponse(responseCode = "400,401,404",
                    description = "Any failed responses",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}
            )
    })
    @PostMapping(value = "/api/v1/member/account/update"
            , consumes = {MediaType.APPLICATION_JSON_VALUE}
            , produces = {MediaType.APPLICATION_JSON_VALUE})
    @Transactional
    ResponseEntity<?> updateAccount(@RequestBody RequestEntity<ResetPasswordRequest> req);

    /**
     * Requests admin access.
     * Takes in the <code>RequestAdminAccessRequest</code> as input.
     * Returns the <code>SuccessResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req the request entity containing the RequestAdminAccessRequest
     * @return the response entity containing the SuccessResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Request for admin access",
            description = "Allow users to become admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successful operation",
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
    @PostMapping(value = "/api/v1/member/admin/request"
            , consumes = {MediaType.APPLICATION_JSON_VALUE}
            , produces = {MediaType.APPLICATION_JSON_VALUE})
    @Transactional
    ResponseEntity<?> requestAdminAccess(@RequestBody RequestEntity<RequestAdminAccessRequest> req);

    /**
     * Returns the admin access requests.
     * Takes in the <code>Void</code> as input.
     * Returns the <code>AdminRequestResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req      the request entity containing the Void
     * @param page     the page number to retrieve
     * @param pageSize the number of records to retrieve
     * @return the response entity containing the AdminRequestResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Admin access requests",
            description = "Return the admin access requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the admin access requests",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AdminRequestResponse.class))}
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
    @GetMapping(value = "/api/v1/admin/requests")
    ResponseEntity<?> adminRequests(RequestEntity<Void> req,
                                    @RequestParam("page") int page, @RequestParam("pageSize") int pageSize);

    /**
     * Returns the admin accesses.
     * Takes in the <code>Void</code> as input.
     * Returns the <code>AdminAccessResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req      the request entity containing the Void
     * @param page     the page number to retrieve
     * @param pageSize the number of records to retrieve
     * @return the response entity containing the AdminAccessResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Admin accesses",
            description = "Return the admin accesses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the admin accesses",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AdminAccessResponse.class))}
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
    @GetMapping(value = "/api/v1/admin/accesses")
    ResponseEntity<?> adminAccesses(RequestEntity<Void> req,
                                    @RequestParam("page") int page, @RequestParam("pageSize") int pageSize);

    /**
     * Grants admin accesses.
     * Takes in the <code>GrantAdminAccessRequest</code> as input.
     * Returns the <code>SuccessResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req the request entity containing the GrantAdminAccessRequest
     * @return the response entity containing the SuccessResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Grant admin accesses",
            description = "Grant the admin accesses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successful operation",
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
    @PostMapping(value = "/api/v1/admin/accesses/grant"
            , consumes = {MediaType.APPLICATION_JSON_VALUE}
            , produces = {MediaType.APPLICATION_JSON_VALUE})
    @Transactional
    ResponseEntity<?> grantAdminAccesses(@RequestBody RequestEntity<GrantAdminAccessRequest> req);

    /**
     * Revokes admin accesses.
     * Takes in the <code>RevokeAdminAccessRequest</code> as input.
     * Returns the <code>SuccessResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req the request entity containing the RevokeAdminAccessRequest
     * @return the response entity containing the SuccessResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Revoke admin accesses",
            description = "Revoke the admin accesses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successful operation",
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
    @PostMapping(value = "/api/v1/admin/accesses/revoke"
            , consumes = {MediaType.APPLICATION_JSON_VALUE}
            , produces = {MediaType.APPLICATION_JSON_VALUE})
    @Transactional
    ResponseEntity<?> revokeAdminAccesses(@RequestBody RequestEntity<RevokeAdminAccessRequest> req);

}
