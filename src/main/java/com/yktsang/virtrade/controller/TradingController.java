/*
 * TradingController.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.controller;

import com.yktsang.virtrade.api.controller.*;
import com.yktsang.virtrade.entity.IsoCurrency;
import com.yktsang.virtrade.entity.OutstandingTradingTransaction;
import com.yktsang.virtrade.entity.TradingDeed;
import com.yktsang.virtrade.request.*;
import com.yktsang.virtrade.response.*;
import com.yktsang.virtrade.util.DateTimeUtil;
import com.yktsang.virtrade.yahoofinance.YahooStock;
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
import java.math.BigInteger;
import java.net.URI;
import java.time.LocalDate;
import java.util.*;

/**
 * The controller for trading.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Controller
public class TradingController {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(TradingController.class);
    /**
     * The trading service.
     */
    @Autowired
    private TradingService tradingService;
    /**
     * The watch list service.
     */
    @Autowired
    private WatchListService watchListService;
    /**
     * The portfolio service.
     */
    @Autowired
    private PortfolioService portfolioService;
    /**
     * The account balance service.
     */
    @Autowired
    private AccountBalanceService accountBalanceService;
    /**
     * The profile service.
     */
    @Autowired
    private ProfileService profileService;
    /**
     * The bank account service.
     */
    @Autowired
    private BankAccountService bankAccountService;
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
     * Shows the dashboard page.
     *
     * @param session              the HTTP session
     * @param currency             the Optional containing the currency
     * @param watchListPage        the Optional containing the watch list page
     * @param watchListPageSize    the Optional containing the watch list page size
     * @param searchResultPage     the Optional containing the search result page
     * @param searchResultPageSize the Optional containing the search result page size
     * @return the dashboard page, the member login page otherwise
     */
    @GetMapping("/member/dashboard")
    public ModelAndView showMemberDashboard(HttpSession session,
                                            @RequestParam("ccy") Optional<String> currency,
                                            @RequestParam("wlPage") Optional<Integer> watchListPage,
                                            @RequestParam("wlPageSize") Optional<Integer> watchListPageSize,
                                            @RequestParam("srPage") Optional<Integer> searchResultPage,
                                            @RequestParam("srPageSize") Optional<Integer> searchResultPageSize) {
        if (Objects.nonNull(session.getAttribute("email"))) {

            int defaultWatchListStartPage = watchListPage.orElse(1);
            int defaultWatchListPageSize = watchListPageSize.orElse(5);
            int defaultSearchStartPage = searchResultPage.orElse(1);
            int defaultSearchPageSize = searchResultPageSize.orElse(5);

            if (currency.isPresent() && currency.get().isEmpty()) {
                return new ModelAndView("redirect:/member/dashboard");
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
            return this.loadDashboardPage(email, currency.orElse(""), session,
                    defaultWatchListStartPage, defaultWatchListPageSize,
                    defaultSearchStartPage, defaultSearchPageSize,
                    headerMap, null);

        } else {
            return new ModelAndView("redirect:/login?from=member");
        }
    }

    /**
     * Returns the dashboard page.
     *
     * @param email                the email address
     * @param selectedCcy          the selected currency
     * @param session              the HTTP session
     * @param watchListPage        the page number to retrieve for watch list
     * @param watchListPageSize    the number of records to retrieve for watch list
     * @param searchResultPage     the page number to retrieve for search result
     * @param searchResultPageSize the number of records to retrieve for search result
     * @param headerMap            the HTTP headers
     * @param errorMessage         the error message
     * @return the dashboard page
     */
    private ModelAndView loadDashboardPage(String email, String selectedCcy, HttpSession session,
                                           int watchListPage, int watchListPageSize,
                                           int searchResultPage, int searchResultPageSize,
                                           HttpHeaders headerMap, String errorMessage) {
        SearchRequest savedSearchReq = Objects.nonNull(session.getAttribute("searchRequest"))
                ? (SearchRequest) session.getAttribute("searchRequest") : null;
        boolean hasSavedSearchReq = Objects.nonNull(savedSearchReq);

        RequestEntity<Void> isoReq =
                new RequestEntity<>(headerMap, HttpMethod.GET, URI.create("/api/v1/member/iso/currencies"));
        ResponseEntity<?> isoResp = isoDataService.activeCurrencies(isoReq);
        logger.info("activeCurrencies={}", isoResp.getStatusCode());

        URI balApiEndpoint = selectedCcy.isEmpty()
                ? URI.create("/api/v1/member/balances")
                : URI.create("/api/v1/member/balances/" + selectedCcy);
        RequestEntity<Void> acctBalReq =
                new RequestEntity<>(headerMap, HttpMethod.GET, balApiEndpoint);
        // no pagination for account balances, only show last 5 created
        ResponseEntity<?> acctBalResp = selectedCcy.isEmpty()
                ? accountBalanceService.accountBalances(acctBalReq, 1, 5)
                : accountBalanceService.accountBalances(acctBalReq, selectedCcy);
        logger.info("accountBalances={}", acctBalResp.getStatusCode());

        URI portApiEndpoint = selectedCcy.isEmpty()
                ? URI.create("/api/v1/member/portfolios")
                : URI.create("/api/v1/member/portfolios/" + selectedCcy);
        RequestEntity<Void> portReq =
                new RequestEntity<>(headerMap, HttpMethod.GET, portApiEndpoint);
        // no pagination for portfolios, only show last 5 created
        ResponseEntity<?> portResp = selectedCcy.isEmpty()
                ? portfolioService.portfolios(portReq, 1, 5)
                : portfolioService.portfolios(portReq, selectedCcy, 1, 5);
        logger.info("portfolios={}", portResp.getStatusCode());

        URI wlApiEndpoint = selectedCcy.isEmpty()
                ? URI.create("/api/v1/member/watchList")
                : URI.create("/api/v1/member/watchList/" + selectedCcy);
        RequestEntity<Void> wlReq =
                new RequestEntity<>(headerMap, HttpMethod.GET, wlApiEndpoint);
        // allow pagination for watch list
        ResponseEntity<?> wlResp = selectedCcy.isEmpty()
                ? watchListService.watchList(wlReq, watchListPage, watchListPageSize)
                : watchListService.watchList(wlReq, selectedCcy, watchListPage, watchListPageSize);
        logger.info("watchList={}", wlResp.getStatusCode());

        ResponseEntity<?> searchResp;
        if (hasSavedSearchReq) {
            URI searchApiEndpoint = URI.create("/api/v1/member/trading/search");
            RequestEntity<SearchRequest> searchReq =
                    new RequestEntity<>(savedSearchReq, headerMap, HttpMethod.POST, searchApiEndpoint);
            // allow pagination for search result
            searchResp = tradingService.search(searchReq, searchResultPage, searchResultPageSize);
            logger.info("search={}", searchResp.getStatusCode());
        } else {
            searchResp = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        // not authorized (HTTP 401)
        if (acctBalResp.getStatusCode() == HttpStatus.UNAUTHORIZED
                || isoResp.getStatusCode() == HttpStatus.UNAUTHORIZED
                || portResp.getStatusCode() == HttpStatus.UNAUTHORIZED
                || wlResp.getStatusCode() == HttpStatus.UNAUTHORIZED
                || searchResp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            return new ModelAndView("redirect:/login?from=member");
        }

        // validation errors (HTTP 400) or currency not found (HTTP 404)
        if (acctBalResp.getStatusCode() == HttpStatus.BAD_REQUEST
                || acctBalResp.getStatusCode() == HttpStatus.NOT_FOUND
                || portResp.getStatusCode() == HttpStatus.BAD_REQUEST
                || portResp.getStatusCode() == HttpStatus.NOT_FOUND
                || wlResp.getStatusCode() == HttpStatus.BAD_REQUEST
                || wlResp.getStatusCode() == HttpStatus.NOT_FOUND
                || searchResp.getStatusCode() == HttpStatus.BAD_REQUEST) {
            return new ModelAndView("redirect:/member/dashboard");
        }

        ModelAndView mv = new ModelAndView("member/dashboard");
        mv.addObject("email", email);
        mv.addObject("selectedCcy", selectedCcy);
        mv.addObject("wlPageNum", watchListPage);
        mv.addObject("wlPageSize", watchListPageSize);
        mv.addObject("srPageNum", searchResultPage);
        mv.addObject("srPageSize", searchResultPageSize);

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
        } else {
            AccountBalanceResponse actualResp = (AccountBalanceResponse) acctBalResp.getBody();
            List<AccountBalance> balances = Objects.requireNonNull(actualResp).balances();
            mv.addObject("balances", balances);
        }

        // success (HTTP 200) or no data (HTTP 204) for portfolio
        if (portResp.getStatusCode() == HttpStatus.NO_CONTENT) {
            mv.addObject("portfolios", new ArrayList<Portfolio>());
        } else {
            PortfolioResponse actualResp = (PortfolioResponse) portResp.getBody();
            List<Portfolio> portfolios = Objects.requireNonNull(actualResp).portfolios();
            mv.addObject("portfolios", portfolios);
        }

        // success (HTTP 200) or no data (HTTP 204) for watch list
        if (wlResp.getStatusCode() == HttpStatus.NO_CONTENT) {
            mv.addObject("watchList", new ArrayList<WatchList>());
            mv.addObject("hasWatchListRecords", false);
            mv.addObject("wlCurrent", "");
        } else {
            Map<String, String> respHeaderMap = wlResp.getHeaders().toSingleValueMap();
            String wlFirst = respHeaderMap.get("first");
            String wlPrev = respHeaderMap.get("prev");
            String wlNext = respHeaderMap.get("next");
            String wlLast = respHeaderMap.get("last");
            String wlCurrent = respHeaderMap.get("current");
            int wlTotalPageCount = Objects.isNull(respHeaderMap.get("totalPageCount")) ? 0 : Integer.parseInt(respHeaderMap.get("totalPageCount"));
            boolean wlHasPrev = !Objects.isNull(respHeaderMap.get("hasPrev")) && Boolean.parseBoolean(respHeaderMap.get("hasPrev"));
            boolean wlHasNext = !Objects.isNull(respHeaderMap.get("hasNext")) && Boolean.parseBoolean(respHeaderMap.get("hasNext"));
            WatchListResponse actualResp = (WatchListResponse) wlResp.getBody();
            mv.addObject("watchList", Objects.requireNonNull(actualResp).watchLists());
            mv.addObject("hasWatchListRecords", wlTotalPageCount > 0);
            mv.addObject("wlFirst", wlFirst);
            mv.addObject("wlPrev", wlPrev);
            mv.addObject("wlNext", wlNext);
            mv.addObject("wlLast", wlLast);
            mv.addObject("wlHasPrev", wlHasPrev);
            mv.addObject("wlHasNext", wlHasNext);
            mv.addObject("wlCurrent", wlCurrent);
        }

        // success (HTTP 200) or no data (HTTP 204) for search
        if (searchResp.getStatusCode() == HttpStatus.NO_CONTENT) {
            mv.addObject("hasSearchResults", false);
            mv.addObject("srCurrent", "");
        } else {
            Map<String, String> respHeaderMap = searchResp.getHeaders().toSingleValueMap();
            String srFirst = respHeaderMap.get("first");
            String srPrev = respHeaderMap.get("prev");
            String srNext = respHeaderMap.get("next");
            String srLast = respHeaderMap.get("last");
            String srCurrent = respHeaderMap.get("current");
            int srTotalPageCount = Objects.isNull(respHeaderMap.get("totalPageCount")) ? 0 : Integer.parseInt(respHeaderMap.get("totalPageCount"));
            boolean srHasPrev = !Objects.isNull(respHeaderMap.get("hasPrev")) && Boolean.parseBoolean(respHeaderMap.get("hasPrev"));
            boolean srHasNext = !Objects.isNull(respHeaderMap.get("hasNext")) && Boolean.parseBoolean(respHeaderMap.get("hasNext"));
            SearchResultResponse actualResp = (SearchResultResponse) searchResp.getBody();
            List<SearchResult> searchResults = Objects.requireNonNull(actualResp).searchResults();
            mv.addObject("hasSearchResults", srTotalPageCount > 0);
            mv.addObject("searchResults", searchResults);
            mv.addObject("srFirst", srFirst);
            mv.addObject("srPrev", srPrev);
            mv.addObject("srNext", srNext);
            mv.addObject("srLast", srLast);
            mv.addObject("srHasPrev", srHasPrev);
            mv.addObject("srHasNext", srHasNext);
            mv.addObject("srCurrent", srCurrent);
        }

        if (Objects.nonNull(errorMessage)) {
            mv.addObject("errorMessage", errorMessage);
        }
        return mv;
    }

    /**
     * Adds items to the watch list.
     *
     * @param session the HTTP session
     * @param request the HTTP request
     * @return the dashboard page, the member login page otherwise
     */
    @PostMapping("/member/addToWatchList")
    public ModelAndView addToWatchList(HttpSession session, HttpServletRequest request) {
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

            String[] stockSymbolsToAdd = Objects.requireNonNullElse(
                    request.getParameterValues("add"), new String[]{}); // null if nothing is selected
            List<String> symbolsToAdd = Arrays.stream(stockSymbolsToAdd).toList();

            HttpHeaders headerMap = new HttpHeaders();
            headerMap.add("Authorization", "Bearer " + jwt);
            AddWatchListStockRequest actualReq = new AddWatchListStockRequest(symbolsToAdd);
            URI apiEndpoint = URI.create("/api/v1/member/watchList/add");

            RequestEntity<AddWatchListStockRequest> req =
                    new RequestEntity<>(actualReq, headerMap, HttpMethod.POST, apiEndpoint);
            ResponseEntity<?> resp = watchListService.addToWatchList(req);
            logger.info("addToWatchList={}", resp.getStatusCode());

            // not authorized (HTTP 401)
            if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new ModelAndView("redirect:/login?from=member");
            }

            // success (HTTP 200) or no op (HTTP 304)
            return new ModelAndView("redirect:/member/dashboard");
        } else {
            return new ModelAndView("redirect:/login?from=member");
        }
    }

    /**
     * Removes items from the watch list.
     *
     * @param session the HTTP session
     * @param request the HTTP request
     * @return the dashboard page, the member login page otherwise
     */
    @PostMapping("/member/removeFromWatchList")
    public ModelAndView removeFromWatchList(HttpSession session, HttpServletRequest request) {
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

            String[] stockSymbolsToRemove = Objects.requireNonNullElse(
                    request.getParameterValues("remove"), new String[]{}); // null if nothing is selected
            List<String> symbolsToRemove = Arrays.stream(stockSymbolsToRemove).toList();

            HttpHeaders headerMap = new HttpHeaders();
            headerMap.add("Authorization", "Bearer " + jwt);
            RemoveWatchListStockRequest actualReq = new RemoveWatchListStockRequest(symbolsToRemove);
            URI apiEndpoint = URI.create("/api/v1/member/watchList/remove");

            RequestEntity<RemoveWatchListStockRequest> req =
                    new RequestEntity<>(actualReq, headerMap, HttpMethod.POST, apiEndpoint);
            ResponseEntity<?> resp = watchListService.removeFromWatchList(req);
            logger.info("removeFromWatchList={}", resp.getStatusCode());

            // not authorized (HTTP 401)
            if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new ModelAndView("redirect:/login?from=member");
            }

            // success (HTTP 200) or no op (HTTP 304)
            return new ModelAndView("redirect:/member/dashboard");
        } else {
            return new ModelAndView("redirect:/login?from=member");
        }
    }

    /**
     * Searches results.
     * If failed, go to the member login page.
     *
     * @param session the HTTP session
     * @param request the HTTP request
     * @return the dashboard page, the member login page otherwise
     */
    @PostMapping("/member/search")
    public ModelAndView search(HttpSession session, HttpServletRequest request) {
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

            String selectedCcy = request.getParameter("searchSelectedCcy");
            String searchType = request.getParameter("stockType");
            String searchCriteria = request.getParameter("searchCriteria");
            int wlPage = Integer.parseInt(request.getParameter("wlPageNum"));
            int wlPageSize = Integer.parseInt(request.getParameter("wlPageSize"));
            int srPage = Integer.parseInt(request.getParameter("srPageNum"));
            int srPageSize = Integer.parseInt(request.getParameter("srPageSize"));

            HttpHeaders headerMap = new HttpHeaders();
            headerMap.add("Authorization", "Bearer " + jwt);
            SearchRequest actualReq = new SearchRequest(searchType, searchCriteria);
            URI apiEndpoint = URI.create("/api/v1/member/trading/search");

            RequestEntity<SearchRequest> req =
                    new RequestEntity<>(actualReq, headerMap, HttpMethod.POST, apiEndpoint);
            ResponseEntity<?> resp = tradingService.search(req, 0, 0);
            logger.info("search={}", resp.getStatusCode());

            // not authorized (HTTP 401)
            if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new ModelAndView("redirect:/login?from=member");
            }

            // validation errors (HTTP 400)
            if (resp.getStatusCode() == HttpStatus.BAD_REQUEST) {
                ErrorResponse errResp = (ErrorResponse) resp.getBody();
                String errorMessage = Objects.requireNonNull(errResp).errorMessage();
                return this.loadDashboardPage(email, selectedCcy, session,
                        wlPage, wlPageSize, srPage, srPageSize,
                        headerMap, errorMessage);
            }

            // success (HTTP 200) or no data (HTTP 204) for search
            session.setAttribute("searchRequest", actualReq);
            // showing the actual search results in dashboard

            return new ModelAndView("redirect:/member/dashboard");

        } else {
            return new ModelAndView("redirect:/login?from=member");
        }
    }

    /**
     * Shows the buy page.
     * If failed, go to the member login page.
     *
     * @param session     the HTTP session
     * @param symbolToBuy the Optional containing the stock symbol to buy
     * @return the buy page, the member login page otherwise
     */
    @GetMapping("/member/buy")
    public ModelAndView showBuy(HttpSession session, @RequestParam("s") Optional<String> symbolToBuy) {
        if (Objects.nonNull(session.getAttribute("email"))) {

            if (symbolToBuy.isEmpty() || symbolToBuy.get().isEmpty()) {
                return new ModelAndView("redirect:/member/dashboard");
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
            return this.loadBuyPage(email, symbolToBuy.orElse(null), headerMap, Collections.emptyMap(), null);

        } else {
            return new ModelAndView("redirect:/login?from=member");
        }
    }

    /**
     * Returns the buy page.
     *
     * @param email        the email address
     * @param symbol       the trading symbol
     * @param headerMap    the HTTP headers
     * @param attributeMap the attribute map
     * @param errorMessage the error message
     * @return the buy page
     */
    private ModelAndView loadBuyPage(String email, String symbol, HttpHeaders headerMap,
                                     Map<String, Object> attributeMap, String errorMessage) {
        SearchRequest actualReq = new SearchRequest("symbol", symbol);

        RequestEntity<SearchRequest> req =
                new RequestEntity<>(actualReq, headerMap, HttpMethod.POST, URI.create("/api/v1/member/trading/search"));
        ResponseEntity<?> resp = tradingService.search(req, 0, 0);
        logger.info("search={}", resp.getStatusCode());

        // not authorized (HTTP 401)
        if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            return new ModelAndView("redirect:/login?from=member");
        }
        SearchResult stockToBuy;
        if (resp.getStatusCode() == HttpStatus.NO_CONTENT) {
            stockToBuy = null;
        } else {
            SearchResultResponse actualResp = (SearchResultResponse) resp.getBody();
            List<SearchResult> searchResults = Objects.requireNonNull(actualResp).searchResults();
            stockToBuy = searchResults.get(0);
        }

        ModelAndView mv = new ModelAndView("member/buy");
        mv.addObject("email", email);

        if (Objects.isNull(stockToBuy) || stockToBuy.index()) {
            mv.addObject("invalidStock", true);
        } else {
            mv.addObject("invalidStock", false);
            mv.addObject("stock", stockToBuy);

            // inputtedQuantity and estimatedCost
            for (Map.Entry<String, Object> entry : attributeMap.entrySet()) {
                mv.addObject(entry.getKey(), entry.getValue());
            }

            URI acctBalApiEndpoint = URI.create("/api/v1/member/balances/" + stockToBuy.currency());
            RequestEntity<Void> acctBalReq =
                    new RequestEntity<>(headerMap, HttpMethod.GET, acctBalApiEndpoint);
            ResponseEntity<?> acctBalResp = accountBalanceService.accountBalances(acctBalReq, stockToBuy.currency());
            logger.info("accountBalances={}", acctBalResp.getStatusCode());
            if (acctBalResp.getStatusCode() == HttpStatus.OK) {
                AccountBalanceResponse actualResp = (AccountBalanceResponse) acctBalResp.getBody();
                AccountBalance balance = Objects.requireNonNull(actualResp).balances().get(0);
                mv.addObject("missingBalance", false);
                mv.addObject("balance", balance);
            } else {
                mv.addObject("missingBalance", true);
            }

            mv.addObject("today", DateTimeUtil.toDate(LocalDate.now()));
        }

        if (Objects.nonNull(errorMessage)) {
            mv.addObject("errorMessage", errorMessage);
        }

        return mv;
    }

    /**
     * Executes buy.
     * If failed, go to the member login page.
     *
     * @param session the HTTP session
     * @param request the HTTP request
     * @return the trading transaction page, the member login page otherwise
     */
    @PostMapping("/member/buy")
    public ModelAndView executeBuy(HttpSession session, HttpServletRequest request) {
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

            String action = request.getParameter("action");
            String symbol = request.getParameter("symbol");
            String quantityStr = request.getParameter("quantity");
            BigDecimal transPrice = new BigDecimal(request.getParameter("transPrice"));

            HttpHeaders headerMap = new HttpHeaders();
            headerMap.add("Authorization", "Bearer " + jwt);

            boolean validNumFormat = Objects.nonNull(quantityStr) && NumberUtils.isDigits(quantityStr);
            logger.info("executeBuy validNumFormat={}", validNumFormat);

            if (validNumFormat) {
                int quantity = Integer.parseInt(request.getParameter("quantity"));

                if (action.equalsIgnoreCase("Calculate Cost")) {
                    BigDecimal estimatedCost = tradingService.calculateEstimatedCost(TradingDeed.BUY, transPrice, quantity);
                    Map<String, Object> attrMap = new HashMap<>();
                    attrMap.put("inputtedQuantity", String.valueOf(quantity));
                    attrMap.put("estimatedCost", estimatedCost);
                    return this.loadBuyPage(email, symbol, headerMap, attrMap, null);
                }

                BuyRequest actualReq = new BuyRequest(symbol, quantity);
                URI apiEndpoint = URI.create("/api/v1/member/trading/buy");

                RequestEntity<BuyRequest> req =
                        new RequestEntity<>(actualReq, headerMap, HttpMethod.POST, apiEndpoint);
                ResponseEntity<?> resp = tradingService.buy(req);
                logger.info("buy={}", resp.getStatusCode());

                // not authorized (HTTP 401)
                if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    return new ModelAndView("redirect:/login?from=member");
                }

                // validation errors (HTTP 400)
                // or resource not found (HTTP 404)
                // or other errors (HTTP 406)
                if (resp.getStatusCode() == HttpStatus.BAD_REQUEST
                        || resp.getStatusCode() == HttpStatus.NOT_FOUND
                        || resp.getStatusCode() == HttpStatus.NOT_ACCEPTABLE) {
                    ErrorResponse errResp = (ErrorResponse) resp.getBody();
                    String errorMessage = Objects.requireNonNull(errResp).errorMessage();
                    return this.loadBuyPage(email, symbol, headerMap, Collections.emptyMap(), errorMessage);
                }

            } else {
                return this.loadBuyPage(email, symbol, headerMap, Collections.emptyMap(), "Validation failed");
            }

            // success (HTTP 200)
            return new ModelAndView("redirect:/member/tradingTxn");
        } else {
            return new ModelAndView("redirect:/login?from=member");
        }
    }

    /**
     * Shows the sell page.
     * If failed, go to the member login page.
     *
     * @param session      the HTTP session
     * @param symbolToSell the Optional containing the stock symbol to sell
     * @return the sell page, the member login page otherwise
     */
    @GetMapping("/member/sell")
    public ModelAndView showSell(HttpSession session, @RequestParam("s") Optional<String> symbolToSell) {
        if (Objects.nonNull(session.getAttribute("email"))) {

            if (symbolToSell.isEmpty() || symbolToSell.get().isEmpty()) {
                return new ModelAndView("redirect:/member/outstandingTxn");
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

            return this.loadSellPage(email, symbolToSell.orElse(null), headerMap, Collections.emptyMap(), null);

        } else {
            return new ModelAndView("redirect:/login?from=member");
        }
    }

    /**
     * Returns the sell page.
     *
     * @param email        the email address
     * @param symbol       the trading symbol
     * @param headerMap    the HTTP headers
     * @param attributeMap the attribute map
     * @param errorMessage the error message
     * @return the sell page
     */
    private ModelAndView loadSellPage(String email, String symbol, HttpHeaders headerMap,
                                      Map<String, Object> attributeMap, String errorMessage) {
        SearchRequest actualReq = new SearchRequest("symbol", symbol);

        RequestEntity<SearchRequest> req =
                new RequestEntity<>(actualReq, headerMap, HttpMethod.POST, URI.create("/api/v1/member/trading/search"));
        ResponseEntity<?> resp = tradingService.search(req, 0, 0);
        logger.info("search={}", resp.getStatusCode());

        // not authorized (HTTP 401)
        if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            return new ModelAndView("redirect:/login?from=member");
        }

        SearchResult stockToSell;
        if (resp.getStatusCode() == HttpStatus.NO_CONTENT) {
            stockToSell = null;
        } else {
            SearchResultResponse actualResp = (SearchResultResponse) resp.getBody();
            List<SearchResult> searchResults = Objects.requireNonNull(actualResp).searchResults();
            stockToSell = searchResults.get(0);
        }

        ModelAndView mv = new ModelAndView("member/sell");
        mv.addObject("email", email);

        if (Objects.isNull(stockToSell) || stockToSell.index()
                || tradingService.holdExisting(email, stockToSell.currency())) {
            mv.addObject("invalidStock", true);
        } else {
            mv.addObject("invalidStock", false);

            int outstandingQuantity = tradingService.calculateOutstandingQuantity(email, stockToSell.symbol());
            if (outstandingQuantity > 0) {
                mv.addObject("outstandingQuantity", outstandingQuantity);
                mv.addObject("stock", stockToSell);

                // inputtedQuantity and estimatedCost
                for (Map.Entry<String, Object> entry : attributeMap.entrySet()) {
                    mv.addObject(entry.getKey(), entry.getValue());
                }

                RequestEntity<Void> profileReq = new RequestEntity<>(headerMap, HttpMethod.GET,
                        URI.create("/api/v1/member/profile/public"));
                ResponseEntity<?> profileResp = profileService.publicProfile(profileReq);
                logger.info("publicProfile={}", profileResp.getStatusCode());
                if (profileResp.getStatusCode() == HttpStatus.OK) {
                    ProfileResponse actualResp = (ProfileResponse) profileResp.getBody();
                    TraderProfile trader = Objects.requireNonNull(actualResp).getTrader();
                    mv.addObject("trader", trader);
                }

                RequestEntity<Void> bankAcctReq = new RequestEntity<>(headerMap, HttpMethod.GET,
                        URI.create("/api/v1/member/banks/" + stockToSell.currency()));
                ResponseEntity<?> bankAcctResp = bankAccountService.bankAccounts(bankAcctReq, stockToSell.currency(), 0, 0);
                logger.info("bankAccounts={}", bankAcctResp.getStatusCode());
                if (bankAcctResp.getStatusCode() == HttpStatus.NO_CONTENT) {
                    mv.addObject("bankAccounts", new ArrayList<BankAccount>());
                } else {
                    BankAccountResponse actualResp = (BankAccountResponse) bankAcctResp.getBody();
                    mv.addObject("bankAccounts", Objects.requireNonNull(actualResp).bankAccounts());
                }

                mv.addObject("today", DateTimeUtil.toDate(LocalDate.now()));
            } else {
                mv.addObject("noOutstandingShares",
                        "No outstanding shares to sell for "
                                + stockToSell.symbol() + " " + stockToSell.name());
            }
        }

        if (Objects.nonNull(errorMessage)) {
            mv.addObject("errorMessage", errorMessage);
        }

        return mv;
    }

    /**
     * Executes sell.
     * If failed, go to the member login page.
     *
     * @param session the HTTP session
     * @param request the HTTP request
     * @return the trading transaction page, the member login page otherwise
     */
    @PostMapping("/member/sell")
    public ModelAndView executeSell(HttpSession session, HttpServletRequest request) {
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

            String action = request.getParameter("action");
            String symbol = request.getParameter("symbol");
            String quantityStr = request.getParameter("quantity");
            BigDecimal transPrice = new BigDecimal(request.getParameter("transPrice"));
            boolean autoTransferToBank = Objects.nonNull(request.getParameter("autoTransferToBank"))
                    && request.getParameter("autoTransferToBank").equalsIgnoreCase("Y");
            BigInteger bankAcctId = Objects.nonNull(request.getParameter("bankAcctId"))
                    && request.getParameter("bankAcctId").equalsIgnoreCase("#")
                    ? null
                    : new BigInteger(request.getParameter("bankAcctId"));

            HttpHeaders headerMap = new HttpHeaders();
            headerMap.add("Authorization", "Bearer " + jwt);

            boolean validNumFormat = Objects.nonNull(quantityStr) && NumberUtils.isDigits(quantityStr);
            logger.info("executeSell validNumFormat={}", validNumFormat);

            if (validNumFormat) {
                int quantity = Integer.parseInt(request.getParameter("quantity"));

                if (action.equalsIgnoreCase("Calculate Cost")) {
                    BigDecimal estimatedCost = tradingService.calculateEstimatedCost(TradingDeed.SELL, transPrice, quantity);
                    Map<String, Object> attrMap = new HashMap<>();
                    attrMap.put("inputtedQuantity", String.valueOf(quantity));
                    attrMap.put("estimatedCost", estimatedCost);
                    return this.loadSellPage(email, symbol, headerMap, attrMap, null);
                }

                SellRequest actualReq = new SellRequest(symbol, quantity, autoTransferToBank, bankAcctId);
                URI apiEndpoint = URI.create("/api/v1/member/trading/sell");

                RequestEntity<SellRequest> req =
                        new RequestEntity<>(actualReq, headerMap, HttpMethod.POST, apiEndpoint);
                ResponseEntity<?> resp = tradingService.sell(req);
                logger.info("sell={}", resp.getStatusCode());

                // not authorized (HTTP 401)
                if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    return new ModelAndView("redirect:/login?from=member");
                }

                // validation errors (HTTP 400)
                // or resource not found (HTTP 404)
                // or other errors (HTTP 406)
                if (resp.getStatusCode() == HttpStatus.BAD_REQUEST
                        || resp.getStatusCode() == HttpStatus.NOT_FOUND
                        || resp.getStatusCode() == HttpStatus.NOT_ACCEPTABLE) {
                    ErrorResponse errResp = (ErrorResponse) resp.getBody();
                    String errorMessage = Objects.requireNonNull(errResp).errorMessage();
                    return this.loadSellPage(email, symbol, headerMap, Collections.emptyMap(), errorMessage);
                }

            } else {
                return this.loadSellPage(email, symbol, headerMap, Collections.emptyMap(), "Validation failed");
            }

            // success (HTTP 200)
            return new ModelAndView("redirect:/member/tradingTxn");
        } else {
            return new ModelAndView("redirect:/login?from=member");
        }
    }

    /**
     * Shows the indices page.
     *
     * @param session  the HTTP session
     * @param currency the Optional containing the currency
     * @param page     the Optional containing the page
     * @param pageSize the Optional containing the page size
     * @return the indices page, the member login page otherwise
     */
    @GetMapping("/member/indices")
    public ModelAndView showIndices(HttpSession session,
                                    @RequestParam("ccy") Optional<String> currency,
                                    @RequestParam("page") Optional<Integer> page,
                                    @RequestParam("pageSize") Optional<Integer> pageSize) {
        if (Objects.nonNull(session.getAttribute("email"))) {
            int defaultStartPage = page.orElse(1);
            int defaultPageSize = pageSize.orElse(5);

            if (currency.isPresent() && currency.get().isEmpty()) {
                return new ModelAndView("redirect:/member/indices");
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

            URI apiEndpoint = currency.map(s -> URI.create("/api/v1/member/trading/indices/" + s))
                    .orElseGet(() -> URI.create("/api/v1/member/trading/indices"));
            RequestEntity<Void> req =
                    new RequestEntity<>(headerMap, HttpMethod.GET, apiEndpoint);
            ResponseEntity<?> resp =
                    currency.<ResponseEntity<?>>map(s -> tradingService.indices(req, s, defaultStartPage, defaultPageSize))
                            .orElseGet(() -> tradingService.indices(req, defaultStartPage, defaultPageSize));
            logger.info("indices={}", resp.getStatusCode());

            // not authorized (HTTP 401)
            if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED
                    || isoResp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new ModelAndView("redirect:/login?from=member");
            }

            ModelAndView mv = new ModelAndView("member/indices");
            mv.addObject("email", email);
            mv.addObject("selectedCcy", currency.orElse(""));

            // success (HTTP 200) or no data (HTTP 204) for ISO currency
            if (isoResp.getStatusCode() == HttpStatus.NO_CONTENT) {
                mv.addObject("activeCurrencies", new HashSet<IsoCurrency>());
            } else {
                IsoCurrencyResponse actualResp = (IsoCurrencyResponse) isoResp.getBody();
                mv.addObject("activeCurrencies", Objects.requireNonNull(actualResp).activeCurrencies());
            }

            if (resp.getStatusCode() == HttpStatus.NO_CONTENT) {
                mv.addObject("indices", new ArrayList<YahooStock>());
                mv.addObject("hasRecords", false);
            } else {
                Map<String, String> respHeaderMap = resp.getHeaders().toSingleValueMap();
                String first = respHeaderMap.get("first");
                String prev = respHeaderMap.get("prev");
                String next = respHeaderMap.get("next");
                String last = respHeaderMap.get("last");
                int totalPageCount = Objects.isNull(respHeaderMap.get("totalPageCount")) ? 0 : Integer.parseInt(respHeaderMap.get("totalPageCount"));
                boolean hasPrev = !Objects.isNull(respHeaderMap.get("hasPrev")) && Boolean.parseBoolean(respHeaderMap.get("hasPrev"));
                boolean hasNext = !Objects.isNull(respHeaderMap.get("hasNext")) && Boolean.parseBoolean(respHeaderMap.get("hasNext"));
                YahooStockResponse actualResp = (YahooStockResponse) resp.getBody();
                mv.addObject("indices", Objects.requireNonNull(actualResp).stocks());
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

    /**
     * Shows the equities page.
     *
     * @param session  the HTTP session
     * @param currency the Optional containing the currency
     * @param page     the Optional containing the page
     * @param pageSize the Optional containing the page size
     * @return the equities page, the member login page otherwise
     */
    @GetMapping("/member/equities")
    public ModelAndView showEquities(HttpSession session,
                                     @RequestParam("ccy") Optional<String> currency,
                                     @RequestParam("page") Optional<Integer> page,
                                     @RequestParam("pageSize") Optional<Integer> pageSize) {
        if (Objects.nonNull(session.getAttribute("email"))) {
            int defaultStartPage = page.orElse(1);
            int defaultPageSize = pageSize.orElse(5);

            if (currency.isPresent() && currency.get().isEmpty()) {
                return new ModelAndView("redirect:/member/equities");
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

            URI apiEndpoint = currency.map(s -> URI.create("/api/v1/member/trading/equities/" + s))
                    .orElseGet(() -> URI.create("/api/v1/member/trading/equities"));
            RequestEntity<Void> req =
                    new RequestEntity<>(headerMap, HttpMethod.GET, apiEndpoint);
            ResponseEntity<?> resp =
                    currency.<ResponseEntity<?>>map(s -> tradingService.equities(req, s, defaultStartPage, defaultPageSize))
                            .orElseGet(() -> tradingService.equities(req, defaultStartPage, defaultPageSize));
            logger.info("equities={}", resp.getStatusCode());

            // not authorized (HTTP 401)
            if (resp.getStatusCode() == HttpStatus.UNAUTHORIZED
                    || isoResp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new ModelAndView("redirect:/login?from=member");
            }

            ModelAndView mv = new ModelAndView("member/equities");
            mv.addObject("email", email);
            mv.addObject("selectedCcy", currency.orElse(""));

            // success (HTTP 200) or no data (HTTP 204) for ISO currency
            if (isoResp.getStatusCode() == HttpStatus.NO_CONTENT) {
                mv.addObject("activeCurrencies", new HashSet<IsoCurrency>());
            } else {
                IsoCurrencyResponse actualResp = (IsoCurrencyResponse) isoResp.getBody();
                mv.addObject("activeCurrencies", Objects.requireNonNull(actualResp).activeCurrencies());
            }

            if (resp.getStatusCode() == HttpStatus.NO_CONTENT) {
                mv.addObject("equities", new ArrayList<YahooStock>());
                mv.addObject("hasRecords", false);
            } else {
                Map<String, String> respHeaderMap = resp.getHeaders().toSingleValueMap();
                String first = respHeaderMap.get("first");
                String prev = respHeaderMap.get("prev");
                String next = respHeaderMap.get("next");
                String last = respHeaderMap.get("last");
                int totalPageCount = Objects.isNull(respHeaderMap.get("totalPageCount")) ? 0 : Integer.parseInt(respHeaderMap.get("totalPageCount"));
                boolean hasPrev = !Objects.isNull(respHeaderMap.get("hasPrev")) && Boolean.parseBoolean(respHeaderMap.get("hasPrev"));
                boolean hasNext = !Objects.isNull(respHeaderMap.get("hasNext")) && Boolean.parseBoolean(respHeaderMap.get("hasNext"));
                YahooStockResponse actualResp = (YahooStockResponse) resp.getBody();
                mv.addObject("equities", Objects.requireNonNull(actualResp).stocks());
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

    /**
     * Shows the outstanding transaction page.
     * If failed, go to the member login page.
     *
     * @param session  the HTTP session
     * @param currency the Optional containing the currency
     * @param page     the Optional containing the page
     * @param pageSize the Optional containing the page size
     * @return the outstanding transaction page, the member login page otherwise
     */
    @GetMapping("/member/outstandingTxn")
    public ModelAndView showOutstandingTransaction(HttpSession session,
                                                   @RequestParam("ccy") Optional<String> currency,
                                                   @RequestParam("page") Optional<Integer> page,
                                                   @RequestParam("pageSize") Optional<Integer> pageSize) {
        if (Objects.nonNull(session.getAttribute("email"))) {

            int defaultStartPage = page.orElse(1);
            int defaultPageSize = pageSize.orElse(5);

            if (currency.isPresent() && currency.get().isEmpty()) {
                return new ModelAndView("redirect:/member/outstandingTxn");
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

            URI txnApiEndpoint = currency.map(s -> URI.create("/api/v1/member/trading/transactions/outstanding/" + s))
                    .orElseGet(() -> URI.create("/api/v1/member/trading/transactions/outstanding"));
            RequestEntity<Void> txnReq =
                    new RequestEntity<>(headerMap, HttpMethod.GET, txnApiEndpoint);
            ResponseEntity<?> txnResp =
                    currency.<ResponseEntity<?>>map(s -> tradingService.outstandingTransactions(txnReq, s, defaultStartPage, defaultPageSize))
                            .orElseGet(() -> tradingService.outstandingTransactions(txnReq, defaultStartPage, defaultPageSize));
            logger.info("outstandingTransactions={}", txnResp.getStatusCode());

            // not authorized (HTTP 401)
            if (txnResp.getStatusCode() == HttpStatus.UNAUTHORIZED
                    || isoResp.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new ModelAndView("redirect:/login?from=member");
            }

            // validation errors (HTTP 400) or currency not found (HTTP 404)
            if (txnResp.getStatusCode() == HttpStatus.BAD_REQUEST
                    || txnResp.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new ModelAndView("redirect:/member/outstandingTxn");
            }

            ModelAndView mv = new ModelAndView("member/outstandingTxn");
            mv.addObject("email", email);
            mv.addObject("selectedCcy", currency.orElse(""));

            // success (HTTP 200) or no data (HTTP 204) for ISO currency
            if (isoResp.getStatusCode() == HttpStatus.NO_CONTENT) {
                mv.addObject("activeCurrencies", new HashSet<IsoCurrency>());
            } else {
                IsoCurrencyResponse actualResp = (IsoCurrencyResponse) isoResp.getBody();
                mv.addObject("activeCurrencies", Objects.requireNonNull(actualResp).activeCurrencies());
            }

            // success (HTTP 200) or no data (HTTP 204) for outstanding transaction
            if (txnResp.getStatusCode() == HttpStatus.NO_CONTENT) {
                mv.addObject("osTransactions", new ArrayList<OutstandingTradingTransaction>());
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
                OutstandingTransactionResponse actualResp = (OutstandingTransactionResponse) txnResp.getBody();
                mv.addObject("osTransactions", Objects.requireNonNull(actualResp).outstandingTradingTransactions());
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
