/*
 * WatchListServiceController.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.api.controller;

import com.yktsang.virtrade.api.jwt.JwtService;
import com.yktsang.virtrade.entity.IsoCurrency;
import com.yktsang.virtrade.entity.IsoDataRepository;
import com.yktsang.virtrade.entity.WatchList;
import com.yktsang.virtrade.entity.WatchListRepository;
import com.yktsang.virtrade.request.AddWatchListStockRequest;
import com.yktsang.virtrade.request.RemoveWatchListStockRequest;
import com.yktsang.virtrade.response.ErrorResponse;
import com.yktsang.virtrade.response.SuccessResponse;
import com.yktsang.virtrade.response.WatchListResponse;
import com.yktsang.virtrade.util.PaginationUtil;
import com.yktsang.virtrade.yahoofinance.YahooFinanceSampler;
import com.yktsang.virtrade.yahoofinance.YahooStock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The API controller for implementing <code>WatchListService</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@RestController
public class WatchListServiceController implements WatchListService {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(WatchListServiceController.class);
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
     * The watch list repository.
     */
    @Autowired
    private WatchListRepository watchListRepo;
    /**
     * The ISO data repository.
     */
    @Autowired
    private IsoDataRepository isoDataRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> watchList(RequestEntity<Void> req, int page, int pageSize) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        Set<IsoCurrency> activeCurrencies = isoDataRepo.findActiveIsoData(true).stream()
                .map(c -> new IsoCurrency(c.getCurrencyAlphaCode(), c.getCurrencyName()))
                .sorted().collect(Collectors.toCollection(LinkedHashSet::new));

        GenericHolder holder = this.getWatchListResults(tokenUser, "", page, Math.max(pageSize, 1), activeCurrencies);
        List<com.yktsang.virtrade.response.WatchList> watchLists =
                (List<com.yktsang.virtrade.response.WatchList>) holder.items();
        HttpHeaders respHeaderMap = holder.headers();

        if (watchLists.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(respHeaderMap).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).headers(respHeaderMap)
                    .body(new WatchListResponse(watchLists));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> watchList(RequestEntity<Void> req, String currency, int page, int pageSize) {
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

        GenericHolder holder = this.getWatchListResults(tokenUser, currency, page, Math.max(pageSize, 1), activeCurrencies);
        List<com.yktsang.virtrade.response.WatchList> watchLists =
                (List<com.yktsang.virtrade.response.WatchList>) holder.items();
        HttpHeaders respHeaderMap = holder.headers();

        if (watchLists.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(respHeaderMap).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).headers(respHeaderMap)
                    .body(new WatchListResponse(watchLists));
        }
    }

    /**
     * Returns the <code>GenericHolder</code> containing <code>WatchList</code>>.
     *
     * @param email            the email address
     * @param currency         the currency
     * @param page             the page number to retrieve
     * @param pageSize         the number of records to retrieve
     * @param activeCurrencies the active currencies
     * @return the GenericHolder
     */
    private GenericHolder getWatchListResults(String email, String currency,
                                              int page, int pageSize, Set<IsoCurrency> activeCurrencies) {
        List<YahooStock> stocks = tradingService.getStocks();
        YahooFinanceSampler sampler = new YahooFinanceSampler();
        Map<String, YahooStock> stockMap = sampler.listToStockMap(stocks);

        List<com.yktsang.virtrade.entity.WatchList> dbWatchList;
        if (currency.isEmpty()) {
            dbWatchList = watchListRepo.findActiveByEmail(email);
        } else {
            dbWatchList = watchListRepo.findActiveByEmailAndCurrency(email, currency);
        }
        List<com.yktsang.virtrade.response.WatchList> respWatchList = dbWatchList.stream()
                //filter with active currencies
                .filter(w -> activeCurrencies.contains(new IsoCurrency(w.getCurrency())))
                //map to response format
                .map(w -> new com.yktsang.virtrade.response.WatchList(w.getSymbol(), w.getName(),
                        w.getCurrency(), stockMap.get(w.getSymbol()).getQuote().getPrice()))
                .toList();

        Page<com.yktsang.virtrade.response.WatchList> respPage;
        if (page == 0) {
            respPage = (Page<com.yktsang.virtrade.response.WatchList>)
                    PaginationUtil.convertListToPage(respWatchList, page, respWatchList.isEmpty() ? 1 : respWatchList.size());
        } else {
            respPage = (Page<com.yktsang.virtrade.response.WatchList>)
                    PaginationUtil.convertListToPage(respWatchList, page, pageSize);
        }
        HttpHeaders respHeaderMap = PaginationUtil.populateWatchListResponseHeader(respPage.getTotalElements(),
                respPage.getTotalPages(), page, respPage.getPageable().getPageSize(),
                respPage.hasPrevious(), respPage.hasNext(), currency);

        return new GenericHolder(respPage.getContent(), respHeaderMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> addToWatchList(RequestEntity<AddWatchListStockRequest> req) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        if (req.hasBody() && Objects.nonNull(req.getBody())) {
            AddWatchListStockRequest actualReq = req.getBody();

            if (Objects.isNull(actualReq.stockSymbolsToAdd())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Validation failed"));
            }

            List<String> symbolsToAdd = actualReq.stockSymbolsToAdd();
            List<WatchList> dbWatchList = watchListRepo.findActiveByEmail(tokenUser);

            if (!symbolsToAdd.isEmpty()) {
                List<YahooStock> stocksToAdd = tradingService.getStocks().stream()
                        .filter(stock -> symbolsToAdd.contains(stock.getSymbol()))
                        .toList();
                logger.info("something to add to watch list? {}", stocksToAdd.size());

                long count = 0L;
                for (YahooStock s : stocksToAdd) {
                    WatchList wl = new WatchList(tokenUser, s.getSymbol(), s.getName(), s.getCurrency());
                    if (!dbWatchList.contains(wl)) {
                        watchListRepo.save(wl);
                        count++;
                        logger.info("added to watch list {}", wl.getSymbol());
                    }
                }

                if (count == 0L) {
                    return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
                } else {
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new SuccessResponse("Successfully added "
                                    + count));
                }

            } else {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
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
    public ResponseEntity<?> removeFromWatchList(RequestEntity<RemoveWatchListStockRequest> req) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        if (req.hasBody() && Objects.nonNull(req.getBody())) {
            RemoveWatchListStockRequest actualReq = req.getBody();

            if (Objects.isNull(actualReq.stockSymbolsToRemove())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Validation failed"));
            }

            List<String> symbolsToRemove = actualReq.stockSymbolsToRemove();
            List<WatchList> dbWatchList = watchListRepo.findActiveByEmail(tokenUser);

            if (!symbolsToRemove.isEmpty()) {
                List<YahooStock> stocksToRemove = tradingService.getStocks().stream()
                        .filter(stock -> symbolsToRemove.contains(stock.getSymbol()))
                        .toList();
                logger.info("something to remove from watch list? {}", stocksToRemove.size());

                long count = 0L;
                for (YahooStock s : stocksToRemove) {
                    WatchList wl = new WatchList(tokenUser, s.getSymbol(), s.getName(), s.getCurrency());
                    if (dbWatchList.contains(wl)) {
                        int listIdx = dbWatchList.indexOf(wl);
                        WatchList wlToRemove = dbWatchList.get(listIdx);
                        wlToRemove.setRemovalDateTime(LocalDateTime.now());
                        watchListRepo.save(wlToRemove);
                        count++;
                        logger.info("removed from watch list {}id={}", wlToRemove.getSymbol(), wlToRemove.getWatchListId());
                    }
                }

                if (count == 0L) {
                    return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
                } else {
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new SuccessResponse("Successfully removed "
                                    + count));
                }

            } else {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
            }

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid request"));
        }
    }

}
