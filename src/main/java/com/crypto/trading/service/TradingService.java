package com.crypto.trading.service;

import com.crypto.trading.entity.AggregatedPrice;
import com.crypto.trading.repository.AggregatedPriceRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class TradingService {

    private static final String BINANCE_URL = "https://api.binance.com/api/v3/ticker/bookTicker";
    private static final String HUOBI_URL = "https://api.huobi.pro/market/tickers";

    private static final String[] CURRENT_SUPPORTED_SYMBOLS = {"ETHUSDT", "BTCUSDT"};

    private static final int timeIntervalInMS = 10000;

    private AggregatedPriceRepository aggregatedPriceRepository;

    public TradingService(AggregatedPriceRepository aggregatedPriceRepository) {
        this.aggregatedPriceRepository = aggregatedPriceRepository;
    }

    @Scheduled(fixedRate = timeIntervalInMS)
    public AggregatedPrice getAggregatePrices() {
        String binanceResponse = fetchPriceFromBinance();
        String huobiResponse = fetchPriceFromHuobi();

        for (String pair : CURRENT_SUPPORTED_SYMBOLS) {
            AggregatedPrice bestPrice = getBestPrice(pair, binanceResponse, huobiResponse);

            if (bestPrice != null) {
                return aggregatedPriceRepository.save(bestPrice);
            }
        }
        return null;
    }

    private String fetchPriceFromBinance() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BINANCE_URL))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String fetchPriceFromHuobi() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(HUOBI_URL))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private AggregatedPrice getBestPrice(String pair, String binanceResponse, String huobiResponse) {
        try{

            BigDecimal bestBid = null;
            BigDecimal bestAsk = null;
            String bestExchange = null;

            JSONArray binancePrices = new JSONArray(binanceResponse);
            for (int i = 0; i < binancePrices.length(); i++) {
                JSONObject obj = binancePrices.getJSONObject(i);
                if (pair.equals(obj.getString("symbol"))) {
                    BigDecimal bid = new BigDecimal(obj.getString("bidPrice"));
                    BigDecimal ask = new BigDecimal(obj.getString("askPrice"));
                    if (bestBid == null || bid.compareTo(bestBid) > 0) {
                        bestBid = bid;
                        bestExchange = "Binance";
                    }
                    if (bestAsk == null || ask.compareTo(bestAsk) < 0) {
                        bestExchange = "Binance";
                    }
                }
            }

            // Parse Huobi prices
            JSONObject huobiData = new JSONObject(huobiResponse);
            JSONArray huobiPrices = huobiData.getJSONArray("data");
            for (int i = 0; i < huobiPrices.length(); i++) {
                JSONObject obj = huobiPrices.getJSONObject(i);
                if (pair.toLowerCase().equals(obj.getString("symbol"))) {
                    BigDecimal bid = new BigDecimal(obj.get("bid").toString());
                    BigDecimal ask = new BigDecimal(obj.get("ask").toString());
                    if (bestBid == null || bid.compareTo(bestBid) > 0) {
                        bestBid = bid;
                        bestExchange = "Huobi";
                    }
                    if (bestAsk == null || ask.compareTo(bestAsk) < 0) {
                        bestAsk = ask;
                        bestExchange = "Huobi";
                    }
                }
            }

            if (bestBid != null && bestAsk != null) {
                AggregatedPrice result = new AggregatedPrice();
                result.setTickerSymbol(pair);
                result.setBidPrice(bestBid);
                result.setAskPrice(bestAsk);
                result.setExchange(bestExchange);
                result.setTimeStamp(Instant.now());
                return result;
            }

        }catch (Exception e) {

        }

        return null;
    }

}
