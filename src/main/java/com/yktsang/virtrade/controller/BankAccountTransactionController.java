/*
 * BankAccountTransactionController.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.controller;

import com.yktsang.virtrade.api.controller.BankAccountTransactionService;
import com.yktsang.virtrade.api.controller.IsoDataService;
import com.yktsang.virtrade.api.controller.RefreshTokenService;
import com.yktsang.virtrade.entity.IsoCurrency;
import com.yktsang.virtrade.request.RefreshTokenRequest;
import com.yktsang.virtrade.response.BankAccountTransaction;
import com.yktsang.virtrade.response.BankAccountTransactionResponse;
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
 * The controller for <code>BankAccountTransaction</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Controller
public class BankAccountTransactionController {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(BankAccountTransactionController.class);
    /**
     * The bank account transaction service.
     */
    @Autowired
    private BankAccountTransactionService bankAcctTxnService;
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
     * Shows the bank transaction page.
     * If failed, go to the member login page.
     *
     * @param session  the HTTP session
     * @param currency the Optional containing the currency
     * @param page     the Optional containing the page
     * @param pageSize the Optional containing the page size
     * @return the bank transaction page, the member login page otherwise
     */
    @GetMapping("/member/bankTxn")
    public ModelAndView showBankTransaction(HttpSession session,
                                            @RequestParam("ccy") Optional<String> currency,
                                            @RequestParam("page") Optional<Integer> page,
                                            @RequestParam("pageSize") Optional<Integer> pageSize) {
        if (Objects.nonNull(session.getAttribute("email"))) {

            int defaultStartPage = page.orElse(1);
            int defaultPageSize = pageSize.orElse(5);

            if (currency.isPresent() && currency.get().isEmpty()) {
                return new ModelAndView("redirect:/member/bankTxn");
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
            URI apiEndpoint = currency.map(s -> URI.create("/api/v1/member/banks/transactions/" + s))
                    .orElseGet(() -> URI.create("/api/v1/member/banks/transactions"));

            RequestEntity<Void> bankAcctTxnReq =
                    new RequestEntity<>(headerMap, HttpMethod.GET, apiEndpoint);
            ResponseEntity<?> bankAcctTxnResp =
                    currency.<ResponseEntity<?>>map(s -> bankAcctTxnService.bankAccountTransactions(bankAcctTxnReq, s, defaultStartPage, defaultPageSize))
                            .orElseGet(() -> bankAcctTxnService.bankAccountTransactions(bankAcctTxnReq, defaultStartPage, defaultPageSize));
            logger.info("bankAccountTransactions={}", bankAcctTxnResp.getStatusCode());

            RequestEntity<Void> isoReq =
                    new RequestEntity<>(headerMap, HttpMethod.GET, URI.create("/api/v1/member/iso/currencies"));
            ResponseEntity<?> isoResp = isoDataService.activeCurrencies(isoReq);
            logger.info("activeCurrencies={}", isoResp.getStatusCode());

            // not authorized (HTTP 401)
            if (bankAcctTxnResp.getStatusCode() == HttpStatus.UNAUTHORIZED
                    || isoResp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new ModelAndView("redirect:/login?from=member");
            }

            // validation errors (HTTP 400) or currency not found (HTTP 404)
            if (bankAcctTxnResp.getStatusCode() == HttpStatus.BAD_REQUEST
                    || bankAcctTxnResp.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new ModelAndView("redirect:/member/bankTxn");
            }

            ModelAndView mv = new ModelAndView("member/bankTxn");
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

            // success (HTTP 200) or no data (HTTP 204) for bank account transaction
            if (bankAcctTxnResp.getStatusCode() == HttpStatus.NO_CONTENT) {
                mv.addObject("bankAccountTransactions", new ArrayList<BankAccountTransaction>());
                mv.addObject("hasRecords", false);
            } else {
                Map<String, String> respHeaderMap = bankAcctTxnResp.getHeaders().toSingleValueMap();
                String first = respHeaderMap.get("first");
                String prev = respHeaderMap.get("prev");
                String next = respHeaderMap.get("next");
                String last = respHeaderMap.get("last");
                int totalPageCount = Objects.isNull(respHeaderMap.get("totalPageCount")) ? 0 : Integer.parseInt(respHeaderMap.get("totalPageCount"));
                boolean hasPrev = !Objects.isNull(respHeaderMap.get("hasPrev")) && Boolean.parseBoolean(respHeaderMap.get("hasPrev"));
                boolean hasNext = !Objects.isNull(respHeaderMap.get("hasNext")) && Boolean.parseBoolean(respHeaderMap.get("hasNext"));
                BankAccountTransactionResponse actualResp = (BankAccountTransactionResponse) bankAcctTxnResp.getBody();
                mv.addObject("bankAccountTransactions",
                        Objects.requireNonNull(actualResp).bankAccountTransactions());
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
