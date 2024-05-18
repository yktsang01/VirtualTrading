/*
 * BankAccountTransactionService.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.api.controller;

import com.yktsang.virtrade.response.BankAccountTransactionResponse;
import com.yktsang.virtrade.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The service for <code>BankAccountTransaction</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Tag(name = "Bank Account Transaction Service", description = "The Bank Account Transaction API")
@Service
public interface BankAccountTransactionService {

    /**
     * Returns the bank account transactions.
     * Takes in the <code>Void</code> as input.
     * Returns the <code>BankAccountTransactionResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req      the request entity containing the Void
     * @param page     the page number to retrieve
     * @param pageSize the number of records to retrieve
     * @return the response entity containing the BankAccountTransactionResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Bank account transactions",
            description = "Return the bank account transactions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the bank account transactions",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BankAccountTransactionResponse.class))}
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
    @GetMapping(value = "/api/v1/member/banks/transactions")
    ResponseEntity<?> bankAccountTransactions(RequestEntity<Void> req,
                                              @RequestParam("page") int page, @RequestParam("pageSize") int pageSize);

    /**
     * Returns the bank account transactions for specific currency.
     * Takes in the <code>Void</code> as input.
     * Returns the <code>BankAccountTransactionResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req      the request entity containing the Void
     * @param currency the currency code
     * @param page     the page number to retrieve
     * @param pageSize the number of records to retrieve
     * @return the response entity containing the BankAccountTransactionResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Bank account transactions for specific currency",
            description = "Return the bank account transactions for specific currency")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the bank account transactions for specific currency",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BankAccountTransactionResponse.class))}
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
    @GetMapping(value = "/api/v1/member/banks/transactions/{ccy}")
    ResponseEntity<?> bankAccountTransactions(RequestEntity<Void> req, @PathVariable("ccy") String currency,
                                              @RequestParam("page") int page, @RequestParam("pageSize") int pageSize);

}
