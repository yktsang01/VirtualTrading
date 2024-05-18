/*
 * YahooFinanceSampler.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.yahoofinance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Provides the samples returned by Yahoo Finance API.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public class YahooFinanceSampler {

    /**
     * Reads a URL to return the samples.
     *
     * @param url the URL
     * @return the samples
     * @throws IOException when there is problem processing the input stream
     */
    public List<YahooStock> readOnline(String url) throws IOException {
        List<YahooStock> stocks = new ArrayList<>();

        Map<String, StockSymbol> stockSymbols = new HashMap<>();
        try {
            URL request = new URL(url);
            URLConnection connection = request.openConnection();
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(isr);
            if (node.has("stocks")) {
                node = node.get("stocks");
                for (int i = 0; i < node.size(); i++) {
                    JsonNode n = node.get(i);
                    stockSymbols.put(n.get("symbol").asText(),
                            new StockSymbol(n.get("symbol").asText(), n.get("description").asText(),
                                    n.get("type").asText(), n.get("currency").asText(), n.get("location").asText()));
                }
            } else {
                throw new IOException("Invalid response");
            }
        } catch (IOException ioe) {
            throw new IOException(ioe.getMessage());
        }

        String[] symbols = stockSymbols.keySet().toArray(new String[0]);
        try {
            Map<String, Stock> stockMap = YahooFinance.get(symbols);
            for (Map.Entry<String, Stock> entry : stockMap.entrySet()) {
                String symbol = entry.getKey();
                Stock s = entry.getValue();
                YahooStock stock = new YahooStock(symbol);
                stock.setEncodedSymbol(URLEncoder.encode(symbol, StandardCharsets.UTF_8));
                stock.setQuoteType(stockSymbols.get(symbol).type());
                if (stock.getQuoteType().equalsIgnoreCase("index")) {
                    stock.setIndex(true);
                }
                stock.setName(s.getName());
                stock.setCurrency(s.getCurrency());
                stock.setStockExchange(s.getStockExchange());
                stock.setQuote(s.getQuote());
                stock.setStats(s.getStats());
                stock.setDividend(s.getDividend());
                stocks.add(stock);
            }
        } catch (IOException ioe) {
            throw new IOException(ioe.getMessage());
        }
        return stocks;
    }

    /**
     * Converts the list to a map.
     *
     * @param stocks the list
     * @return the map
     */
    public Map<String, YahooStock> listToStockMap(List<YahooStock> stocks) {
        Map<String, YahooStock> stockMap = new TreeMap<>();
        for (YahooStock stock : stocks) {
            stockMap.put(stock.getSymbol(), stock);
        }
        return stockMap;
    }

}
