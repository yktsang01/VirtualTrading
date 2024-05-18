/*
 * AccountTransactionController.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.controller;

import com.yktsang.virtrade.api.controller.AccountTransactionService;
import com.yktsang.virtrade.api.controller.IsoDataService;
import com.yktsang.virtrade.api.controller.RefreshTokenService;
import com.yktsang.virtrade.entity.IsoCurrency;
import com.yktsang.virtrade.request.RefreshTokenRequest;
import com.yktsang.virtrade.response.AccountTransaction;
import com.yktsang.virtrade.response.AccountTransactionResponse;
import com.yktsang.virtrade.response.IsoCurrencyResponse;
import com.yktsang.virtrade.response.JwtResponse;
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
 * The controller for <code>AccountTransaction</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Controller
public class AccountTransactionController {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(AccountTransactionController.class);
    /**
     * The account transaction service.
     */
    @Autowired
    private AccountTransactionService acctTxnService;
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
     * Shows the account transaction page.
     * If failed, go to the member login page.
     *
     * @param session  the HTTP session
     * @param currency the Optional containing the currency
     * @param page     the Optional containing the page
     * @param pageSize the Optional containing the page size
     * @return the account transaction page, the member login page otherwise
     */
    @GetMapping("/member/accountTxn")
    public ModelAndView showAccountTransaction(HttpSession session,
                                               @RequestParam("ccy") Optional<String> currency,
                                               @RequestParam("page") Optional<Integer> page,
                                               @RequestParam("pageSize") Optional<Integer> pageSize) {
        if (Objects.nonNull(session.getAttribute("email"))) {

            int defaultStartPage = page.orElse(1);
            int defaultPageSize = pageSize.orElse(5);

            if (currency.isPresent() && currency.get().isEmpty()) {
                return new ModelAndView("redirect:/member/accountTxn");
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
            URI apiEndpoint = currency.map(s -> URI.create("/api/v1/member/account/transactions/" + s))
                    .orElseGet(() -> URI.create("/api/v1/member/account/transactions"));

            RequestEntity<Void> acctTxnReq =
                    new RequestEntity<>(headerMap, HttpMethod.GET, apiEndpoint);
            ResponseEntity<?> acctTxnResp =
                    currency.<ResponseEntity<?>>map(s -> acctTxnService.accountTransactions(acctTxnReq, s, defaultStartPage, defaultPageSize))
                            .orElseGet(() -> acctTxnService.accountTransactions(acctTxnReq, defaultStartPage, defaultPageSize));
            logger.info("accountTransactions={}", acctTxnResp.getStatusCode());

            RequestEntity<Void> isoReq =
                    new RequestEntity<>(headerMap, HttpMethod.GET, URI.create("/api/v1/member/iso/currencies"));
            ResponseEntity<?> isoResp = isoDataService.activeCurrencies(isoReq);
            logger.info("activeCurrencies={}", isoResp.getStatusCode());

            // not authorized (HTTP 401)
            if (acctTxnResp.getStatusCode() == HttpStatus.UNAUTHORIZED
                    || isoResp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new ModelAndView("redirect:/login?from=member");
            }

            // validation errors (HTTP 400) or currency not found (HTTP 404)
            if (acctTxnResp.getStatusCode() == HttpStatus.BAD_REQUEST
                    || acctTxnResp.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new ModelAndView("redirect:/member/accountTxn");
            }

            ModelAndView mv = new ModelAndView("member/accountTxn");
            mv.addObject("email", email);
            mv.addObject("selectedCcy", currency.orElse(""));

            // success (HTTP 200) or no data (HTTP 204) for ISO currency
            if (isoResp.getStatusCode() == HttpStatus.NO_CONTENT) {
                mv.addObject("activeCurrencies", new HashSet<IsoCurrency>());
            } else {
                IsoCurrencyResponse actualResp = (IsoCurrencyResponse) isoResp.getBody();
                mv.addObject("activeCurrencies",
                        Objects.requireNonNull(actualResp).activeCurrencies());
            }

            // success (HTTP 200) or no data (HTTP 204) for account transaction
            if (acctTxnResp.getStatusCode() == HttpStatus.NO_CONTENT) {
                mv.addObject("accountTransactions", new ArrayList<AccountTransaction>());
                mv.addObject("hasRecords", false);
            } else {
                Map<String, String> respHeaderMap = acctTxnResp.getHeaders().toSingleValueMap();
                String first = respHeaderMap.get("first");
                String prev = respHeaderMap.get("prev");
                String next = respHeaderMap.get("next");
                String last = respHeaderMap.get("last");
                int totalPageCount = Objects.isNull(respHeaderMap.get("totalPageCount")) ? 0 : Integer.parseInt(respHeaderMap.get("totalPageCount"));
                boolean hasPrev = !Objects.isNull(respHeaderMap.get("hasPrev")) && Boolean.parseBoolean(respHeaderMap.get("hasPrev"));
                boolean hasNext = !Objects.isNull(respHeaderMap.get("hasNext")) && Boolean.parseBoolean(respHeaderMap.get("hasNext"));
                AccountTransactionResponse actualResp = (AccountTransactionResponse) acctTxnResp.getBody();
                mv.addObject("accountTransactions", Objects.requireNonNull(actualResp).accountTransactions());
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
