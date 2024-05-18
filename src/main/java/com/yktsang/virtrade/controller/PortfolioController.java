/*
 * PortfolioController.java
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
import com.yktsang.virtrade.request.CreatePortfolioRequest;
import com.yktsang.virtrade.request.LinkTransactionRequest;
import com.yktsang.virtrade.request.RefreshTokenRequest;
import com.yktsang.virtrade.request.UnlinkTransactionRequest;
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

import java.math.BigInteger;
import java.net.URI;
import java.util.*;

/**
 * The controller for <code>Portfolio</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Controller
public class PortfolioController {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(PortfolioController.class);
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
     * Shows the portfolio page.
     * If failed, go to the member login page.
     *
     * @param session  the HTTP session
     * @param currency the Optional containing the currency
     * @param page     the Optional containing the page
     * @param pageSize the Optional containing the page size
     * @return the portfolio page, the member login page otherwise
     */
    @GetMapping("/member/portfolio")
    public ModelAndView showPortfolio(HttpSession session,
                                      @RequestParam("ccy") Optional<String> currency,
                                      @RequestParam("page") Optional<Integer> page,
                                      @RequestParam("pageSize") Optional<Integer> pageSize) {
        if (Objects.nonNull(session.getAttribute("email"))) {

            int defaultStartPage = page.orElse(1);
            int defaultPageSize = pageSize.orElse(5);

            if (currency.isPresent() && currency.get().isEmpty()) {
                return new ModelAndView("redirect:/member/portfolio");
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
            return this.loadPortfolioPage(email, currency.orElse(""), defaultStartPage,
                    defaultPageSize, headerMap, null);

        } else {
            return new ModelAndView("redirect:/login?from=member");
        }
    }

    /**
     * Returns the portfolio page.
     *
     * @param email        the email address
     * @param selectedCcy  the selected currency
     * @param page         the page number to retrieve
     * @param pageSize     the number of records to retrieve
     * @param headerMap    the HTTP headers
     * @param errorMessage the error message
     * @return the portfolio page
     */
    private ModelAndView loadPortfolioPage(String email, String selectedCcy, int page, int pageSize,
                                           HttpHeaders headerMap, String errorMessage) {
        RequestEntity<Void> isoReq =
                new RequestEntity<>(headerMap, HttpMethod.GET, URI.create("/api/v1/member/iso/currencies"));
        ResponseEntity<?> isoResp = isoDataService.activeCurrencies(isoReq);
        logger.info("activeCurrencies={}", isoResp.getStatusCode());

        URI portApiEndpoint = selectedCcy.isEmpty()
                ? URI.create("/api/v1/member/portfolios")
                : URI.create("/api/v1/member/portfolios/" + selectedCcy);
        RequestEntity<Void> portReq =
                new RequestEntity<>(headerMap, HttpMethod.GET, portApiEndpoint);
        ResponseEntity<?> portResp = selectedCcy.isEmpty()
                ? portfolioService.portfolios(portReq, page, pageSize)
                : portfolioService.portfolios(portReq, selectedCcy, page, pageSize);
        logger.info("portfolios={}", portResp.getStatusCode());

        // not authorized (HTTP 401)
        if (portResp.getStatusCode() == HttpStatus.UNAUTHORIZED
                || isoResp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            return new ModelAndView("redirect:/login?from=member");
        }

        // validation errors (HTTP 400) or currency not found (HTTP 404)
        if (portResp.getStatusCode() == HttpStatus.BAD_REQUEST
                || portResp.getStatusCode() == HttpStatus.NOT_FOUND) {
            return new ModelAndView("redirect:/member/portfolio");
        }

        ModelAndView mv = new ModelAndView("member/portfolio");
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

        // success (HTTP 200) or no data (HTTP 204) for portfolio
        if (portResp.getStatusCode() == HttpStatus.NO_CONTENT) {
            mv.addObject("portfolios", new ArrayList<Portfolio>());
            mv.addObject("hasRecords", false);
        } else {
            Map<String, String> respHeaderMap = portResp.getHeaders().toSingleValueMap();
            String first = respHeaderMap.get("first");
            String prev = respHeaderMap.get("prev");
            String next = respHeaderMap.get("next");
            String last = respHeaderMap.get("last");
            int totalPageCount = Objects.isNull(respHeaderMap.get("totalPageCount")) ? 0 : Integer.parseInt(respHeaderMap.get("totalPageCount"));
            boolean hasPrev = !Objects.isNull(respHeaderMap.get("hasPrev")) && Boolean.parseBoolean(respHeaderMap.get("hasPrev"));
            boolean hasNext = !Objects.isNull(respHeaderMap.get("hasNext")) && Boolean.parseBoolean(respHeaderMap.get("hasNext"));
            PortfolioResponse actualResp = (PortfolioResponse) portResp.getBody();
            mv.addObject("portfolios", Objects.requireNonNull(actualResp).portfolios());
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
     * Creates the portfolio.
     * If failed, go to the member login page.
     *
     * @param session the HTTP session
     * @param request the HTTP request
     * @return the portfolio page, the member login page otherwise
     */
    @PostMapping("/member/createPortfolio")
    public ModelAndView createPortfolio(HttpSession session, HttpServletRequest request) {
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
            String portfolioName = request.getParameter("portName");
            String portfolioCurrency = request.getParameter("portCcy");
            int page = Integer.parseInt(request.getParameter("pageNum"));
            int pageSize = Integer.parseInt(request.getParameter("pageSize"));

            HttpHeaders headerMap = new HttpHeaders();
            headerMap.add("Authorization", "Bearer " + jwt);
            CreatePortfolioRequest actualReq = new CreatePortfolioRequest(portfolioName, portfolioCurrency);
            URI apiEndpoint = URI.create("/api/v1/member/portfolios/create");

            RequestEntity<CreatePortfolioRequest> req =
                    new RequestEntity<>(actualReq, headerMap, HttpMethod.GET, apiEndpoint);
            ResponseEntity<?> resp = portfolioService.createPortfolio(req);
            logger.info("createPortfolio={}", resp.getStatusCode());

            // not authorized (HTTP 401)
            if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new ModelAndView("redirect:/login?from=member");
            }

            // validation errors (HTTP 400) or currency not found (HTTP 404)
            if (resp.getStatusCode() == HttpStatus.BAD_REQUEST
                    || resp.getStatusCode() == HttpStatus.NOT_FOUND) {
                ErrorResponse errResp = (ErrorResponse) resp.getBody();
                return this.loadPortfolioPage(email, selectedCcy, page, pageSize, headerMap,
                        Objects.requireNonNull(errResp).errorMessage());
            }

            //success (HTTP 201)
            return new ModelAndView("redirect:/member/portfolio");

        } else {
            return new ModelAndView("redirect:/login?from=member");
        }
    }

    /**
     * Shows the portfolio details page.
     * If failed, go to the member login page.
     *
     * @param session        the HTTP session
     * @param portfolioIdOpt the Optional containing the portfolio ID
     * @param page           the Optional containing the page
     * @param pageSize       the Optional containing the page size
     * @return the portfolio details page, the member login page otherwise
     */
    @GetMapping("/member/portfolioDetails")
    public ModelAndView showPortfolioDetails(HttpSession session,
                                             @RequestParam("id") Optional<String> portfolioIdOpt,
                                             @RequestParam("page") Optional<Integer> page,
                                             @RequestParam("pageSize") Optional<Integer> pageSize) {
        if (Objects.nonNull(session.getAttribute("email"))) {

            int defaultStartPage = page.orElse(1);
            int defaultPageSize = pageSize.orElse(5);

            if (portfolioIdOpt.isEmpty() || portfolioIdOpt.get().isEmpty()) {
                return new ModelAndView("redirect:/member/portfolio");
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

            BigInteger portfolioId = NumberUtils.isDigits(portfolioIdOpt.get()) ? new BigInteger(portfolioIdOpt.get()) : null;

            HttpHeaders headerMap = new HttpHeaders();
            headerMap.add("Authorization", "Bearer " + jwt);
            return this.loadPortfolioDetailsPage(email, portfolioId, defaultStartPage, defaultPageSize, headerMap, null);

        } else {
            return new ModelAndView("redirect:/login?from=member");
        }
    }

    /**
     * Returns the portfolio details page.
     *
     * @param email        the email address
     * @param portfolioId  the portfolio ID
     * @param page         the page number to retrieve
     * @param pageSize     the number of records to retrieve
     * @param headerMap    the HTTP headers
     * @param errorMessage the error message
     * @return the portfolio details page
     */
    private ModelAndView loadPortfolioDetailsPage(String email, BigInteger portfolioId, int page, int pageSize,
                                                  HttpHeaders headerMap, String errorMessage) {
        URI apiEndpoint = URI.create("/api/v1/member/portfolios/details/" + portfolioId);

        RequestEntity<Void> req = new RequestEntity<>(headerMap, HttpMethod.GET, apiEndpoint);
        ResponseEntity<?> resp = portfolioService.portfolioDetails(req, portfolioId, page, pageSize);
        logger.info("portfolioDetails={}", resp.getStatusCode());

        // not authorized (HTTP 401)
        if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            return new ModelAndView("redirect:/login?from=member");
        }

        ModelAndView mv = new ModelAndView("member/portfolioDetails");
        mv.addObject("email", email);
        mv.addObject("pageNum", page);
        mv.addObject("pageSize", pageSize);

        // validation errors (HTTP 400), portfolio not found (HTTP 404),
        // or portfolio not belong to caller (HTTP 406)
        if (resp.getStatusCode() == HttpStatus.BAD_REQUEST
                || resp.getStatusCode() == HttpStatus.NOT_FOUND
                || resp.getStatusCode() == HttpStatus.NOT_ACCEPTABLE) {
            ErrorResponse errResp = (ErrorResponse) resp.getBody();
            mv.addObject("portfolioMessage", Objects.requireNonNull(errResp).errorMessage());
            return mv;
        }

        PortfolioDetailResponse actualResp = (PortfolioDetailResponse) resp.getBody();
        mv.addObject("portfolio", Objects.requireNonNull(actualResp).portfolio());
        mv.addObject("tradingTransactions", actualResp.tradingTransactions());
        Map<String, String> respHeaderMap = resp.getHeaders().toSingleValueMap();
        String first = respHeaderMap.get("first");
        String prev = respHeaderMap.get("prev");
        String next = respHeaderMap.get("next");
        String last = respHeaderMap.get("last");
        int totalPageCount = Objects.isNull(respHeaderMap.get("totalPageCount")) ? 0 : Integer.parseInt(respHeaderMap.get("totalPageCount"));
        boolean hasPrev = !Objects.isNull(respHeaderMap.get("hasPrev")) && Boolean.parseBoolean(respHeaderMap.get("hasPrev"));
        boolean hasNext = !Objects.isNull(respHeaderMap.get("hasNext")) && Boolean.parseBoolean(respHeaderMap.get("hasNext"));
        mv.addObject("hasRecords", totalPageCount > 0);
        mv.addObject("first", first);
        mv.addObject("prev", prev);
        mv.addObject("next", next);
        mv.addObject("last", last);
        mv.addObject("hasPrev", hasPrev);
        mv.addObject("hasNext", hasNext);

        if (Objects.nonNull(errorMessage)) {
            mv.addObject("portfolioMessage", errorMessage);
        }

        return mv;
    }

    /**
     * Links trading transactions to portfolio.
     * If failed, go to the member login page.
     *
     * @param session the HTTP session
     * @param request the HTTP request
     * @return the portfolio page, the member login page otherwise
     */
    @PostMapping("/member/linkToPortfolio")
    public ModelAndView linkToPortfolio(HttpSession session, HttpServletRequest request) {
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
            int page = Integer.parseInt(request.getParameter("pageNum"));
            int pageSize = Integer.parseInt(request.getParameter("pageSize"));
            boolean newPortfolio = Objects.nonNull(request.getParameter("portfolio"))
                    && request.getParameter("portfolio").equalsIgnoreCase("New");
            String portfolioName = request.getParameter("portName");
            String portfolioCurrency = request.getParameter("portCcy");
            String portfolioIdStr = request.getParameter("portfolio");
            BigInteger portfolioId = NumberUtils.isDigits(portfolioIdStr) ? new BigInteger(portfolioIdStr) : null;
            String[] links = Objects.requireNonNullElse(
                    request.getParameterValues("link"), new String[]{}); // null if nothing is selected
            List<BigInteger> tradingTransactionIds = Arrays.stream(links).map(BigInteger::new).toList();

            HttpHeaders headerMap = new HttpHeaders();
            headerMap.add("Authorization", "Bearer " + jwt);
            LinkTransactionRequest actualReq = new LinkTransactionRequest(newPortfolio,
                    newPortfolio ? new CreatePortfolioRequest(portfolioName, portfolioCurrency) : null,
                    newPortfolio ? null : portfolioId, tradingTransactionIds);
            URI apiEndpoint = URI.create("api/member/portfolios/link");

            RequestEntity<LinkTransactionRequest> req =
                    new RequestEntity<>(actualReq, headerMap, HttpMethod.GET, apiEndpoint);
            ResponseEntity<?> resp = portfolioService.linkToPortfolio(req);
            logger.info("linkToPortfolio={}", resp.getStatusCode());

            // not authorized (HTTP 401)
            if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new ModelAndView("redirect:/login?from=member");
            }

            // validation errors (HTTP 400),
            // resource not found (HTTP 404),
            // or other errors (HTTP 406)
            if (resp.getStatusCode() == HttpStatus.BAD_REQUEST
                    || resp.getStatusCode() == HttpStatus.NOT_FOUND
                    || resp.getStatusCode() == HttpStatus.NOT_ACCEPTABLE) {
                ErrorResponse errResp = (ErrorResponse) resp.getBody();
                return this.loadTradingTransactionPage(email, selectedCcy, page, pageSize,
                        headerMap, Objects.requireNonNull(errResp).errorMessage());
            }

            // success (HTTP 200) or no op (HTTP 304)
            return new ModelAndView("redirect:/member/portfolio");

        } else {
            return new ModelAndView("redirect:/login?from=member");
        }
    }

    /**
     * Returns the trading transaction page.
     *
     * @param email        the email address
     * @param selectedCcy  the selected currency
     * @param page         the page number to retrieve
     * @param pageSize     the number of records to retrieve
     * @param headerMap    the HTTP headers
     * @param errorMessage the error message
     * @return the trading transaction page
     */
    private ModelAndView loadTradingTransactionPage(String email, String selectedCcy, int page, int pageSize,
                                                    HttpHeaders headerMap, String errorMessage) {
        RequestEntity<Void> isoReq =
                new RequestEntity<>(headerMap, HttpMethod.GET, URI.create("/api/v1/member/iso/currencies"));
        ResponseEntity<?> isoResp = isoDataService.activeCurrencies(isoReq);
        logger.info("activeCurrencies={}", isoResp.getStatusCode());

        URI txnApiEndpoint = selectedCcy.isEmpty()
                ? URI.create("/api/v1/member/trading/transactions")
                : URI.create("/api/v1/member/trading/transactions/" + selectedCcy);
        RequestEntity<Void> txnReq = new RequestEntity<>(headerMap, HttpMethod.GET, txnApiEndpoint);
        ResponseEntity<?> txnResp = selectedCcy.isEmpty()
                ? tradingTxnService.tradingTransactions(txnReq, page, pageSize)
                : tradingTxnService.tradingTransactions(txnReq, selectedCcy, page, pageSize);
        logger.info("tradingTransactions={}", txnResp.getStatusCode());

        URI portApiEndpoint = selectedCcy.isEmpty()
                ? URI.create("/api/v1/member/portfolios")
                : URI.create("/api/v1/member/portfolios/" + selectedCcy);
        RequestEntity<Void> portReq = new RequestEntity<>(headerMap, HttpMethod.GET, portApiEndpoint);
        ResponseEntity<?> portResp = selectedCcy.isEmpty()
                ? portfolioService.portfolios(portReq, page, pageSize)
                : portfolioService.portfolios(portReq, selectedCcy, page, pageSize);
        logger.info("portfolios={}", portResp.getStatusCode());

        // not authorized (HTTP 401)
        if (txnResp.getStatusCode() == HttpStatus.UNAUTHORIZED
                || portResp.getStatusCode() == HttpStatus.UNAUTHORIZED
                || isoResp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            return new ModelAndView("redirect:/login?from=member");
        }

        ModelAndView mv = new ModelAndView("member/tradingTxn");
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

        if (Objects.nonNull(errorMessage)) {
            mv.addObject("errorMessage", errorMessage);
        }

        return mv;
    }

    /**
     * Unlinks trading transactions from portfolio.
     * If failed, go to the member login page.
     *
     * @param session the HTTP session
     * @param request the HTTP request
     * @return the portfolio page, the member login page otherwise
     */
    @PostMapping("/member/unlinkFromPortfolio")
    public ModelAndView unlinkFromPortfolio(HttpSession session, HttpServletRequest request) {
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

            int page = Integer.parseInt(request.getParameter("pageNum"));
            int pageSize = Integer.parseInt(request.getParameter("pageSize"));
            String portfolioIdStr = request.getParameter("portfolioId");
            String[] unlinks = Objects.requireNonNullElse(
                    request.getParameterValues("unlink"), new String[]{}); // null if nothing is selected
            List<BigInteger> tradingTransactionIds = Arrays.stream(unlinks).map(BigInteger::new).toList();

            HttpHeaders headerMap = new HttpHeaders();
            headerMap.add("Authorization", "Bearer " + jwt);
            UnlinkTransactionRequest actualReq =
                    new UnlinkTransactionRequest(new BigInteger(portfolioIdStr), tradingTransactionIds);
            URI apiEndpoint = URI.create("api/member/portfolios/link");

            RequestEntity<UnlinkTransactionRequest> req =
                    new RequestEntity<>(actualReq, headerMap, HttpMethod.GET, apiEndpoint);
            ResponseEntity<?> resp = portfolioService.unlinkFromPortfolio(req);
            logger.info("unlinkFromPortfolio={}", resp.getStatusCode());

            // not authorized (HTTP 401)
            if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new ModelAndView("redirect:/login?from=member");
            }

            // validation errors (HTTP 400),
            // resource not found (HTTP 404),
            // or other errors (HTTP 406)
            if (resp.getStatusCode() == HttpStatus.BAD_REQUEST
                    || resp.getStatusCode() == HttpStatus.NOT_FOUND
                    || resp.getStatusCode() == HttpStatus.NOT_ACCEPTABLE) {
                ErrorResponse errResp = (ErrorResponse) resp.getBody();
                return this.loadPortfolioDetailsPage(email, new BigInteger(portfolioIdStr), page, pageSize,
                        headerMap, Objects.requireNonNull(errResp).errorMessage());
            }

            // success (HTTP 200) or no op (HTTP 304)
            return new ModelAndView("redirect:/member/portfolio");

        } else {
            return new ModelAndView("redirect:/login?from=member");
        }
    }

}
