/*
 * TradingServiceController.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.api.controller;

import com.yktsang.virtrade.api.jwt.JwtService;
import com.yktsang.virtrade.entity.AccountBalance;
import com.yktsang.virtrade.entity.AccountTransaction;
import com.yktsang.virtrade.entity.BankAccount;
import com.yktsang.virtrade.entity.BankAccountTransaction;
import com.yktsang.virtrade.entity.TradingTransaction;
import com.yktsang.virtrade.entity.*;
import com.yktsang.virtrade.request.BuyRequest;
import com.yktsang.virtrade.request.SearchRequest;
import com.yktsang.virtrade.request.SellRequest;
import com.yktsang.virtrade.response.*;
import com.yktsang.virtrade.util.PaginationUtil;
import com.yktsang.virtrade.util.TradingUtil;
import com.yktsang.virtrade.yahoofinance.YahooFinanceSampler;
import com.yktsang.virtrade.yahoofinance.YahooStock;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The API controller for implementing <code>TradingService</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@RestController
public class TradingServiceController implements TradingService {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(TradingServiceController.class);
    /**
     * The JWT service.
     */
    @Autowired
    private JwtService jwtService;
    /**
     * The trading transaction repository.
     */
    @Autowired
    private TradingTransactionRepository tradingTxnRepo;
    /**
     * The account balance repository.
     */
    @Autowired
    private AccountBalanceRepository acctBalRepo;
    /**
     * The account transaction repository.
     */
    @Autowired
    private AccountTransactionRepository acctTxnRepo;
    /**
     * The bank account repository.
     */
    @Autowired
    private BankAccountRepository bankAcctRepo;
    /**
     * The bank account transaction repository.
     */
    @Autowired
    private BankAccountTransactionRepository bankAcctTxnRepo;
    /**
     * The ISO data repository.
     */
    @Autowired
    private IsoDataRepository isoDataRepo;
    /**
     * The URL for stocks.
     */
    @Value("${yahoo.stock.json}")
    private String stockJson; // from application.properties

    /**
     * {@inheritDoc}
     */
    @Override
    public int calculateOutstandingQuantity(String email, String symbol) {
        List<TradingTransaction> buys = tradingTxnRepo.findByTradingSymbol(email, TradingDeed.BUY, symbol);
        List<TradingTransaction> sells = tradingTxnRepo.findByTradingSymbol(email, TradingDeed.SELL, symbol);
        int buyQuantity = buys.stream().mapToInt(TradingTransaction::getQuantity).sum();
        int sellQuantity = sells.stream().mapToInt(TradingTransaction::getQuantity).sum();
        return buyQuantity - sellQuantity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean holdExisting(String email, String symbol) {
        List<TradingTransaction> buys = tradingTxnRepo.findByTradingSymbol(email, TradingDeed.BUY, symbol);
        return !buys.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<OutstandingTradingTransaction> getOutstandingTradingTransactions(String email,
                                                                                List<TradingTransaction> txns) {
        Set<OutstandingTradingTransaction> osTxns = txns.stream()
                .map(t -> new OutstandingTradingTransaction(t.getTradingSymbol(),
                        t.getTradingSymbolName(), t.getTransactionCurrency()))
                .collect(Collectors.toSet());
        YahooFinanceSampler sampler = new YahooFinanceSampler();
        List<YahooStock> equities = this.getStocks().stream()
                .filter(s -> s.getQuoteType().equalsIgnoreCase("equity"))
                .toList();

        Map<String, YahooStock> myMap = sampler.listToStockMap(equities);
        for (OutstandingTradingTransaction o : osTxns) {
            int osQuantity = this.calculateOutstandingQuantity(email, o.getTradingSymbol());
            o.setOutstandingQuantity(osQuantity);
        }
        Set<OutstandingTradingTransaction> filteredTxns = osTxns.stream()
                .filter(t -> t.getOutstandingQuantity() > 0)
                .collect(Collectors.toSet());
        for (OutstandingTradingTransaction o : filteredTxns) {
            BigDecimal currPrice = myMap.get(o.getTradingSymbol()).getQuote().getPrice();
            o.setCurrentPrice(currPrice);
            o.setCurrentAmount(currPrice.multiply(new BigDecimal(o.getOutstandingQuantity())));
        }
        return filteredTxns;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal calculateEstimatedCost(TradingDeed tradingDeed, BigDecimal transactionPrice, Integer quantity) {
        BigDecimal transCost = transactionPrice.multiply(new BigDecimal(quantity));
        BigDecimal fees = TradingUtil.calculateFees(transCost);
        // if buy then add fees; if sell then subtract fees
        return tradingDeed.equals(TradingDeed.BUY) ? transCost.add(fees) : transCost.subtract(fees);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<YahooStock> getStocks() {
        YahooFinanceSampler sampler = new YahooFinanceSampler();

        List<YahooStock> stocks = new ArrayList<>();
        try {
            stocks = sampler.readOnline(stockJson);
        } catch (IOException ioe) {
            logger.error(ioe.getMessage());
        }
        stocks.sort(Comparator.comparing(YahooStock::getSymbol));

        return stocks;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> indices(RequestEntity<Void> req, int page, int pageSize) {
        GenericHolder holder = this.getStockResults("index", "", page, Math.max(pageSize, 1));
        List<YahooStock> indices = (List<YahooStock>) holder.items();
        HttpHeaders respHeaderMap = holder.headers();

        if (indices.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(respHeaderMap).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).headers(respHeaderMap)
                    .body(new YahooStockResponse(indices));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> indices(RequestEntity<Void> req, String currency, int page, int pageSize) {
        if (Objects.isNull(currency)
                || currency.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Validation failed"));
        }

        Set<IsoCurrency> activeCurrencies = isoDataRepo.findActiveIsoData(true).stream()
                .map(c -> new IsoCurrency(c.getCurrencyAlphaCode(), c.getCurrencyName()))
                .sorted().collect(Collectors.toCollection(LinkedHashSet::new));

        if (!activeCurrencies.contains(new IsoCurrency(currency))) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Currency not found"));
        }

        GenericHolder holder = this.getStockResults("index", currency, page, Math.max(pageSize, 1));
        List<YahooStock> indices = (List<YahooStock>) holder.items();
        HttpHeaders respHeaderMap = holder.headers();

        if (indices.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(respHeaderMap).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).headers(respHeaderMap)
                    .body(new YahooStockResponse(indices));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> equities(RequestEntity<Void> req, int page, int pageSize) {
        GenericHolder holder = this.getStockResults("equity", "", page, Math.max(pageSize, 1));
        List<YahooStock> equities = (List<YahooStock>) holder.items();
        HttpHeaders respHeaderMap = holder.headers();

        if (equities.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(respHeaderMap).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).headers(respHeaderMap)
                    .body(new YahooStockResponse(equities));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> equities(RequestEntity<Void> req, String currency, int page, int pageSize) {
        if (Objects.isNull(currency)
                || currency.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Validation failed"));
        }

        Set<IsoCurrency> activeCurrencies = isoDataRepo.findActiveIsoData(true).stream()
                .map(c -> new IsoCurrency(c.getCurrencyAlphaCode(), c.getCurrencyName()))
                .sorted().collect(Collectors.toCollection(LinkedHashSet::new));

        if (!activeCurrencies.contains(new IsoCurrency(currency))) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Currency not found"));
        }

        GenericHolder holder = this.getStockResults("equity", currency, page, Math.max(pageSize, 1));
        List<YahooStock> equities = (List<YahooStock>) holder.items();
        HttpHeaders respHeaderMap = holder.headers();

        if (equities.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(respHeaderMap).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).headers(respHeaderMap)
                    .body(new YahooStockResponse(equities));
        }
    }

    /**
     * Returns the <code>GenericHolder</code> containing <code>YahooStock</code>>.
     *
     * @param quoteType the quote type (index or equity)
     * @param currency  the currency
     * @param page      the page number to retrieve
     * @param pageSize  the number of records to retrieve
     * @return the GenericHolder
     */
    private GenericHolder getStockResults(String quoteType, String currency, int page, int pageSize) {
        List<YahooStock> availableStocks = this.getStocks();
        List<YahooStock> stocks;
        if (currency.isEmpty()) {
            stocks = availableStocks.stream()
                    .filter(s -> s.getQuoteType().equalsIgnoreCase(quoteType))
                    .toList();
        } else {
            stocks = availableStocks.stream()
                    .filter(s -> s.getQuoteType().equalsIgnoreCase(quoteType))
                    .filter(s -> s.getCurrency().equalsIgnoreCase(currency))
                    .toList();
        }

        Page<YahooStock> respPage;
        if (page == 0) {
            respPage = (Page<YahooStock>) PaginationUtil.convertListToPage(stocks, page, stocks.isEmpty() ? 1 : stocks.size());
        } else {
            respPage = (Page<YahooStock>) PaginationUtil.convertListToPage(stocks, page, pageSize);
        }
        HttpHeaders respHeaderMap = PaginationUtil.populateResponseHeader(respPage.getTotalElements(),
                respPage.getTotalPages(), page, respPage.getPageable().getPageSize(),
                respPage.hasPrevious(), respPage.hasNext(), currency);
        return new GenericHolder(respPage.getContent(), respHeaderMap);
    }

    /**
     * Returns true if the value of the symbolOrName parameter in <code>SearchRequest</code>
     * not equals "name" and "symbol", false otherwise.
     *
     * @param valToCheck the value to check
     * @return true if the value not equals "name" and "symbol", false otherwise
     */
    private boolean checkValueNotEqualsSymbolAndName(String valToCheck) {
        return !valToCheck.equalsIgnoreCase("name") && !valToCheck.equalsIgnoreCase("symbol");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> search(RequestEntity<SearchRequest> req, int page, int pageSize) {
        if (req.hasBody() && Objects.nonNull(req.getBody())) {
            SearchRequest actualReq = req.getBody();

            if (
                    Objects.isNull(actualReq.symbolOrName())
                            || actualReq.symbolOrName().isEmpty()
                            || Objects.isNull(actualReq.criteria())
                            || actualReq.criteria().isEmpty()

                            // check symbolOrName indeed equals "name" or "symbol"
                            || this.checkValueNotEqualsSymbolAndName(actualReq.symbolOrName())
            ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Validation failed"));
            }

            String searchType = actualReq.symbolOrName();
            String searchCriteria = actualReq.criteria();

            GenericHolder holder = this.getSearchResultResults(searchType, searchCriteria, page, Math.max(pageSize, 1));
            List<SearchResult> searchResults = (List<SearchResult>) holder.items();
            HttpHeaders respHeaderMap = holder.headers();

            if (searchResults.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(respHeaderMap).build();
            } else {
                return ResponseEntity.status(HttpStatus.OK).headers(respHeaderMap)
                        .body(new SearchResultResponse(searchResults));
            }

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid request"));
        }
    }

    /**
     * Returns the <code>GenericHolder</code> containing <code>SearchResult</code>>.
     *
     * @param searchType     the search type
     * @param searchCriteria the search criteria
     * @param page           the page number to retrieve
     * @param pageSize       the number of records to retrieve
     * @return the GenericHolder
     */
    private GenericHolder getSearchResultResults(String searchType, String searchCriteria, int page, int pageSize) {
        List<YahooStock> searchResults;
        List<YahooStock> availableStocks = this.getStocks();
        if (searchType.equalsIgnoreCase("symbol")) {
            searchResults = availableStocks.stream()
                    .filter(s -> StringUtils.containsIgnoreCase(s.getSymbol(), searchCriteria))
                    .toList();
        } else {
            searchResults = availableStocks.stream()
                    .filter(s -> StringUtils.containsIgnoreCase(s.getName(), searchCriteria))
                    .toList();
        }
        List<SearchResult> respSearchResults = searchResults.stream()
                // map to response format
                .map(s -> new SearchResult(s.getSymbol(),
                        URLEncoder.encode(s.getSymbol(), StandardCharsets.UTF_8), s.getName(),
                        s.isIndex(), s.getCurrency(), s.getQuote().getPrice()))
                .toList();

        Page<SearchResult> respPage;
        if (page == 0) {
            respPage = (Page<SearchResult>) PaginationUtil.convertListToPage(respSearchResults, page, respSearchResults.isEmpty() ? 1 : respSearchResults.size());
        } else {
            respPage = (Page<SearchResult>) PaginationUtil.convertListToPage(respSearchResults, page, pageSize);
        }
        HttpHeaders respHeaderMap = PaginationUtil.populateSearchResultResponseHeader(respPage.getTotalElements(),
                respPage.getTotalPages(), page, respPage.getPageable().getPageSize(),
                respPage.hasPrevious(), respPage.hasNext());
        return new GenericHolder(respPage.getContent(), respHeaderMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> buy(RequestEntity<BuyRequest> req) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        if (req.hasBody() && Objects.nonNull(req.getBody())) {
            BuyRequest actualReq = req.getBody();

            if (
                    Objects.isNull(actualReq.symbol())
                            || actualReq.symbol().isEmpty()
                            || Objects.isNull(actualReq.quantityToBuy())

                            || actualReq.quantityToBuy() <= 0
            ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Validation failed"));
            }

            List<YahooStock> availableStocks = this.getStocks();
            List<YahooStock> stocksToBuy = availableStocks.stream()
                    .filter(s -> StringUtils.containsIgnoreCase(s.getSymbol(), actualReq.symbol()))
                    .toList();

            if (stocksToBuy.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Trading symbol not found"));
            }

            YahooStock stockToBuy = stocksToBuy.get(0);

            if (stockToBuy.isIndex()) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(new ErrorResponse("Trading symbol is an index rather than an equity"));
            }

            Optional<AccountBalance> acctBalOpt =
                    acctBalRepo.findById(new AccountBalancePK(tokenUser, stockToBuy.getCurrency()));

            if (acctBalOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Account balance for currency " + stockToBuy.getCurrency() + " not found"));
            }

            AccountBalance balance = acctBalOpt.get();
            Integer quantity = actualReq.quantityToBuy();
            BigDecimal transPrice = stockToBuy.getQuote().getPrice();

            BigDecimal transCost = transPrice.multiply(new BigDecimal(quantity));
            BigDecimal fees = TradingUtil.calculateFees(transCost);
            BigDecimal totalCost = transCost.add(fees);

            BigDecimal newNonTradingAmt = balance.getNonTradingAmount().subtract(totalCost);
            if (newNonTradingAmt.compareTo(BigDecimal.ZERO) < 0) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(new ErrorResponse("Insufficient funds for currency " + stockToBuy.getCurrency()));
            }

            balance.setNonTradingAmount(newNonTradingAmt);
            BigDecimal newTradingAmt = balance.getTradingAmount().add(totalCost);
            balance.setTradingAmount(newTradingAmt);
            balance.setLastUpdatedDateTime(LocalDateTime.now());
            acctBalRepo.save(balance);
            logger.info("account balance updated");

            String transactionDesc = quantity + " shares of "
                    + stockToBuy.getSymbol() + " at " + stockToBuy.getCurrency() + " " + transPrice
                    + ", total cost " + stockToBuy.getCurrency() + " "
                    + new DecimalFormat("#,###.0000").format(totalCost);

            AccountTransaction txn = new AccountTransaction(tokenUser, stockToBuy.getCurrency(),
                    "Bought " + transactionDesc);
            acctTxnRepo.save(txn);
            logger.info("account transaction created");

            TradingTransaction tradingTxn = new TradingTransaction(tokenUser,
                    stockToBuy.getSymbol(), stockToBuy.getName(), LocalDate.now(), TradingDeed.BUY,
                    quantity, stockToBuy.getCurrency(), transPrice, totalCost);
            tradingTxnRepo.save(tradingTxn);
            logger.info("trading transaction created");

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new SuccessResponse("Successfully bought " + transactionDesc));

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid request"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> sell(RequestEntity<SellRequest> req) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        if (req.hasBody() && Objects.nonNull(req.getBody())) {
            SellRequest actualReq = req.getBody();

            int outstandingQuantity = this.calculateOutstandingQuantity(tokenUser, actualReq.symbol());

            if (
                    Objects.isNull(actualReq.symbol())
                            || actualReq.symbol().isEmpty()
                            || Objects.isNull(actualReq.quantityToSell())
                            || Objects.isNull(actualReq.autoTransferToBank())

                            || actualReq.quantityToSell() <= 0

                            // quantityToSell cannot be greater than outstandingQuantity
                            || actualReq.quantityToSell() > outstandingQuantity

                            // if autoTransferToBank then check bankAccountId
                            || (actualReq.autoTransferToBank() && Objects.isNull(actualReq.bankAccountId()))
            ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Validation failed"));
            }

            List<YahooStock> availableStocks = this.getStocks();
            List<YahooStock> stocksToSell = availableStocks.stream()
                    .filter(s -> StringUtils.containsIgnoreCase(s.getSymbol(), actualReq.symbol()))
                    .toList();

            if (stocksToSell.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Trading symbol not found"));
            }

            YahooStock stockToSell = stocksToSell.get(0);

            if (stockToSell.isIndex()) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(new ErrorResponse("Trading symbol is an index rather than an equity"));
            }

            Optional<AccountBalance> acctBalOpt =
                    acctBalRepo.findById(new AccountBalancePK(tokenUser, stockToSell.getCurrency()));

            if (acctBalOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Account balance for currency " + stockToSell.getCurrency() + " not found"));
            }

            boolean transferToBank = actualReq.autoTransferToBank();
            Optional<BankAccount> toBankOpt = Optional.empty();
            if (transferToBank) {
                toBankOpt = bankAcctRepo.findById(actualReq.bankAccountId());
                if (toBankOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ErrorResponse("Bank account not found"));
                }
            }

            AccountBalance balance = acctBalOpt.get();
            Integer quantity = actualReq.quantityToSell();
            BigDecimal transPrice = stockToSell.getQuote().getPrice();

            BigDecimal transCost = transPrice.multiply(new BigDecimal(quantity));
            BigDecimal fees = TradingUtil.calculateFees(transCost);
            BigDecimal totalCost = transCost.subtract(fees);

            BigDecimal newTradingAmount = balance.getTradingAmount().subtract(totalCost);
            balance.setTradingAmount(newTradingAmount);
            BigDecimal newNonTradingAmt = balance.getNonTradingAmount().add(totalCost);
            balance.setNonTradingAmount(newNonTradingAmt);
            balance.setLastUpdatedDateTime(LocalDateTime.now());
            acctBalRepo.save(balance);
            logger.info("account balance updated");

            String transactionDesc = quantity + " shares of "
                    + stockToSell.getSymbol() + " at " + stockToSell.getCurrency() + " " + transPrice
                    + ", total cost " + stockToSell.getCurrency() + " "
                    + new DecimalFormat("#,###.0000").format(totalCost);

            AccountTransaction txn = new AccountTransaction(tokenUser, balance.getCurrency(),
                    "Sold " + transactionDesc);
            acctTxnRepo.save(txn);
            logger.info("account transaction created");

            TradingTransaction tradingTxn = new TradingTransaction(tokenUser,
                    stockToSell.getSymbol(), stockToSell.getName(), LocalDate.now(), TradingDeed.SELL,
                    quantity, stockToSell.getCurrency(), transPrice, totalCost);
            tradingTxnRepo.save(tradingTxn);
            logger.info("trading transaction created");

            String transferDesc = "";

            if (transferToBank) {
                BankAccount toBank = toBankOpt.get();
                BigDecimal newAmount = balance.getNonTradingAmount().subtract(totalCost);
                balance.setNonTradingAmount(newAmount);
                balance.setLastUpdatedDateTime(LocalDateTime.now());
                acctBalRepo.save(balance);
                logger.info("account balance updated");
                transferDesc = toBank.getBankName()
                        + " with account number " + toBank.getBankAccountNumber()
                        + " for currency " + toBank.getCurrency();
                String txnDesc = "Transferred " + balance.getCurrency() + " "
                        + new DecimalFormat("#,###.0000").format(totalCost)
                        + " to bank " + transferDesc;
                AccountTransaction acctTxn = new AccountTransaction(tokenUser, balance.getCurrency(), txnDesc);
                acctTxnRepo.save(acctTxn);
                logger.info("account transaction created");
                BankAccountTransaction bankTxn = new BankAccountTransaction(tokenUser, toBank.getCurrency(), txnDesc);
                bankAcctTxnRepo.save(bankTxn);
                logger.info("bank account transaction created");
                logger.info("funds transferred");
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Successfully sold ").append(transactionDesc);
            if (transferToBank) {
                sb.append(" and transferred amount to bank ").append(transferDesc);
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new SuccessResponse(sb.toString()));

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid request"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> outstandingTransactions(RequestEntity<Void> req, int page, int pageSize) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        GenericHolder holder = this.getOutstandingTransactionResults(tokenUser, "", page, Math.max(pageSize, 1));
        List<OutstandingTradingTransaction> osTxns = (List<OutstandingTradingTransaction>) holder.items();
        HttpHeaders respHeaderMap = holder.headers();

        if (osTxns.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(respHeaderMap).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).headers(respHeaderMap)
                    .body(new OutstandingTransactionResponse(osTxns));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> outstandingTransactions(RequestEntity<Void> req, String currency, int page, int pageSize) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        if (Objects.isNull(currency)
                || currency.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Validation failed"));
        }

        Set<IsoCurrency> activeCurrencies = isoDataRepo.findActiveIsoData(true).stream()
                .map(c -> new IsoCurrency(c.getCurrencyAlphaCode(), c.getCurrencyName()))
                .sorted().collect(Collectors.toCollection(LinkedHashSet::new));

        if (!activeCurrencies.contains(new IsoCurrency(currency))) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Currency not found"));
        }

        GenericHolder holder = this.getOutstandingTransactionResults(tokenUser, currency, page, Math.max(pageSize, 1));
        List<OutstandingTradingTransaction> osTxns = (List<OutstandingTradingTransaction>) holder.items();
        HttpHeaders respHeaderMap = holder.headers();

        if (osTxns.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(respHeaderMap).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).headers(respHeaderMap)
                    .body(new OutstandingTransactionResponse(osTxns));
        }
    }

    /**
     * Returns the <code>GenericHolder</code> containing <code>OutstandingTradingTransaction</code>>.
     *
     * @param email    the email address
     * @param currency the currency
     * @param page     the page number to retrieve
     * @param pageSize the number of records to retrieve
     * @return the GenericHolder
     */
    private GenericHolder getOutstandingTransactionResults(String email, String currency, int page, int pageSize) {
        List<com.yktsang.virtrade.entity.TradingTransaction> dbTradingTxns;
        if (currency.isEmpty()) {
            dbTradingTxns = tradingTxnRepo.findByEmail(email);
        } else {
            dbTradingTxns = tradingTxnRepo.findByEmailAndCurrency(email, currency);
        }
        Set<OutstandingTradingTransaction> osTxns = this.getOutstandingTradingTransactions(email, dbTradingTxns);
        List<OutstandingTradingTransaction> osTxnsList = osTxns.stream().toList();

        Page<OutstandingTradingTransaction> respPage;
        if (page == 0) {
            respPage = (Page<OutstandingTradingTransaction>) PaginationUtil.convertListToPage(osTxnsList, page, osTxnsList.isEmpty() ? 1 : osTxnsList.size());
        } else {
            respPage = (Page<OutstandingTradingTransaction>) PaginationUtil.convertListToPage(osTxnsList, page, pageSize);
        }
        HttpHeaders respHeaderMap = PaginationUtil.populateResponseHeader(respPage.getTotalElements(), respPage.getTotalPages(),
                page, respPage.getPageable().getPageSize(), respPage.hasPrevious(), respPage.hasNext(), currency);

        return new GenericHolder(respPage.getContent(), respHeaderMap);
    }

}
