/*
 * AccountBalanceService.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.api.controller;

import com.yktsang.virtrade.request.DepositFundRequest;
import com.yktsang.virtrade.response.AccountBalanceResponse;
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

/**
 * The service for <code>AccountBalance</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Tag(name = "Account Balance Service", description = "The Account Balance API")
@Service
public interface AccountBalanceService {

    /**
     * Returns the account balances.
     * Takes in the <code>Void</code> as input.
     * Returns the <code>AccountBalanceResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req      the request entity containing the Void
     * @param page     the page number to retrieve
     * @param pageSize the number of records to retrieve
     * @return the response entity containing the AccountBalanceResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Account balances",
            description = "Return the account balances")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the account balances",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AccountBalanceResponse.class))}
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
    @GetMapping(value = "/api/v1/member/balances")
    ResponseEntity<?> accountBalances(RequestEntity<Void> req,
                                      @RequestParam("page") int page, @RequestParam("pageSize") int pageSize);

    /**
     * Returns the account balance for specific currency.
     * Takes in the <code>Void</code> as input.
     * Returns the <code>AccountBalanceResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req      the request entity containing the Void
     * @param currency the currency code
     * @return the response entity containing the AccountBalanceResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Account balance for specific currency",
            description = "Return the account balance for specific currency")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the account balance for specific currency",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AccountBalanceResponse.class))}
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
    @GetMapping(value = "/api/v1/member/balances/{ccy}")
    ResponseEntity<?> accountBalances(RequestEntity<Void> req, @PathVariable("ccy") String currency);

    /**
     * Deposits funds.
     * Takes in the <code>DepositFundRequest</code> as input.
     * Returns the <code>SuccessResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req the request entity containing the DepositFundRequest
     * @return the response entity containing the SuccessResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Fund deposit",
            description = "Deposit funds")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200,201",
                    description = "Successful operation",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SuccessResponse.class))}
            ),
            @ApiResponse(responseCode = "400,401,404,406",
                    description = "Any failed responses",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}
            )
    })
    @PostMapping(value = "/api/v1/member/balances/deposit"
            , consumes = {MediaType.APPLICATION_JSON_VALUE}
            , produces = {MediaType.APPLICATION_JSON_VALUE})
    @Transactional
    ResponseEntity<?> depositFunds(@RequestBody RequestEntity<DepositFundRequest> req);

}
