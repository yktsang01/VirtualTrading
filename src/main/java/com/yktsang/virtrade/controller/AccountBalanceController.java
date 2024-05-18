/*
 * AccountBalanceController.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.controller;

import com.yktsang.virtrade.api.controller.AccountBalanceService;
import com.yktsang.virtrade.api.controller.IsoDataService;
import com.yktsang.virtrade.api.controller.RefreshTokenService;
import com.yktsang.virtrade.entity.IsoCurrency;
import com.yktsang.virtrade.request.DepositFundRequest;
import com.yktsang.virtrade.request.RefreshTokenRequest;
import com.yktsang.virtrade.response.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.net.URI;
import java.util.*;

/**
 * The controller for <code>AccountBalance</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Controller
public class AccountBalanceController {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(AccountBalanceController.class);
    /**
     * The account balance service.
     */
    @Autowired
    private AccountBalanceService acctBalService;
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
     * Shows the account balance page.
     * If failed, go to the member login page.
     *
     * @param session  the HTTP session
     * @param currency the Optional containing the currency
     * @param page     the Optional containing the page
     * @param pageSize the Optional containing the page size
     * @return the account balance page, the member login page otherwise
     */
    @GetMapping("/member/accountBalance")
    public ModelAndView showAccountBalance(HttpSession session,
                                           @RequestParam("ccy") Optional<String> currency,
                                           @RequestParam("page") Optional<Integer> page,
                                           @RequestParam("pageSize") Optional<Integer> pageSize) {
        if (Objects.nonNull(session.getAttribute("email"))) {

            int defaultStartPage = page.orElse(1);
            int defaultPageSize = pageSize.orElse(5);

            if (currency.isPresent() && currency.get().isEmpty()) {
                return new ModelAndView("redirect:/member/accountBalance");
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
            return this.loadAccountBalancePage(email, currency.orElse(""), defaultStartPage,
                    defaultPageSize, headerMap, null);
        } else {
            return new ModelAndView("redirect:/login?from=member");
        }
    }

    /**
     * Returns the account balance page.
     *
     * @param email        the email address
     * @param selectedCcy  the selected currency
     * @param headerMap    the HTTP headers
     * @param page         the page number to retrieve
     * @param pageSize     the number of records to retrieve
     * @param errorMessage the error message
     * @return the account balance page
     */
    private ModelAndView loadAccountBalancePage(String email, String selectedCcy, int page, int pageSize,
                                                HttpHeaders headerMap, String errorMessage) {
        URI apiEndpoint = selectedCcy.isEmpty()
                ? URI.create("/api/v1/member/balances")
                : URI.create("/api/v1/member/balances/" + selectedCcy);

        RequestEntity<Void> acctBalReq =
                new RequestEntity<>(headerMap, HttpMethod.GET, apiEndpoint);
        ResponseEntity<?> acctBalResp = selectedCcy.isEmpty()
                ? acctBalService.accountBalances(acctBalReq, page, pageSize)
                : acctBalService.accountBalances(acctBalReq, selectedCcy);
        logger.info("accountBalances={}", acctBalResp.getStatusCode());

        RequestEntity<Void> isoReq =
                new RequestEntity<>(headerMap, HttpMethod.GET, URI.create("/api/v1/member/iso/currencies"));
        ResponseEntity<?> isoResp = isoDataService.activeCurrencies(isoReq);
        logger.info("activeCurrencies={}", isoResp.getStatusCode());

        // not authorized (HTTP 401)
        if (acctBalResp.getStatusCode() == HttpStatus.UNAUTHORIZED
                || isoResp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            return new ModelAndView("redirect:/login?from=member");
        }

        // validation errors (HTTP 400) or currency not found (HTTP 404)
        if (acctBalResp.getStatusCode() == HttpStatus.BAD_REQUEST
                || acctBalResp.getStatusCode() == HttpStatus.NOT_FOUND) {
            return new ModelAndView("redirect:/member/accountBalance");
        }

        ModelAndView mv = new ModelAndView("member/accountBalance");
        mv.addObject("email", email);
        mv.addObject("selectedCcy", selectedCcy);
        mv.addObject("pageNum", page);
        mv.addObject("pageSize", pageSize);

        // success (HTTP 200) or no data (HTTP 204) for ISO currency
        if (isoResp.getStatusCode() == HttpStatus.NO_CONTENT) {
            mv.addObject("activeCurrencies", new HashSet<IsoCurrency>());
        } else {
            IsoCurrencyResponse actualResp = (IsoCurrencyResponse) isoResp.getBody();
            mv.addObject("activeCurrencies", Objects.requireNonNull(actualResp).activeCurrencies());
        }

        // success (HTTP 200) or no data (HTTP 204) for account balance
        if (acctBalResp.getStatusCode() == HttpStatus.NO_CONTENT) {
            mv.addObject("balances", new ArrayList<AccountBalance>());
            mv.addObject("hasRecords", false);
        } else {
            Map<String, String> respHeaderMap = acctBalResp.getHeaders().toSingleValueMap();
            String first = respHeaderMap.get("first");
            String prev = respHeaderMap.get("prev");
            String next = respHeaderMap.get("next");
            String last = respHeaderMap.get("last");
            int totalPageCount = Objects.isNull(respHeaderMap.get("totalPageCount")) ? 0 : Integer.parseInt(respHeaderMap.get("totalPageCount"));
            boolean hasPrev = !Objects.isNull(respHeaderMap.get("hasPrev")) && Boolean.parseBoolean(respHeaderMap.get("hasPrev"));
            boolean hasNext = !Objects.isNull(respHeaderMap.get("hasNext")) && Boolean.parseBoolean(respHeaderMap.get("hasNext"));
            AccountBalanceResponse actualResp = (AccountBalanceResponse) acctBalResp.getBody();
            mv.addObject("balances", Objects.requireNonNull(actualResp).balances());
            mv.addObject("hasRecords", totalPageCount > 0);
            mv.addObject("first", first);
            mv.addObject("prev", prev);
            mv.addObject("next", next);
            mv.addObject("last", last);
            mv.addObject("hasPrev", hasPrev);
            mv.addObject("hasNext", hasNext);
        }

        if (Objects.nonNull(errorMessage)) {
            mv.addObject("errorMessage", errorMessage);
        }

        return mv;
    }

    /**
     * Deposits virtual funds.
     * If there are any validation errors, it will go back to the account balance page.
     * If successful, it will go to the account balance page, the member login page otherwise.
     *
     * @param session the HTTP session
     * @param request the HTTP request
     * @return the account balance page if successful or validation errors,
     * the member login page otherwise
     */
    @PostMapping("/member/depositFunds")
    public ModelAndView deposit(HttpSession session, HttpServletRequest request) {
        if (Objects.nonNull(session.getAttribute("email"))) {
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

            String selectedCcy = request.getParameter("selectedCcy");
            String currency = request.getParameter("currency");
            String amountStr = request.getParameter("amount");
            int page = Integer.parseInt(request.getParameter("pageNum"));
            int pageSize = Integer.parseInt(request.getParameter("pageSize"));

            HttpHeaders headerMap = new HttpHeaders();
            headerMap.add("Authorization", "Bearer " + jwt);

            boolean validNumFormat = Objects.nonNull(amountStr) && NumberUtils.isParsable(amountStr);
            logger.info("deposit validNumFormat={}", validNumFormat);

            if (validNumFormat) {
                DepositFundRequest actualReq = new DepositFundRequest(currency, new BigDecimal(amountStr));
                URI apiEndpoint = URI.create("/api/v1/member/balances/deposit");

                RequestEntity<DepositFundRequest> req =
                        new RequestEntity<>(actualReq, headerMap, HttpMethod.POST, apiEndpoint);
                ResponseEntity<?> resp = acctBalService.depositFunds(req);
                logger.info("depositFunds={}", resp.getStatusCode());

                // not authorized (HTTP 401)
                if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    return new ModelAndView("redirect:/login?from=member");
                }

                // validation errors (HTTP 400) or currency not found (HTTP 404)
                // or new balance is greater than one trillion (HTTP 406)
                if (resp.getStatusCode() == HttpStatus.BAD_REQUEST
                        || resp.getStatusCode() == HttpStatus.NOT_FOUND
                        || resp.getStatusCode() == HttpStatus.NOT_ACCEPTABLE) {
                    ErrorResponse errResp = (ErrorResponse) resp.getBody();
                    String errorMessage = Objects.requireNonNull(errResp).errorMessage();
                    return this.loadAccountBalancePage(email, selectedCcy, page, pageSize, headerMap, errorMessage);
                }

                // success (HTTP 200 or HTTP 201)
                if (selectedCcy.isEmpty()) {
                    return new ModelAndView("redirect:/member/accountBalance");
                } else {
                    return new ModelAndView("redirect:/member/accountBalance?ccy=" + selectedCcy);
                }

            } else {
                return this.loadAccountBalancePage(email, selectedCcy, page, pageSize,
                        headerMap, "Validation failed");
            }

        } else {
            return new ModelAndView("redirect:/login?from=member");
        }
    }

}
