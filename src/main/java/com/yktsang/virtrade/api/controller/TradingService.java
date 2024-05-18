/*
 * TradingService.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.api.controller;

import com.yktsang.virtrade.entity.OutstandingTradingTransaction;
import com.yktsang.virtrade.entity.TradingDeed;
import com.yktsang.virtrade.entity.TradingTransaction;
import com.yktsang.virtrade.request.BuyRequest;
import com.yktsang.virtrade.request.SearchRequest;
import com.yktsang.virtrade.request.SellRequest;
import com.yktsang.virtrade.response.*;
import com.yktsang.virtrade.yahoofinance.YahooStock;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * The service for trading purposes.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Tag(name = "Trading Service", description = "The Trading API")
@Service
public interface TradingService {

    /**
     * Returns the available stocks.
     *
     * @return the available stocks
     */
    List<YahooStock> getStocks();

    /**
     * Returns the outstanding quantity for given trading symbol.
     *
     * @param email  the email address
     * @param symbol the trading symbol
     * @return the outstanding quantity
     */
    int calculateOutstandingQuantity(String email, String symbol);

    /**
     * Returns the outstanding trading transactions from the provided trading transactions.
     *
     * @param email the email address
     * @param txns  the provided trading transactions
     * @return the outstanding trading transactions
     */
    Set<OutstandingTradingTransaction> getOutstandingTradingTransactions(String email, List<TradingTransaction> txns);

    /**
     * Returns true if given trading symbol had been bought, false otherwise.
     *
     * @param email  the email address
     * @param symbol the trading symbol
     * @return true if given trading symbol had been bought, false otherwise
     */
    boolean holdExisting(String email, String symbol);

    /**
     * Returns the estimated cost.
     *
     * @param tradingDeed      the trading deed
     * @param transactionPrice the transaction price
     * @param quantity         the quantity
     * @return the estimated cost
     */
    BigDecimal calculateEstimatedCost(TradingDeed tradingDeed, BigDecimal transactionPrice, Integer quantity);

    /**
     * Returns the world indices.
     * Takes in the <code>Void</code> as input.
     * Returns the <code>YahooStockResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req the request entity containing the Void
     * @param page     the page number to retrieve
     * @param pageSize the number of records to retrieve
     * @return the response entity containing the YahooStockResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "World indices",
            description = "Return the world indices")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the world indices",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = YahooStockResponse.class))}
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
    @GetMapping(value = "/api/v1/member/trading/indices")
    ResponseEntity<?> indices(RequestEntity<Void> req,
                              @RequestParam("page") int page, @RequestParam("pageSize") int pageSize);

    /**
     * Returns the world indices for specific currency.
     * Takes in the <code>Void</code> as input.
     * Returns the <code>YahooStockResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req      the request entity containing the Void
     * @param currency the currency code
     * @param page     the page number to retrieve
     * @param pageSize the number of records to retrieve
     * @return the response entity containing the YahooStockResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "World indices for specific currency",
            description = "Return the world indices for specific currency")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the world indices for specific currency",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = YahooStockResponse.class))}
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
    @GetMapping(value = "/api/v1/member/trading/indices/{ccy}")
    ResponseEntity<?> indices(RequestEntity<Void> req, @PathVariable("ccy") String currency,
                              @RequestParam("page") int page, @RequestParam("pageSize") int pageSize);

    /**
     * Returns the world equities.
     * Takes in the <code>Void</code> as input.
     * Returns the <code>YahooStockResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req the request entity containing the Void
     * @param page     the page number to retrieve
     * @param pageSize the number of records to retrieve
     * @return the response entity containing the YahooStockResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "World equities",
            description = "Return the world equities")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the world equities",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = YahooStockResponse.class))}
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
    @GetMapping(value = "/api/v1/member/trading/equities")
    ResponseEntity<?> equities(RequestEntity<Void> req,
                               @RequestParam("page") int page, @RequestParam("pageSize") int pageSize);

    /**
     * Returns the world equities for specific currency.
     * Takes in the <code>Void</code> as input.
     * Returns the <code>YahooStockResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req the request entity containing the Void
     * @param currency the currency code
     * @param page     the page number to retrieve
     * @param pageSize the number of records to retrieve
     * @return the response entity containing the YahooStockResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "World equities for specific currency",
            description = "Return the world equities for specific currency")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the world equities for specific currency",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = YahooStockResponse.class))}
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
    @GetMapping(value = "/api/v1/member/trading/equities/{ccy}")
    ResponseEntity<?> equities(RequestEntity<Void> req, @PathVariable("ccy") String currency,
                               @RequestParam("page") int page, @RequestParam("pageSize") int pageSize);

    /**
     * Searches stocks by trading symbol or name.
     * Takes in the <code>SearchRequest</code> as input.
     * Returns the <code>SearchResultResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req      the request entity containing the SearchRequest
     * @param page     the page number to retrieve
     * @param pageSize the number of records to retrieve
     * @return the response entity containing the SearchResultResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Search stocks",
            description = "Return the search results")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the search results",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SearchResultResponse.class))}
            ),
            @ApiResponse(responseCode = "204",
                    description = "No data is returned"
            ),
            @ApiResponse(responseCode = "400,401",
                    description = "Any failed responses",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}
            )
    })
    @PostMapping(value = "/api/v1/member/trading/search"
            , consumes = {MediaType.APPLICATION_JSON_VALUE}
            , produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<?> search(@RequestBody RequestEntity<SearchRequest> req,
                             @RequestParam("page") int page, @RequestParam("pageSize") int pageSize);

    /**
     * Buys equity stock.
     * Takes in the <code>BuyRequest</code> as input.
     * Returns the <code>SuccessResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req the request entity containing the BuyRequest
     * @return the response entity containing the SuccessResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Buy equity stock",
            description = "Buy equity stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
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
    @PostMapping(value = "/api/v1/member/trading/buy"
            , consumes = {MediaType.APPLICATION_JSON_VALUE}
            , produces = {MediaType.APPLICATION_JSON_VALUE})
    @Transactional
    ResponseEntity<?> buy(@RequestBody RequestEntity<BuyRequest> req);

    /**
     * Sells equity stock.
     * Takes in the <code>SellRequest</code> as input.
     * Returns the <code>SuccessResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req the request entity containing the SellRequest
     * @return the response entity containing the SuccessResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Sell equity stock",
            description = "Sell equity stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
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
    @PostMapping(value = "/api/v1/member/trading/sell"
            , consumes = {MediaType.APPLICATION_JSON_VALUE}
            , produces = {MediaType.APPLICATION_JSON_VALUE})
    @Transactional
    ResponseEntity<?> sell(@RequestBody RequestEntity<SellRequest> req);

    /**
     * Returns the outstanding transactions.
     * Takes in the <code>Void</code> as input.
     * Returns the <code>OutstandingTransactionResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req      the request entity containing the Void
     * @param page     the page number to retrieve
     * @param pageSize the number of records to retrieve
     * @return the response entity containing the OutstandingTransactionResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Outstanding transactions",
            description = "Return the outstanding transactions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the outstanding transactions",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OutstandingTransactionResponse.class))}
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
    @GetMapping(value = "/api/v1/member/trading/transactions/outstanding")
    ResponseEntity<?> outstandingTransactions(RequestEntity<Void> req,
                                              @RequestParam("page") int page, @RequestParam("pageSize") int pageSize);

    /**
     * Returns the outstanding transactions for specific currency.
     * Takes in the <code>Void</code> as input.
     * Returns the <code>OutstandingTransactionResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req      the request entity containing the Void
     * @param currency the currency code
     * @param page     the page number to retrieve
     * @param pageSize the number of records to retrieve
     * @return the response entity containing the OutstandingTransactionResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Outstanding transactions for specific currency",
            description = "Return the outstanding transactions for specific currency")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the outstanding transactions for specific currency",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OutstandingTransactionResponse.class))}
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
    @GetMapping(value = "/api/v1/member/trading/transactions/outstanding/{ccy}")
    ResponseEntity<?> outstandingTransactions(RequestEntity<Void> req, @PathVariable("ccy") String currency,
                                              @RequestParam("page") int page, @RequestParam("pageSize") int pageSize);

}
