/*
 * TradingTransactionController.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.controller;

import com.yktsang.virtrade.api.controller.IsoDataService;
import com.yktsang.virtrade.api.controller.PortfolioService;
import com.yktsang.virtrade.api.controller.RefreshTokenService;
import com.yktsang.virtrade.api.controller.TradingTransactionService;
import com.yktsang.virtrade.entity.IsoCurrency;
import com.yktsang.virtrade.request.RefreshTokenRequest;
import com.yktsang.virtrade.response.*;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;
import java.util.*;

/**
 * The controller for <code>TradingTransaction</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Controller
public class TradingTransactionController {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(TradingTransactionController.class);
    /**
     * The trading transaction service.
     */
    @Autowired
    private TradingTransactionService tradingTxnService;
    /**
     * The portfolio service.
     */
    @Autowired
    private PortfolioService portfolioService;
    /**
     * The ISO data service.
     */
    @Autowired
    private IsoDataService isoDataService;
    /**
     * The refresh token service.
     */
    @Autowired
    private RefreshTokenService refreshTokenService;

    /**
     * Shows the trading transaction page.
     * If failed, go to the member login page.
     *
     * @param session  the HTTP session
     * @param currency the Optional containing the currency
     * @param page     the Optional containing the page
     * @param pageSize the Optional containing the page size
     * @return the trading transaction page, the member login page otherwise
     */
    @GetMapping("/member/tradingTxn")
    public ModelAndView showTradingTransaction(HttpSession session,
                                               @RequestParam("ccy") Optional<String> currency,
                                               @RequestParam("page") Optional<Integer> page,
                                               @RequestParam("pageSize") Optional<Integer> pageSize) {
        if (Objects.nonNull(session.getAttribute("email"))) {

            int defaultStartPage = page.orElse(1);
            int defaultPageSize = pageSize.orElse(5);

            if (currency.isPresent() && currency.get().isEmpty()) {
                return new ModelAndView("redirect:/member/tradingTxn");
            }

            String email = (String) session.getAttribute("email");
            String jwt = (String) session.getAttribute("jwt");

            RefreshTokenRequest refreshActualReq = new RefreshTokenRequest(email, jwt);
            RequestEntity<RefreshTokenRequest> refreshReq =
                    new RequestEntity<>(refreshActualReq, HttpMethod.POST, URI.create("/api/v1/refreshToken"));
            ResponseEntity<?> refreshResp = refreshTokenService.refreshToken(refreshReq);
            if (refreshResp.getStatusCode() == HttpStatus.OK) {
                JwtResponse actualResp = (JwtResponse) refreshResp.getBody();
                String newToken = Objects.requireNonNull(actualResp).token();
                session.setAttribute("jwt", newToken);
                jwt = newToken;
            }

            HttpHeaders headerMap = new HttpHeaders();
            headerMap.add("Authorization", "Bearer " + jwt);

            RequestEntity<Void> isoReq =
                    new RequestEntity<>(headerMap, HttpMethod.GET, URI.create("/api/v1/member/iso/currencies"));
            ResponseEntity<?> isoResp = isoDataService.activeCurrencies(isoReq);
            logger.info("activeCurrencies={}", isoResp.getStatusCode());

            URI txnApiEndpoint = currency.map(s -> URI.create("/api/v1/member/trading/transactions/" + s))
                    .orElseGet(() -> URI.create("/api/v1/member/trading/transactions"));
            RequestEntity<Void> txnReq =
                    new RequestEntity<>(headerMap, HttpMethod.GET, txnApiEndpoint);
            ResponseEntity<?> txnResp =
                    currency.<ResponseEntity<?>>map(s -> tradingTxnService.tradingTransactions(txnReq, s, defaultStartPage, defaultPageSize))
                            .orElseGet(() -> tradingTxnService.tradingTransactions(txnReq, defaultStartPage, defaultPageSize));
            logger.info("tradingTransactions={}", txnResp.getStatusCode());

            URI portApiEndpoint = currency.map(s -> URI.create("/api/v1/member/portfolios/" + s))
                    .orElseGet(() -> URI.create("/api/v1/member/portfolios"));
            RequestEntity<Void> portReq =
                    new RequestEntity<>(headerMap, HttpMethod.GET, portApiEndpoint);
            ResponseEntity<?> portResp =
                    currency.<ResponseEntity<?>>map(s -> portfolioService.portfolios(portReq, s, 0, 0))
                            .orElseGet(() -> portfolioService.portfolios(portReq, 0, 0));
            logger.info("portfolios={}", portResp.getStatusCode());

            // not authorized (HTTP 401)
            if (txnResp.getStatusCode() == HttpStatus.UNAUTHORIZED
                    || portResp.getStatusCode() == HttpStatus.UNAUTHORIZED
                    || isoResp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new ModelAndView("redirect:/login?from=member");
            }

            // validation errors (HTTP 400) or currency not found (HTTP 404)
            if (txnResp.getStatusCode() == HttpStatus.BAD_REQUEST
                    || txnResp.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new ModelAndView("redirect:/member/tradingTxn");
            }

            ModelAndView mv = new ModelAndView("member/tradingTxn");
            mv.addObject("email", email);
            mv.addObject("selectedCcy", currency.orElse(""));
            mv.addObject("pageNum", defaultStartPage);
            mv.addObject("pageSize", defaultPageSize);

            // success (HTTP 200) or no data (HTTP 204) for ISO currency
            if (isoResp.getStatusCode() == HttpStatus.NO_CONTENT) {
                mv.addObject("activeCurrencies", new HashSet<IsoCurrency>());
            } else {
                IsoCurrencyResponse actualResp = (IsoCurrencyResponse) isoResp.getBody();
                mv.addObject("activeCurrencies", Objects.requireNonNull(actualResp).activeCurrencies());
            }

            // success (HTTP 200) or no data (HTTP 204) for portfolio
            if (portResp.getStatusCode() == HttpStatus.NO_CONTENT) {
                mv.addObject("portfolios", new ArrayList<Portfolio>());
            } else {
                PortfolioResponse actualResp = (PortfolioResponse) portResp.getBody();
                mv.addObject("portfolios", Objects.requireNonNull(actualResp).portfolios());
            }

            // success (HTTP 200) or no data (HTTP 204) for trading transaction
            if (txnResp.getStatusCode() == HttpStatus.NO_CONTENT) {
                mv.addObject("tradingTransactions", new ArrayList<TradingTransaction>());
                mv.addObject("hasRecords", false);
            } else {
                Map<String, String> respHeaderMap = txnResp.getHeaders().toSingleValueMap();
                String first = respHeaderMap.get("first");
                String prev = respHeaderMap.get("prev");
                String next = respHeaderMap.get("next");
                String last = respHeaderMap.get("last");
                int totalPageCount = Objects.isNull(respHeaderMap.get("totalPageCount")) ? 0 : Integer.parseInt(respHeaderMap.get("totalPageCount"));
                boolean hasPrev = !Objects.isNull(respHeaderMap.get("hasPrev")) && Boolean.parseBoolean(respHeaderMap.get("hasPrev"));
                boolean hasNext = !Objects.isNull(respHeaderMap.get("hasNext")) && Boolean.parseBoolean(respHeaderMap.get("hasNext"));
                TradingTransactionResponse actualResp = (TradingTransactionResponse) txnResp.getBody();
                mv.addObject("tradingTransactions", Objects.requireNonNull(actualResp).tradingTransactions());
                mv.addObject("hasRecords", totalPageCount > 0);
                mv.addObject("first", first);
                mv.addObject("prev", prev);
                mv.addObject("next", next);
                mv.addObject("last", last);
                mv.addObject("hasPrev", hasPrev);
                mv.addObject("hasNext", hasNext);
            }

            return mv;
        } else {
            return new ModelAndView("redirect:/login?from=member");
        }
    }

}
