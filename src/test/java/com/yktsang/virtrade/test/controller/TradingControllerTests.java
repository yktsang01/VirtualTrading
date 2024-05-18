/*
 * TradingControllerTests.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.test.controller;

import com.yktsang.virtrade.api.controller.*;
import com.yktsang.virtrade.api.jwt.JwtService;
import com.yktsang.virtrade.entity.RiskToleranceLevel;
import com.yktsang.virtrade.response.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static com.yktsang.virtrade.test.controller.MemberSessionHelper.getImproperMockSession;
import static com.yktsang.virtrade.test.controller.MemberSessionHelper.getProperMockSession;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Provides the test cases for <code>TradingController</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TradingControllerTests {

    /**
     * The mocked MVC.
     */
    @Autowired
    private MockMvc mvc;
    /**
     * The mocked JWT service.
     */
    @MockBean
    private JwtService jwtService;
    /**
     * The mocked ISO data service.
     */
    @MockBean
    private IsoDataService isoDataService;
    /**
     * The mocked trading service.
     */
    @MockBean
    private TradingService tradingService;
    /**
     * The mocked watch list service.
     */
    @MockBean
    private WatchListService watchListService;
    /**
     * The mocked portfolio service.
     */
    @MockBean
    private PortfolioService portfolioService;
    /**
     * The mocked account balance service.
     */
    @MockBean
    private AccountBalanceService accountBalanceService;
    /**
     * The mocked profile service.
     */
    @MockBean
    private ProfileService profileService;
    /**
     * The mocked bank account service.
     */
    @MockBean
    private BankAccountService bankAccountService;

    /**
     * Initializes the JWT.
     */
    @BeforeEach
    public void init() {
        when(jwtService.generateToken(anyString()))
                .thenReturn("sometoken");
        when(jwtService.extractExpiration(anyString()))
                .thenReturn(new Date());
        when(isoDataService.activeCurrencies(any()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(new IsoCurrencyResponse(new HashSet<>())));
    }

    /**
     * Shows the dashboard with no query parameter and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showMemberDashboardWithNoQueryParamAndProperSession() throws Exception {
        AccountBalanceResponse mockedAcctBalResp = new AccountBalanceResponse(new ArrayList<>());
        when(accountBalanceService.accountBalances(any(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedAcctBalResp));
        PortfolioResponse mockedPortResp = new PortfolioResponse(new ArrayList<>());
        when(portfolioService.portfolios(any(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedPortResp));
        WatchListResponse mockedWatchListResp = new WatchListResponse(new ArrayList<>());
        when(watchListService.watchList(any(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedWatchListResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/dashboard").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the dashboard with no query parameter and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showMemberDashboardWithNoQueryParamAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/dashboard").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the dashboard with no query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showMemberDashboardWithNoQueryParamValAndProperSession() throws Exception {
        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/dashboard?ccy=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the dashboard with no query parameter value and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showMemberDashboardWithNoQueryParamValAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/dashboard?ccy=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the dashboard with query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showMemberDashboardWithQueryParamValAndProperSession() throws Exception {
        AccountBalanceResponse mockedAcctBalResp = new AccountBalanceResponse(new ArrayList<>());
        when(accountBalanceService.accountBalances(any(), anyString()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedAcctBalResp));
        PortfolioResponse mockedPortResp = new PortfolioResponse(new ArrayList<>());
        when(portfolioService.portfolios(any(), anyString(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedPortResp));
        WatchListResponse mockedWatchListResp = new WatchListResponse(new ArrayList<>());
        when(watchListService.watchList(any(), anyString(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedWatchListResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/dashboard?ccy=XXX").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the dashboard with no query parameter value and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showMemberDashboardWithQueryParamValAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/dashboard?ccy=XXX").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Adds to watch list with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void addToWatchListWithProperSession() throws Exception {
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(watchListService.addToWatchList(any()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(post("/member/addToWatchList").session(mockHttpSession)
                        .param("addSelectedCcy", "XXX")
                        .param("add", "SYM01", "SYM02"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Adds to watch list with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void addToWatchListWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(post("/member/addToWatchList").session(mockHttpSession))
                // no  need param
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Removes from watch list with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void removeFromWatchListWithProperSession() throws Exception {
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(watchListService.removeFromWatchList(any()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(post("/member/removeFromWatchList").session(mockHttpSession)
                        .param("removeSelectedCcy", "XXX")
                        .param("remove", "SYM01", "SYM02"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Removes from watch list with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void removeFromWatchListWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(post("/member/removeFromWatchList").session(mockHttpSession))
                // no  need param
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Searches results with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void searchWithProperSession() throws Exception {
        SearchResultResponse mockedResp = new SearchResultResponse(new ArrayList<>());
        when(tradingService.search(any(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(post("/member/search").session(mockHttpSession)
                        .param("searchSelectedCcy", "XXX")
                        .param("wlPageNum", "1")
                        .param("wlPageSize", "5")
                        .param("srPageNum", "1")
                        .param("srPageSize", "5")
                        .param("stockType", "name")
                        .param("searchCriteria", "abc"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Searches results with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void searchWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(post("/member/search").session(mockHttpSession))
                // no  need param
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the buy page with no query parameter and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showBuyWithNoQueryParamAndProperSession() throws Exception {
        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/buy").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the buy page with no query parameter and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showBuyWithNoQueryParamAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/buy").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the buy page with no query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showBuyWithNoQueryParamValAndProperSession() throws Exception {
        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/buy?s=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the buy page with no query parameter value and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showBuyWithNoQueryParamValAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/buy?s=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the buy page with query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showBuyWithQueryParamValAndProperSession() throws Exception {
        SearchResult searchResult = new SearchResult("SYM", "SYM", "name",
                false, "XXX", BigDecimal.ONE);
        List<SearchResult> searchResults = new ArrayList<>();
        searchResults.add(searchResult);
        SearchResultResponse mockedResp = new SearchResultResponse(searchResults);
        when(tradingService.search(any(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));
        AccountBalance balance = new AccountBalance("user@domain.com", "XXX",
                BigDecimal.ZERO, BigDecimal.ZERO, 2);
        List<AccountBalance> balances = new ArrayList<>();
        balances.add(balance);
        AccountBalanceResponse mockedAcctBalResp = new AccountBalanceResponse(balances);
        when(accountBalanceService.accountBalances(any(), anyString()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedAcctBalResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/buy?s=SYM").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the buy page with query parameter value and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showBuyWithQueryParamValAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/buy?s=SYM").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Executes buy with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void executeBuyWithProperSession() throws Exception {
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(tradingService.buy(any()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(post("/member/buy").session(mockHttpSession)
                        .param("symbol", "SYM")
                        .param("action", "BUY")
                        .param("quantity", "100")
                        .param("name", "Some Company")
                        .param("currency", "XXX")
                        .param("transPrice", "50.00"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Executes buy with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void executeBuyWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(post("/member/buy").session(mockHttpSession))
                // no need param
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the sell page with no query parameter and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showSellWithNoQueryParamAndProperSession() throws Exception {
        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/sell").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the sell page with no query parameter and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showSellWithNoQueryParamAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/sell").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the sell page with no query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showSellWithNoQueryParamValAndProperSession() throws Exception {
        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/sell?s=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the sell page with no query parameter value and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showSellWithNoQueryParamValAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/sell?s=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the sell page with query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showSellWithQueryParamValAndProperSession() throws Exception {
        SearchResult searchResult = new SearchResult("SYM", "SYM", "name",
                false, "XXX", BigDecimal.ONE);
        List<SearchResult> searchResults = new ArrayList<>();
        searchResults.add(searchResult);
        SearchResultResponse mockedResp = new SearchResultResponse(searchResults);
        when(tradingService.search(any(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));
        TraderProfile trader = new TraderProfile("user@domain.com", "name",
                LocalDate.now(), true, RiskToleranceLevel.MEDIUM,
                false, true, LocalDateTime.now());
        ProfileResponse mockedProfResp = new ProfileResponse(trader);
        when(profileService.publicProfile(any()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedProfResp));
        BankAccountResponse mockedBankAcctResp = new BankAccountResponse(new ArrayList<>());
        when(bankAccountService.bankAccounts(any(), anyString(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedBankAcctResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/sell?s=SYM").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the sell page with query parameter value and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showSellWithQueryParamValAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/sell?s=SYM").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Executes sell with proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void executeSellWithProperSession() throws Exception {
        SuccessResponse mockedResp = new SuccessResponse("success");
        when(tradingService.sell(any()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(post("/member/sell").session(mockHttpSession)
                        .param("symbol", "SYM")
                        .param("action", "SELL")
                        .param("autoTransferToBank", "N")
                        .param("bankAcctId", "1")
                        .param("quantity", "100")
                        .param("name", "Some Company")
                        .param("currency", "XXX")
                        .param("transPrice", "50.00"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Executes sell with improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void executeSellWithImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(post("/member/sell").session(mockHttpSession))
                // no need param
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the indices page with no query parameter and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showIndicesWithNoQueryParamAndProperSession() throws Exception {
        YahooStockResponse mockedResp = new YahooStockResponse(new ArrayList<>());
        when(tradingService.indices(any(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/indices").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the indices page with no query parameter and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showIndicesWithNoQueryParamAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/indices").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the indices page with no query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showIndicesWithNoQueryParamValAndProperSession() throws Exception {
        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/indices?ccy=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the indices page with no query parameter and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showIndicesWithNoQueryParamValAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/indices?ccy=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the indices page with query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showIndicesWithQueryParamValAndProperSession() throws Exception {
        YahooStockResponse mockedResp = new YahooStockResponse(new ArrayList<>());
        when(tradingService.indices(any(), anyString(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/indices?ccy=XXX").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the indices page with query parameter value and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showIndicesWithQueryParamValAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/indices?ccy=XXX").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the equities page with no query parameter and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showEquitiesWithNoQueryParamAndProperSession() throws Exception {
        YahooStockResponse mockedResp = new YahooStockResponse(new ArrayList<>());
        when(tradingService.equities(any(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/equities").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the equities page with no query parameter and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showEquitiesWithNoQueryParamAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/equities").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the equities page with no query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showEquitiesWithNoQueryParamValAndProperSession() throws Exception {
        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/equities?ccy=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the equities page with no query parameter value and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showEquitiesWithNoQueryParamValAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/equities?ccy=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the equities page with query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showEquitiesWithQueryParamValAndProperSession() throws Exception {
        YahooStockResponse mockedResp = new YahooStockResponse(new ArrayList<>());
        when(tradingService.equities(any(), anyString(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/equities?ccy=XXX").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the equities page with query parameter value and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showEquitiesWithQueryParamAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/equities?ccy=XXX").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the outstanding transaction page with no query parameter and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showOutstandingTransactionWithNoQueryParamAndProperSession() throws Exception {
        OutstandingTransactionResponse mockedResp = new OutstandingTransactionResponse(new ArrayList<>());
        when(tradingService.outstandingTransactions(any(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/outstandingTxn").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the outstanding transaction page with no query parameter and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showOutstandingTransactionWithNoQueryParamAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/outstandingTxn").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the outstanding transaction page with no query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showOutstandingTransactionWithNoQueryParamValAndProperSession() throws Exception {
        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/outstandingTxn?ccy=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the outstanding transaction page with no query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showOutstandingTransactionWithNoQueryParamValAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/outstandingTxn?ccy=").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Shows the outstanding transaction page with query parameter value and proper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showOutstandingTransactionWithQueryParamValAndProperSession() throws Exception {
        OutstandingTransactionResponse mockedResp = new OutstandingTransactionResponse(new ArrayList<>());
        when(tradingService.outstandingTransactions(any(), anyString(), anyInt(), anyInt()))
                .thenAnswer(i -> ResponseEntity.status(HttpStatus.OK).body(mockedResp));

        MockHttpSession mockHttpSession = getProperMockSession();
        mvc.perform(get("/member/outstandingTxn?ccy=XXX").session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Shows the outstanding transaction page with query parameter value and improper HTTP session.
     *
     * @throws Exception when it is unable to load the page
     */
    @Test
    public void showOutstandingTransactionWithQueryParamValAndImproperSession() throws Exception {
        MockHttpSession mockHttpSession = getImproperMockSession();
        mvc.perform(get("/member/outstandingTxn?ccy=XXX").session(mockHttpSession))
                .andExpect(status().is3xxRedirection());
    }

}
