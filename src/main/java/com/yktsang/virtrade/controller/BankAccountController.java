/*
 * BankAccountController.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.controller;

import com.yktsang.virtrade.api.controller.BankAccountService;
import com.yktsang.virtrade.api.controller.IsoDataService;
import com.yktsang.virtrade.api.controller.RefreshTokenService;
import com.yktsang.virtrade.entity.IsoCurrency;
import com.yktsang.virtrade.request.AddBankAccountRequest;
import com.yktsang.virtrade.request.RefreshTokenRequest;
import com.yktsang.virtrade.response.BankAccountResponse;
import com.yktsang.virtrade.response.ErrorResponse;
import com.yktsang.virtrade.response.IsoCurrencyResponse;
import com.yktsang.virtrade.response.JwtResponse;
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

import java.math.BigInteger;
import java.net.URI;
import java.util.*;

/**
 * The controller for <code>BankAccount</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Controller
public class BankAccountController {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(BankAccountController.class);
    /**
     * The bank account service.
     */
    @Autowired
    private BankAccountService bankAcctService;
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
     * Shows the bank info page.
     * If failed, go to the member login page.
     *
     * @param session  the HTTP session
     * @param currency the Optional containing the currency
     * @param page     the Optional containing the page
     * @param pageSize the Optional containing the page size
     * @return the bank info page, the member login page otherwise
     */
    @GetMapping("/member/bankInfo")
    public ModelAndView showBankInfo(HttpSession session,
                                     @RequestParam("ccy") Optional<String> currency,
                                     @RequestParam("page") Optional<Integer> page,
                                     @RequestParam("pageSize") Optional<Integer> pageSize) {
        if (Objects.nonNull(session.getAttribute("email"))) {

            int defaultStartPage = page.orElse(1);
            int defaultPageSize = pageSize.orElse(5);

            if (currency.isPresent() && currency.get().isEmpty()) {
                return new ModelAndView("redirect:/member/bankInfo");
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
            return this.loadBankInfoPage(email, currency.orElse(""), defaultStartPage,
                    defaultPageSize, headerMap, null);

        } else {
            return new ModelAndView("redirect:/login?from=member");
        }
    }

    /**
     * Returns the bank info page.
     *
     * @param email        the email address
     * @param selectedCcy  the selected currency
     * @param page         the page number to retrieve
     * @param pageSize     the number of records to retrieve
     * @param headerMap    the HTTP headers
     * @param errorMessage the error message
     * @return the bank info page
     */
    private ModelAndView loadBankInfoPage(String email, String selectedCcy, int page, int pageSize,
                                          HttpHeaders headerMap, String errorMessage) {
        URI bankAcctApiEndpoint = selectedCcy.isEmpty()
                ? URI.create("/api/v1/member/banks")
                : URI.create("/api/v1/member/banks/" + selectedCcy);
        RequestEntity<Void> bankAcctReq = new RequestEntity<>(headerMap, HttpMethod.GET, bankAcctApiEndpoint);
        ResponseEntity<?> bankAcctResp = selectedCcy.isEmpty()
                ? bankAcctService.bankAccounts(bankAcctReq, page, pageSize)
                : bankAcctService.bankAccounts(bankAcctReq, selectedCcy, page, pageSize);
        logger.info("bankAccounts={}", bankAcctResp.getStatusCode());

        RequestEntity<Void> isoReq =
                new RequestEntity<>(headerMap, HttpMethod.GET, URI.create("/api/v1/member/iso/currencies"));
        ResponseEntity<?> isoResp = isoDataService.activeCurrencies(isoReq);
        logger.info("activeCurrencies={}", isoResp.getStatusCode());

        // not authorized (HTTP 401)
        if (bankAcctResp.getStatusCode() == HttpStatus.UNAUTHORIZED
                || isoResp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            return new ModelAndView("redirect:/login?from=member");
        }

        // validation errors (HTTP 400) or currency not found (HTTP 404)
        if (bankAcctResp.getStatusCode() == HttpStatus.BAD_REQUEST
                || bankAcctResp.getStatusCode() == HttpStatus.NOT_FOUND) {
            return new ModelAndView("redirect:/member/bankInfo");
        }

        ModelAndView mv = new ModelAndView("member/bankInfo");
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

        // success (HTTP 200) or no data (HTTP 204) for bank account
        if (bankAcctResp.getStatusCode() == HttpStatus.NO_CONTENT) {
            mv.addObject("bankAccounts", new ArrayList<com.yktsang.virtrade.response.BankAccount>());
            mv.addObject("hasRecords", false);
        } else {
            Map<String, String> respHeaderMap = bankAcctResp.getHeaders().toSingleValueMap();
            String first = respHeaderMap.get("first");
            String prev = respHeaderMap.get("prev");
            String next = respHeaderMap.get("next");
            String last = respHeaderMap.get("last");
            int totalPageCount = Objects.isNull(respHeaderMap.get("totalPageCount")) ? 0 : Integer.parseInt(respHeaderMap.get("totalPageCount"));
            boolean hasPrev = !Objects.isNull(respHeaderMap.get("hasPrev")) && Boolean.parseBoolean(respHeaderMap.get("hasPrev"));
            boolean hasNext = !Objects.isNull(respHeaderMap.get("hasNext")) && Boolean.parseBoolean(respHeaderMap.get("hasNext"));
            BankAccountResponse actualResp = (BankAccountResponse) bankAcctResp.getBody();
            mv.addObject("bankAccounts", Objects.requireNonNull(actualResp).bankAccounts());
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
     * Adds the bank account.
     * If there are any validation errors, it will return back to the bank info page.
     * If successful, it will go to the bank info page, the member login page otherwise.
     *
     * @param session the HTTP session
     * @param request the HTTP request
     * @return the bank info page, the member login page otherwise
     */
    @PostMapping("/member/addBank")
    public ModelAndView addBankAccount(HttpSession session, HttpServletRequest request) {
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
            String bankName = request.getParameter("bankName");
            String bankAccountNumber = request.getParameter("bankAccountNumber");
            int page = Integer.parseInt(request.getParameter("pageNum"));
            int pageSize = Integer.parseInt(request.getParameter("pageSize"));

            HttpHeaders headerMap = new HttpHeaders();
            headerMap.add("Authorization", "Bearer " + jwt);
            AddBankAccountRequest actualReq = new AddBankAccountRequest(currency, bankName, bankAccountNumber);
            URI apiEndpoint = URI.create("/api/v1/member/banks/add");

            RequestEntity<AddBankAccountRequest> req =
                    new RequestEntity<>(actualReq, headerMap, HttpMethod.POST, apiEndpoint);
            ResponseEntity<?> resp = bankAcctService.addBankAccount(req);
            logger.info("addBankAccount={}", resp.getStatusCode());

            // not authorized (HTTP 401)
            if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new ModelAndView("redirect:/login?from=member");
            }

            // validation errors (HTTP 400) or currency not found (HTTP 404)
            if (resp.getStatusCode() == HttpStatus.BAD_REQUEST
                    || resp.getStatusCode() == HttpStatus.NOT_FOUND) {
                ErrorResponse errResp = (ErrorResponse) resp.getBody();
                String errorMessage = Objects.requireNonNull(errResp).errorMessage();
                return this.loadBankInfoPage(email, selectedCcy, page, pageSize, headerMap, errorMessage);
            }

            // success (HTTP 201)
            return new ModelAndView("redirect:/member/bankInfo");
        } else {
            return new ModelAndView("redirect:/login?from=member");
        }
    }

    /**
     * Marks the bank account as obsolete.
     *
     * @param session       the HTTP session
     * @param bankAccountId the Optional containing the bank account ID
     * @return the bank info page, the member login page otherwise
     */
    @GetMapping("/member/obsoleteBank")
    public ModelAndView obsoleteBankAccount(HttpSession session, @RequestParam("id") Optional<String> bankAccountId) {
        if (Objects.nonNull(session.getAttribute("email"))) {

            if (bankAccountId.isEmpty() || bankAccountId.get().isEmpty()) {
                return new ModelAndView("redirect:/member/bankInfo");
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

            BigInteger bankAcctId = NumberUtils.isDigits(bankAccountId.get()) ? new BigInteger(bankAccountId.get()) : null;

            HttpHeaders headerMap = new HttpHeaders();
            headerMap.add("Authorization", "Bearer " + jwt);
            URI apiEndpoint = URI.create("/api/v1/member/banks/obsolete/" + bankAcctId);

            RequestEntity<Void> req = new RequestEntity<>(headerMap, HttpMethod.GET, apiEndpoint);
            ResponseEntity<?> resp = bankAcctService.obsoleteBankAccount(req, bankAcctId);
            logger.info("obsoleteBankAccount={}", resp.getStatusCode());

            // not authorized (HTTP 401)
            if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new ModelAndView("redirect:/login?from=member");
            }

            // validation errors (HTTP 400) or bank account not found (HTTP 404)
            // or bank account not belong to caller (HTTP 406)
            // or success (HTTP 200) or no op (HTTP 302)
            return new ModelAndView("redirect:/member/bankInfo");
        } else {
            return new ModelAndView("redirect:/login?from=member");
        }
    }

}
