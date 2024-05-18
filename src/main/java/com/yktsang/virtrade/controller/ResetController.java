/*
 * ResetController.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.controller;

import com.yktsang.virtrade.api.controller.IsoDataService;
import com.yktsang.virtrade.api.controller.RefreshTokenService;
import com.yktsang.virtrade.api.controller.ResetService;
import com.yktsang.virtrade.entity.IsoCurrency;
import com.yktsang.virtrade.request.RefreshTokenRequest;
import com.yktsang.virtrade.request.ResetPortfolioRequest;
import com.yktsang.virtrade.response.ErrorResponse;
import com.yktsang.virtrade.response.IsoCurrencyResponse;
import com.yktsang.virtrade.response.JwtResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;
import java.util.HashSet;
import java.util.Objects;

/**
 * The controller for resetting portfolio.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Controller
public class ResetController {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(ResetController.class);
    /**
     * The reset service.
     */
    @Autowired
    private ResetService resetService;
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
     * Shows the reset portfolio page.
     * If failed, go to the member login page.
     *
     * @param session the HTTP session
     * @return the reset portfolio page, the member login page otherwise
     */
    @GetMapping("/member/reset")
    public ModelAndView showResetPortfolio(HttpSession session) {
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

            HttpHeaders headerMap = new HttpHeaders();
            headerMap.add("Authorization", "Bearer " + jwt);

            return this.loadResetPortfolioPage(email, headerMap, null);

        } else {
            return new ModelAndView("redirect:/login?from=member");
        }
    }

    /**
     * Returns the reset portfolio page.
     *
     * @param email        the email address
     * @param headerMap    the HTTP headers
     * @param errorMessage the error message
     * @return the reset portfolio page
     */
    private ModelAndView loadResetPortfolioPage(String email, HttpHeaders headerMap, String errorMessage) {
        RequestEntity<Void> isoReq =
                new RequestEntity<>(headerMap, HttpMethod.GET, URI.create("/api/v1/member/iso/currencies"));
        ResponseEntity<?> isoResp = isoDataService.activeCurrencies(isoReq);
        logger.info("activeCurrencies={}", isoResp.getStatusCode());

        // not authorized (HTTP 401)
        if (isoResp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            return new ModelAndView("redirect:/login?from=member");
        }

        ModelAndView mv = new ModelAndView("member/reset");
        mv.addObject("email", email);

        // success (HTTP 200) or no data (HTTP 204) for ISO currency
        if (isoResp.getStatusCode() == HttpStatus.NO_CONTENT) {
            mv.addObject("activeCurrencies", new HashSet<IsoCurrency>());
        } else {
            IsoCurrencyResponse actualResp = (IsoCurrencyResponse) isoResp.getBody();
            mv.addObject("activeCurrencies", Objects.requireNonNull(actualResp).activeCurrencies());
        }

        if (Objects.nonNull(errorMessage)) {
            mv.addObject("errorMessage", errorMessage);
        }

        return mv;
    }

    /**
     * Executes the reset.
     * If failed, go to the member login page.
     *
     * @param session the HTTP session
     * @param request the HTTP request
     * @return the dashboard page, the member login page otherwise
     */
    @PostMapping("/member/reset")
    public ModelAndView executeResetPortfolio(HttpSession session, HttpServletRequest request) {
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

            String currencyToReset = request.getParameter("resetCurrency");
            boolean resetAll = Objects.nonNull(request.getParameter("resetCurrency"))
                    && request.getParameter("resetCurrency").equalsIgnoreCase("#");

            HttpHeaders headerMap = new HttpHeaders();
            headerMap.add("Authorization", "Bearer " + jwt);
            ResetPortfolioRequest actualReq = new ResetPortfolioRequest(resetAll, currencyToReset);
            URI apiEndpoint = URI.create("/api/v1/member/portfolios/reset");

            RequestEntity<ResetPortfolioRequest> req =
                    new RequestEntity<>(actualReq, headerMap, HttpMethod.POST, apiEndpoint);
            ResponseEntity<?> resp = resetService.resetPortfolio(req);
            logger.info("resetPortfolio={}", resp.getStatusCode());

            // not authorized (HTTP 401)
            if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new ModelAndView("redirect:/login?from=member");
            }

            // validation errors (HTTP 400) or currency not found (HTTP 404)
            if (resp.getStatusCode() == HttpStatus.BAD_REQUEST
                    || resp.getStatusCode() == HttpStatus.NOT_FOUND) {
                ErrorResponse errResp = (ErrorResponse) resp.getBody();
                return this.loadResetPortfolioPage(email, headerMap, Objects.requireNonNull(errResp).errorMessage());
            }

            // success (HTTP 200)
            return new ModelAndView("redirect:/member/dashboard");

        } else {
            return new ModelAndView("redirect:/login?from=member");
        }
    }

}
