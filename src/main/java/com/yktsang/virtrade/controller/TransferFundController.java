/*
 * TransferFundController.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.controller;

import com.yktsang.virtrade.api.controller.AccountBalanceService;
import com.yktsang.virtrade.api.controller.BankAccountService;
import com.yktsang.virtrade.api.controller.RefreshTokenService;
import com.yktsang.virtrade.api.controller.TransferService;
import com.yktsang.virtrade.request.RefreshTokenRequest;
import com.yktsang.virtrade.request.TransferFundRequest;
import com.yktsang.virtrade.response.AccountBalanceResponse;
import com.yktsang.virtrade.response.BankAccountResponse;
import com.yktsang.virtrade.response.ErrorResponse;
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
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

/**
 * The controller for transferring funds.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Controller
public class TransferFundController {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(TransferFundController.class);
    /**
     * The account balance service.
     */
    @Autowired
    private AccountBalanceService acctBalService;
    /**
     * The bank account service.
     */
    @Autowired
    private BankAccountService bankAcctService;
    /**
     * The transfer service.
     */
    @Autowired
    private TransferService transferService;
    /**
     * The refresh token service.
     */
    @Autowired
    private RefreshTokenService refreshTokenService;

    /**
     * Shows the transfer fund page.
     * If failed, go to the member login page.
     *
     * @param session the HTTP session
     * @param ccy     the Optional containing the currency
     * @return the transfer fund page, the member login page otherwise
     */
    @GetMapping("/member/transferFunds")
    public ModelAndView showTransferFunds(HttpSession session, Optional<String> ccy) {
        if (Objects.nonNull(session.getAttribute("email"))) {

            if (ccy.isPresent() && ccy.get().isEmpty()) {
                return new ModelAndView("redirect:/member/transferFunds");
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
            return this.loadTransferFundPage(email, ccy.orElse(""), headerMap, null);

        } else {
            return new ModelAndView("redirect:/login?from=member");
        }
    }

    /**
     * Shows the transfer fund page.
     *
     * @param email        the email address
     * @param selectedCcy  the selected currency
     * @param headerMap    the HTTP headers
     * @param errorMessage the error message
     * @return the transfer fund page
     */
    private ModelAndView loadTransferFundPage(String email, String selectedCcy,
                                              HttpHeaders headerMap, String errorMessage) {
        URI acctBalApiEndpoint = selectedCcy.isEmpty()
                ? URI.create("/api/v1/member/balances")
                : URI.create("/api/v1/member/balances/" + selectedCcy);
        RequestEntity<Void> acctBalReq =
                new RequestEntity<>(headerMap, HttpMethod.GET, acctBalApiEndpoint);
        ResponseEntity<?> acctBalResp = selectedCcy.isEmpty()
                ? acctBalService.accountBalances(acctBalReq, 0, 0)
                : acctBalService.accountBalances(acctBalReq, selectedCcy);
        logger.info("accountBalances={}", acctBalResp.getStatusCode());

        URI bankAcctApiEndpoint = selectedCcy.isEmpty()
                ? URI.create("/api/v1/member/banks")
                : URI.create("/api/v1/member/banks/" + selectedCcy);
        RequestEntity<Void> bankAcctReq =
                new RequestEntity<>(headerMap, HttpMethod.GET, bankAcctApiEndpoint);
        ResponseEntity<?> bankAcctResp = selectedCcy.isEmpty()
                ? bankAcctService.bankAccounts(bankAcctReq, 0, 0)
                : bankAcctService.bankAccounts(bankAcctReq, selectedCcy, 0, 0);
        logger.info("bankAccounts={}", bankAcctResp.getStatusCode());

        // not authorized (HTTP 401)
        if (acctBalResp.getStatusCode() == HttpStatus.UNAUTHORIZED
                || bankAcctResp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            return new ModelAndView("redirect:/login?from=member");
        }

        // validation errors (HTTP 400) or currency not found (HTTP 404)
        if (acctBalResp.getStatusCode() == HttpStatus.BAD_REQUEST
                || acctBalResp.getStatusCode() == HttpStatus.NOT_FOUND
                || bankAcctResp.getStatusCode() == HttpStatus.BAD_REQUEST
                || bankAcctResp.getStatusCode() == HttpStatus.NOT_FOUND) {
            return new ModelAndView("redirect:/member/transferFunds");
        }

        ModelAndView mv = new ModelAndView("member/transfer");
        mv.addObject("email", email);
        mv.addObject("selectedCcy", selectedCcy);

        // success (HTTP 200) or no data (HTTP 204) for account balance
        if (acctBalResp.getStatusCode() == HttpStatus.NO_CONTENT) {
            mv.addObject("balances", new ArrayList<com.yktsang.virtrade.response.AccountBalance>());
        } else {
            AccountBalanceResponse actualResp = (AccountBalanceResponse) acctBalResp.getBody();
            mv.addObject("balances", Objects.requireNonNull(actualResp).balances());
        }

        // success (HTTP 200) or no data (HTTP 204) for bank account
        if (bankAcctResp.getStatusCode() == HttpStatus.NO_CONTENT) {
            mv.addObject("bankAccounts", new ArrayList<com.yktsang.virtrade.response.BankAccount>());
        } else {
            BankAccountResponse actualResp = (BankAccountResponse) bankAcctResp.getBody();
            mv.addObject("bankAccounts", Objects.requireNonNull(actualResp).bankAccounts());
        }

        if (Objects.nonNull(errorMessage)) {
            mv.addObject("errorMessage", errorMessage);
        }

        return mv;
    }

    /**
     * Transfer funds to bank account.
     * If there are any validation errors, it will go back to the transfer fund page.
     * If successful, it will go to the account balance page, the member login page otherwise.
     *
     * @param session the HTTP session
     * @param request the HTTP request
     * @return the account balance page if successful, the transfer fund page if validation error,
     * the member login page otherwise
     */
    @PostMapping("/member/transferFunds")
    public ModelAndView transfer(HttpSession session, HttpServletRequest request) {
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
            String accountCcy = request.getParameter("accountCcy");
            String bankAcctIdStr = request.getParameter("bankAcctId");
            String amountStr = request.getParameter("amount");

            HttpHeaders headerMap = new HttpHeaders();
            headerMap.add("Authorization", "Bearer " + jwt);

            boolean validNumFormat = (Objects.nonNull(amountStr) && NumberUtils.isParsable(amountStr))
                    && (Objects.nonNull(bankAcctIdStr) && NumberUtils.isDigits(bankAcctIdStr));
            logger.info("transfer validNumFormat={}", validNumFormat);
            if (validNumFormat) {
                TransferFundRequest actualReq = new TransferFundRequest(accountCcy,
                        new BigInteger(bankAcctIdStr), new BigDecimal(amountStr));
                URI apiEndpoint = URI.create("/api/v1/member/transfer");

                RequestEntity<TransferFundRequest> req =
                        new RequestEntity<>(actualReq, headerMap, HttpMethod.POST, apiEndpoint);
                ResponseEntity<?> resp = transferService.transferFunds(req);
                logger.info("transferFunds={}", resp.getStatusCode());

                // not authorized (HTTP 401)
                if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    return new ModelAndView("redirect:/login?from=member");
                }

                // validation errors (HTTP 400)
                // or account balance/ bank account not found (HTTP 404)
                // or other errors (HTTP 406)
                if (resp.getStatusCode() == HttpStatus.BAD_REQUEST
                        || resp.getStatusCode() == HttpStatus.NOT_FOUND
                        || resp.getStatusCode() == HttpStatus.NOT_ACCEPTABLE) {
                    ErrorResponse errResp = (ErrorResponse) resp.getBody();
                    String errorMessage = Objects.requireNonNull(errResp).errorMessage();
                    return this.loadTransferFundPage(email, selectedCcy, headerMap, errorMessage);
                }

                // success (HTTP 200)
                if (selectedCcy.isEmpty()) {
                    return new ModelAndView("redirect:/member/accountBalance");
                } else {
                    return new ModelAndView("redirect:/member/accountBalance?ccy=" + selectedCcy);
                }

            } else {
                return this.loadTransferFundPage(email, selectedCcy,
                        headerMap, "Validation failed");
            }

        } else {
            return new ModelAndView("redirect:/login?from=member");
        }
    }

}
