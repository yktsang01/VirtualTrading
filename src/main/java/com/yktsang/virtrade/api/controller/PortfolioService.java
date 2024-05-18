/*
 * PortfolioService.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.api.controller;

import com.yktsang.virtrade.request.CreatePortfolioRequest;
import com.yktsang.virtrade.request.LinkTransactionRequest;
import com.yktsang.virtrade.request.UnlinkTransactionRequest;
import com.yktsang.virtrade.response.ErrorResponse;
import com.yktsang.virtrade.response.PortfolioDetailResponse;
import com.yktsang.virtrade.response.PortfolioResponse;
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
 * The service for <code>Portfolio</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Tag(name = "Portfolio Service", description = "The Portfolio API")
@Service
public interface PortfolioService {

    /**
     * Returns the portfolios.
     * Takes in the <code>Void</code> as input.
     * Returns the <code>PortfolioResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req      the request entity containing the Void
     * @param page     the page number to retrieve
     * @param pageSize the number of records to retrieve
     * @return the response entity containing the PortfolioResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Portfolios",
            description = "Return the portfolios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the portfolios",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PortfolioResponse.class))}
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
    @GetMapping(value = "/api/v1/member/portfolios")
    ResponseEntity<?> portfolios(RequestEntity<Void> req,
                                 @RequestParam("page") int page, @RequestParam("pageSize") int pageSize);

    /**
     * Returns the portfolios.
     * Takes in the <code>Void</code> as input.
     * Returns the <code>PortfolioResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req      the request entity containing the Void
     * @param currency the currency code
     * @param page     the page number to retrieve
     * @param pageSize the number of records to retrieve
     * @return the response entity containing the PortfolioResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Portfolios for specific currency",
            description = "Return the portfolios for specific currency")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the portfolios for specific currency",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PortfolioResponse.class))}
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
    @GetMapping(value = "/api/v1/member/portfolios/{ccy}")
    ResponseEntity<?> portfolios(RequestEntity<Void> req, @PathVariable("ccy") String currency,
                                 @RequestParam("page") int page, @RequestParam("pageSize") int pageSize);


    /**
     * Creates the portfolios.
     * Takes in the <code>CreatePortfolioRequest</code> as input.
     * Returns the <code>SuccessResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req the request entity containing the CreatePortfolioRequest
     * @return the response entity containing the SuccessResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Portfolio creation",
            description = "Create the portfolio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Portfolio is created",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SuccessResponse.class))}
            ),
            @ApiResponse(responseCode = "400,401,404",
                    description = "Any failed responses",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}
            )
    })
    @PostMapping(value = "/api/v1/member/portfolios/create"
            , consumes = {MediaType.APPLICATION_JSON_VALUE}
            , produces = {MediaType.APPLICATION_JSON_VALUE})
    @Transactional
    ResponseEntity<?> createPortfolio(@RequestBody RequestEntity<CreatePortfolioRequest> req);

    /**
     * Returns the portfolio details.
     * Takes in the <code>Void</code> as input.
     * Returns the <code>PortfolioDetailResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req         the request entity containing the Void
     * @param portfolioId the portfolio ID
     * @param page        the page number to retrieve
     * @param pageSize    the number of records to retrieve
     * @return the response entity containing the PortfolioDetailResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Portfolio details",
            description = "Return the portfolio details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the portfolio details",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PortfolioDetailResponse.class))}
            ),
            @ApiResponse(responseCode = "400,401,404,406",
                    description = "Any failed responses",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}
            )
    })
    @GetMapping(value = "/api/v1/member/portfolios/details/{id}")
    ResponseEntity<?> portfolioDetails(RequestEntity<Void> req, @PathVariable("id") BigInteger portfolioId,
                                       @RequestParam("page") int page, @RequestParam("pageSize") int pageSize);

    /**
     * Links trading transactions to portfolio.
     * Takes in the <code>LinkTransactionRequest</code> as input.
     * Returns the <code>SuccessResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req the request entity containing the LinkTransactionRequest
     * @return the response entity containing the SuccessResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Link trading transactions to portfolio",
            description = "Link trading transactions to portfolio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successful operation",
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
    @PostMapping(value = "/api/v1/member/portfolios/link"
            , consumes = {MediaType.APPLICATION_JSON_VALUE}
            , produces = {MediaType.APPLICATION_JSON_VALUE})
    @Transactional
    ResponseEntity<?> linkToPortfolio(@RequestBody RequestEntity<LinkTransactionRequest> req);

    /**
     * Unlinks trading transactions from portfolio.
     * Takes in the <code>Void</code> as input.
     * Returns the <code>SuccessResponse</code> upon success
     * or the <code>ErrorResponse</code> upon failure.
     *
     * @param req the request entity containing the Void
     * @return the response entity containing the SuccessResponse upon success
     * or ErrorResponse upon failure
     */
    @Operation(
            summary = "Unlink trading transactions from portfolio",
            description = "Unlink trading transactions from portfolio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successful operation",
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
    @PostMapping(value = "/api/v1/member/portfolios/unlink"
            , consumes = {MediaType.APPLICATION_JSON_VALUE}
            , produces = {MediaType.APPLICATION_JSON_VALUE})
    @Transactional
    ResponseEntity<?> unlinkFromPortfolio(@RequestBody RequestEntity<UnlinkTransactionRequest> req);

}
