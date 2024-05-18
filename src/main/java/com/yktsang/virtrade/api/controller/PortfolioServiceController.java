/*
 * PortfolioServiceController.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.api.controller;

import com.yktsang.virtrade.api.jwt.JwtService;
import com.yktsang.virtrade.entity.*;
import com.yktsang.virtrade.request.CreatePortfolioRequest;
import com.yktsang.virtrade.request.LinkTransactionRequest;
import com.yktsang.virtrade.request.UnlinkTransactionRequest;
import com.yktsang.virtrade.response.ErrorResponse;
import com.yktsang.virtrade.response.PortfolioDetailResponse;
import com.yktsang.virtrade.response.PortfolioResponse;
import com.yktsang.virtrade.response.SuccessResponse;
import com.yktsang.virtrade.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The API controller for implementing <code>PortfolioService</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@RestController
public class PortfolioServiceController implements PortfolioService {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(PortfolioServiceController.class);
    /**
     * The JWT service.
     */
    @Autowired
    private JwtService jwtService;
    /**
     * The trading service.
     */
    @Autowired
    private TradingService tradingService;
    /**
     * The portfolio repository.
     */
    @Autowired
    private PortfolioRepository portfolioRepo;
    /**
     * The trading transaction repository.
     */
    @Autowired
    private TradingTransactionRepository tradingTxnRepo;
    /**
     * The ISO data repository.
     */
    @Autowired
    private IsoDataRepository isoDataRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> portfolios(RequestEntity<Void> req, int page, int pageSize) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        Set<IsoCurrency> activeCurrencies = isoDataRepo.findActiveIsoData(true).stream()
                .map(c -> new IsoCurrency(c.getCurrencyAlphaCode(), c.getCurrencyName()))
                .sorted().collect(Collectors.toCollection(LinkedHashSet::new));

        GenericHolder holder = this.getPortfolioResults(tokenUser, "", page, Math.max(pageSize, 1), activeCurrencies);
        List<com.yktsang.virtrade.response.Portfolio> portfolios =
                (List<com.yktsang.virtrade.response.Portfolio>) holder.items();
        HttpHeaders respHeaderMap = holder.headers();

        if (portfolios.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(respHeaderMap).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).headers(respHeaderMap)
                    .body(new PortfolioResponse(portfolios));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> portfolios(RequestEntity<Void> req, String currency, int page, int pageSize) {
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

        GenericHolder holder = this.getPortfolioResults(tokenUser, currency, page, Math.max(pageSize, 1), activeCurrencies);
        List<com.yktsang.virtrade.response.Portfolio> portfolios =
                (List<com.yktsang.virtrade.response.Portfolio>) holder.items();
        HttpHeaders respHeaderMap = holder.headers();

        if (portfolios.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(respHeaderMap).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).headers(respHeaderMap)
                    .body(new PortfolioResponse(portfolios));
        }
    }

    /**
     * Returns the <code>GenericHolder</code> containing <code>Portfolio</code>>.
     *
     * @param email            the email address
     * @param currency         the currency
     * @param page             the page number to retrieve
     * @param pageSize         the number of records to retrieve
     * @param activeCurrencies the active currencies
     * @return the GenericHolder
     */
    private GenericHolder getPortfolioResults(String email, String currency,
                                              int page, int pageSize, Set<IsoCurrency> activeCurrencies) {
        List<com.yktsang.virtrade.entity.Portfolio> dbPortfolios;
        if (currency.isEmpty()) {
            dbPortfolios = portfolioRepo.findByEmail(email);
        } else {
            dbPortfolios = portfolioRepo.findByEmailAndCurrency(email, currency);
        }
        List<com.yktsang.virtrade.response.Portfolio> respPortfolios = dbPortfolios.stream()
                //filter with active currencies
                .filter(c -> activeCurrencies.contains(new IsoCurrency(c.getCurrency())))
                //map to response format
                .map(p -> new com.yktsang.virtrade.response.Portfolio(p.getPortfolioId(), p.getEmail(),
                        p.getPortfolioName(), p.getCurrency(),
                        p.getInvestedAmount(), p.getCurrentAmount(), p.getProfitLoss()))
                .toList();

        Page<com.yktsang.virtrade.response.Portfolio> respPage;
        if (page == 0) {
            respPage = (Page<com.yktsang.virtrade.response.Portfolio>)
                    PaginationUtil.convertListToPage(respPortfolios, page, respPortfolios.isEmpty() ? 1 : respPortfolios.size());
        } else {
            respPage = (Page<com.yktsang.virtrade.response.Portfolio>)
                    PaginationUtil.convertListToPage(respPortfolios, page, pageSize);
        }
        HttpHeaders respHeaderMap = PaginationUtil.populateResponseHeader(respPage.getTotalElements(),
                respPage.getTotalPages(), page, respPage.getPageable().getPageSize(),
                respPage.hasPrevious(), respPage.hasNext(), currency);

        return new GenericHolder(respPage.getContent(), respHeaderMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> createPortfolio(RequestEntity<CreatePortfolioRequest> req) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        if (req.hasBody() && Objects.nonNull(req.getBody())) {
            CreatePortfolioRequest actualReq = req.getBody();

            if (
                    Objects.isNull(actualReq.name())
                            || actualReq.name().isEmpty()
                            || Objects.isNull(actualReq.currency())
                            || actualReq.currency().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Validation failed"));
            }

            String portfolioName = actualReq.name();
            String currency = actualReq.currency();

            Set<IsoCurrency> activeCurrencies = isoDataRepo.findActiveIsoData(true).stream()
                    .map(c -> new IsoCurrency(c.getCurrencyAlphaCode(), c.getCurrencyName()))
                    .sorted().collect(Collectors.toCollection(LinkedHashSet::new));

            if (!activeCurrencies.contains(new IsoCurrency(currency))) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Currency not found"));
            }

            Portfolio portfolio = new Portfolio(tokenUser, portfolioName, currency);
            portfolioRepo.save(portfolio);
            logger.info("portfolio created");

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new SuccessResponse("Portfolio created"));

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid request"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> portfolioDetails(RequestEntity<Void> req, BigInteger portfolioId, int page, int pageSize) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        if (Objects.isNull(portfolioId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Validation failed"));
        }

        Optional<Portfolio> portOpt = portfolioRepo.findById(portfolioId);
        if (portOpt.isPresent()) {
            com.yktsang.virtrade.entity.Portfolio dbPort = portOpt.get();

            if (!dbPort.getEmail().equals(tokenUser)) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(new ErrorResponse("Portfolio ID not belong to caller"));
            }

            Set<IsoCurrency> activeCurrencies = isoDataRepo.findActiveIsoData(true).stream()
                    .map(c -> new IsoCurrency(c.getCurrencyAlphaCode(), c.getCurrencyName()))
                    .sorted().collect(Collectors.toCollection(LinkedHashSet::new));

            GenericHolder holder = this.getTradingTransactionForPortfolioIdResults(tokenUser, dbPort,
                    page, Math.max(pageSize, 1), activeCurrencies);
            List<com.yktsang.virtrade.response.TradingTransaction> tradingTxns =
                    (List<com.yktsang.virtrade.response.TradingTransaction>) holder.items();
            HttpHeaders respHeaderMap = holder.headers();

            com.yktsang.virtrade.response.Portfolio respPort =
                    new com.yktsang.virtrade.response.Portfolio(dbPort.getPortfolioId(), dbPort.getEmail(),
                            dbPort.getPortfolioName(), dbPort.getCurrency(),
                            dbPort.getInvestedAmount(), dbPort.getCurrentAmount(), dbPort.getProfitLoss());

            return ResponseEntity.status(HttpStatus.OK).headers(respHeaderMap)
                    .body(new PortfolioDetailResponse(respPort, tradingTxns));

        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Portfolio not found"));
        }
    }

    /**
     * Returns the <code>GenericHolder</code> containing <code>TradingTransaction</code>>.
     *
     * @param email            the email address
     * @param dbPortfolio      the portfolio entity
     * @param page             the page number to retrieve
     * @param pageSize         the number of records to retrieve
     * @param activeCurrencies the active currencies
     * @return the GenericHolder
     */
    private GenericHolder getTradingTransactionForPortfolioIdResults(String email, Portfolio dbPortfolio,
                                                                     int page, int pageSize, Set<IsoCurrency> activeCurrencies) {
        List<com.yktsang.virtrade.entity.TradingTransaction> dbTradingTxns =
                tradingTxnRepo.findByPortfolioId(email, dbPortfolio.getPortfolioId());
        dbPortfolio.setTradingTransactions(dbTradingTxns);
        List<com.yktsang.virtrade.response.TradingTransaction> respTradingTxns = dbTradingTxns.stream()
                //filter with active currencies
                .filter(c -> activeCurrencies.contains(new IsoCurrency(c.getTransactionCurrency())))
                //map to response format
                .map(tt -> new com.yktsang.virtrade.response.TradingTransaction(tt.getTradingTransactionId(),
                        tt.getEmail(), tt.getTradingSymbol(),
                        URLEncoder.encode(tt.getTradingSymbol(), StandardCharsets.UTF_8),
                        tt.getTradingSymbolName(),
                        tt.getTransactionDate(), tt.getTradingDeed(), tt.getQuantity(),
                        tt.getTransactionCurrency(), tt.getTransactionPrice(), tt.getTransactionCost(),
                        tt.getPortfolioId()))
                .toList();

        Page<com.yktsang.virtrade.response.TradingTransaction> respPage;
        if (page == 0) {
            respPage = (Page<com.yktsang.virtrade.response.TradingTransaction>)
                    PaginationUtil.convertListToPage(respTradingTxns, page, respTradingTxns.isEmpty() ? 1 : respTradingTxns.size());
        } else {
            respPage = (Page<com.yktsang.virtrade.response.TradingTransaction>)
                    PaginationUtil.convertListToPage(respTradingTxns, page, pageSize);
        }
        HttpHeaders respHeaderMap = PaginationUtil.populateResponseHeader(respPage.getTotalElements(),
                respPage.getTotalPages(), page, respPage.getPageable().getPageSize(),
                respPage.hasPrevious(), respPage.hasNext(), "");

        return new GenericHolder(respPage.getContent(), respHeaderMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> linkToPortfolio(RequestEntity<LinkTransactionRequest> req) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        if (req.hasBody() && Objects.nonNull(req.getBody())) {
            LinkTransactionRequest actualReq = req.getBody();

            if (
                    Objects.isNull(actualReq.newPortfolio())
                            || Objects.isNull(actualReq.tradingTransactionIdsToLink())

                            // newPortfolio is true then check for portfolioRequest
                            || (actualReq.newPortfolio() && Objects.isNull(actualReq.portfolioRequest()))

                            // if portfolioRequest not null, check for name and currency inside portfolioRequest
                            || (Objects.nonNull(actualReq.portfolioRequest())
                            && (Objects.isNull(actualReq.portfolioRequest().name()) || actualReq.portfolioRequest().name().isEmpty()
                            || Objects.isNull(actualReq.portfolioRequest().currency()) || actualReq.portfolioRequest().currency().isEmpty()))

                            // newPortfolio is false then check for portfolioId
                            || (!actualReq.newPortfolio() && Objects.isNull(actualReq.portfolioId()))
            ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Validation failed"));
            }

            List<BigInteger> ttidWantToLink = actualReq.tradingTransactionIdsToLink();
            if (ttidWantToLink.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
            }

            // ensure all transaction currencies are the same
            String txnCurrency;
            List<String> a = new ArrayList<>();
            for (BigInteger id : ttidWantToLink) {
                tradingTxnRepo.findById(id).ifPresent(t -> a.add(t.getTransactionCurrency()));
            }
            Set<String> ccySet = new HashSet<>(a);

            if (ccySet.size() != 1) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(new ErrorResponse("Multiple currencies in provided trading transactions"));
            } else {
                txnCurrency = ccySet.iterator().next();
            }

            boolean createNewPortfolio = actualReq.newPortfolio();

            Set<IsoCurrency> activeCurrencies = isoDataRepo.findActiveIsoData(true).stream()
                    .map(c -> new IsoCurrency(c.getCurrencyAlphaCode(), c.getCurrencyName()))
                    .sorted().collect(Collectors.toCollection(LinkedHashSet::new));

            if (createNewPortfolio && !activeCurrencies.contains(new IsoCurrency(actualReq.portfolioRequest().currency()))) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Currency not found"));
            }

            BigInteger portfolioId;
            if (createNewPortfolio) {
                CreatePortfolioRequest crPortReq = actualReq.portfolioRequest();

                if (!crPortReq.currency().equalsIgnoreCase(txnCurrency)) {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(new ErrorResponse("Portfolio currency needs to match trading transaction currency"));
                }
                Portfolio newPortfolio = new Portfolio(tokenUser, crPortReq.name(), crPortReq.currency());
                newPortfolio = portfolioRepo.save(newPortfolio);
                portfolioId = newPortfolio.getPortfolioId();
                logger.info("new portfolio created for linking trading transactions");
            } else {
                portfolioId = actualReq.portfolioId();
            }

            Optional<Portfolio> portfolioOpt = portfolioRepo.findById(portfolioId);
            if (portfolioOpt.isPresent()) {
                Portfolio portfolio = portfolioOpt.get();

                if (!createNewPortfolio) {
                    // check portfolio belong to caller tokenUser
                    if (!portfolio.getEmail().equals(tokenUser)) {
                        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                                .body(new ErrorResponse("Portfolio ID not belong to caller"));
                    }
                    if (!portfolio.getCurrency().equalsIgnoreCase(txnCurrency)) {
                        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                                .body(new ErrorResponse("Portfolio currency needs to match trading transaction currency"));
                    }
                }

                List<TradingTransaction> txnsUnderPortfolioId =
                        tradingTxnRepo.findByPortfolioId(tokenUser, portfolio.getPortfolioId());
                List<BigInteger> ttidUnderPortfolioId = txnsUnderPortfolioId.stream()
                        .map(TradingTransaction::getTradingTransactionId)
                        .toList();
                long count = 0L;
                // check if ttidWantToLink indeed not belong to existing portfolio
                if (!new HashSet<>(ttidUnderPortfolioId).containsAll(ttidWantToLink)) {
                    for (BigInteger ttid : ttidWantToLink) {
                        Optional<TradingTransaction> ttOpt = tradingTxnRepo.findById(ttid);
                        if (ttOpt.isPresent()) {
                            TradingTransaction tt = ttOpt.get();
                            tt.setPortfolioId(portfolio.getPortfolioId());
                            tt.setLastUpdatedDateTime(LocalDateTime.now());
                            tradingTxnRepo.save(tt);
                            count++;
                            logger.info("trading transaction linked to portfolio");
                        }
                    }
                }

                if (count == 0L) {
                    return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
                } else {
                    this.recalculatePortfolioBalance(tokenUser, portfolio.getPortfolioId());
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new SuccessResponse("Link successful "
                                    + count + (createNewPortfolio ? ", portfolio created" : "")
                                    + " and portfolio balance updated"));
                }


            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Portfolio not found"));
            }

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid request"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> unlinkFromPortfolio(RequestEntity<UnlinkTransactionRequest> req) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        if (req.hasBody() && Objects.nonNull(req.getBody())) {
            UnlinkTransactionRequest actualReq = req.getBody();

            if (Objects.isNull(actualReq.portfolioId())
                    || Objects.isNull(actualReq.tradingTransactionIdsToUnlink())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Validation failed"));
            }

            Optional<Portfolio> portfolioOpt = portfolioRepo.findById(actualReq.portfolioId());
            if (portfolioOpt.isPresent()) {
                Portfolio portfolio = portfolioOpt.get();

                // check portfolio belong to caller tokenUser
                if (!portfolio.getEmail().equals(tokenUser)) {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(new ErrorResponse("Portfolio ID not belong to caller"));
                }

                List<BigInteger> ttidWantToUnlink = actualReq.tradingTransactionIdsToUnlink();

                if (!ttidWantToUnlink.isEmpty()) {
                    List<TradingTransaction> txnsUnderPortfolioId =
                            tradingTxnRepo.findByPortfolioId(tokenUser, portfolio.getPortfolioId());
                    List<BigInteger> ttidToUnlink = txnsUnderPortfolioId.stream()
                            .map(TradingTransaction::getTradingTransactionId)
                            // check ttidWantToUnlink indeed belong to existing portfolio
                            .filter(ttidWantToUnlink::contains)
                            .toList();
                    for (BigInteger ttid : ttidToUnlink) {
                        Optional<TradingTransaction> ttOpt = tradingTxnRepo.findById(ttid);
                        if (ttOpt.isPresent()) {
                            TradingTransaction tt = ttOpt.get();
                            tt.setPortfolioId(null);
                            tt.setLastUpdatedDateTime(LocalDateTime.now());
                            tradingTxnRepo.save(tt);
                            logger.info("trading transaction unlinked from portfolio");
                        }
                    }

                    if (ttidToUnlink.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
                    } else {
                        this.recalculatePortfolioBalance(tokenUser, portfolio.getPortfolioId());
                        return ResponseEntity.status(HttpStatus.OK)
                                .body(new SuccessResponse("Unlink successful "
                                        + ttidToUnlink.size() + " and portfolio balance updated"));
                    }

                } else {
                    return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
                }

            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Portfolio not found"));
            }

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid request"));
        }
    }

    /**
     * Recalculates the portfolio balance.
     *
     * @param email       the email address
     * @param portfolioId the portfolio ID
     */
    private void recalculatePortfolioBalance(String email, BigInteger portfolioId) {
        Optional<Portfolio> portfolioOpt = portfolioRepo.findById(portfolioId);
        if (portfolioOpt.isPresent()) {
            Portfolio portfolio = portfolioOpt.get();
            List<TradingTransaction> txns = tradingTxnRepo.findByPortfolioId(email, portfolioId);
            BigDecimal buyTransCost = txns.stream()
                    .filter(t -> t.getTradingDeed().equals(TradingDeed.BUY))
                    .map(TradingTransaction::getTransactionCost)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal sellTransCost = txns.stream()
                    .filter(t -> t.getTradingDeed().equals(TradingDeed.SELL))
                    .map(TradingTransaction::getTransactionCost)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal outstandingInvestedAmount = buyTransCost.subtract(sellTransCost);
            Set<OutstandingTradingTransaction> osTxns =
                    tradingService.getOutstandingTradingTransactions(email, txns);
            BigDecimal currentAmount = osTxns.stream()
                    .map(OutstandingTradingTransaction::getCurrentAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal pnlAmount = currentAmount.subtract(outstandingInvestedAmount);

            portfolio.setInvestedAmount(outstandingInvestedAmount);
            portfolio.setCurrentAmount(currentAmount);
            portfolio.setProfitLoss(pnlAmount);
            portfolio.setLastUpdatedDateTime(LocalDateTime.now());
            portfolioRepo.save(portfolio);
            logger.info("portfolio balance updated");
        }
    }

}
