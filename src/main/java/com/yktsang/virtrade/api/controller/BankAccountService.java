/*
 * BankAccountService.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.api.controller;

import com.yktsang.virtrade.request.AddBankAccountRequest;
import com.yktsang.virtrade.response.BankAccountResponse;
import com.yktsang.virtrade.response.ErrorResponse;
import com.yktsang.virtrade.response.SuccessResponse;
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

import java.math.BigInteger;

/**
 * The service for <code>BankAccount</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Tag(name = "Bank Account Service", description = "The Bank Account API")
@Service
public interface BankAccountService {

    /**
     * Returns the bank accounts.
     * Takes in the <code>Void</code> as input.
     * Returns the <code>BankAccountResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req      the request entity containing the Void
     * @param page     the page number to retrieve
     * @param pageSize the number of records to retrieve
     * @return the response entity containing the BankAccountResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Bank accounts",
            description = "Return the bank accounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the bank accounts",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BankAccountResponse.class))}
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
    @GetMapping(value = "/api/v1/member/banks")
    ResponseEntity<?> bankAccounts(RequestEntity<Void> req,
                                   @RequestParam("page") int page, @RequestParam("pageSize") int pageSize);

    /**
     * Returns the bank accounts for specific currency.
     * Takes in the <code>Void</code> as input.
     * Returns the <code>BankAccountResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req      the request entity containing the Void
     * @param currency the currency code
     * @param page     the page number to retrieve
     * @param pageSize the number of records to retrieve
     * @return the response entity containing the BankAccountResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Bank accounts for specific currency",
            description = "Return the bank accounts for specific currency")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the bank accounts for specific currency",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BankAccountResponse.class))}
            ),
            @ApiResponse(responseCode = "204",
                    description = "No data is returned"
            ),
            @ApiResponse(responseCode = "400,401,404",
                    description = "Any failed responses",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}
            )
    })
    @GetMapping(value = "/api/v1/member/banks/{ccy}")
    ResponseEntity<?> bankAccounts(RequestEntity<Void> req, @PathVariable("ccy") String currency,
                                   @RequestParam("page") int page, @RequestParam("pageSize") int pageSize);

    /**
     * Adds the bank account.
     * Takes in the <code>AddBankAccountRequest</code> as input.
     * Returns the <code>SuccessResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req the request entity containing the AddBankAccountRequest
     * @return the response entity containing the SuccessResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Add the bank account",
            description = "Add the bank account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Bank account is created",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SuccessResponse.class))}
            ),
            @ApiResponse(responseCode = "400,401,404",
                    description = "Any failed responses",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}
            )
    })
    @PostMapping(value = "/api/v1/member/banks/add"
            , consumes = {MediaType.APPLICATION_JSON_VALUE}
            , produces = {MediaType.APPLICATION_JSON_VALUE})
    @Transactional
    ResponseEntity<?> addBankAccount(@RequestBody RequestEntity<AddBankAccountRequest> req);

    /**
     * Obsoletes the specific bank account.
     * Takes in the <code>Void</code> as input.
     * Returns the <code>SuccessResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req           the request entity containing the Void
     * @param bankAccountId the bank account ID
     * @return the response entity containing the SuccessResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Mark not in use for specific bank account",
            description = "Mark not in use for specific bank account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Mark not in use for specific bank account",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SuccessResponse.class))}
            ),
            @ApiResponse(responseCode = "304",
                    description = "Nothing was done, no changes"
            ),
            @ApiResponse(responseCode = "400,401,404,406",
                    description = "Any failed responses",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}
            )
    })
    @GetMapping(value = "/api/v1/member/banks/obsolete/{id}")
    @Transactional
    ResponseEntity<?> obsoleteBankAccount(RequestEntity<Void> req, @PathVariable("id") BigInteger bankAccountId);

}
