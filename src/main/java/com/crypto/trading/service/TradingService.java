package com.crypto.trading.service;

import com.crypto.trading.entity.*;
import com.crypto.trading.repository.AggregatedPriceRepository;
import com.crypto.trading.repository.TradeHistoryRespository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class TradingService {

    private static final String BINANCE_URL = "https://api.binance.com/api/v3/ticker/bookTicker";
    private static final String HUOBI_URL = "https://api.huobi.pro/market/tickers";

    private static final String[] CURRENT_SUPPORTED_SYMBOLS = {"ETHUSDT", "BTCUSDT"};

    private static final int timeIntervalInMS = 10000;

    private AggregatedPriceRepository aggregatedPriceRepository;

    private UserService userService;

    private TradeHistoryRespository tradeHistoryRespository;

    public TradingService(AggregatedPriceRepository aggregatedPriceRepository, UserService userService,
                          TradeHistoryRespository tradeHistoryRespository) {
        this.aggregatedPriceRepository = aggregatedPriceRepository;
        this.userService = userService;
        this.tradeHistoryRespository = tradeHistoryRespository;
    }

    @Scheduled(fixedRate = timeIntervalInMS)
    public AggregatedPrice getAggregatePricesForETHUSDT() {
        String binanceResponse = fetchPriceFromBinance();
        String huobiResponse = fetchPriceFromHuobi();


        AggregatedPrice bestPrice = getBestPrice("ETHUSDT", binanceResponse, huobiResponse);
        if (bestPrice != null) {
            return aggregatedPriceRepository.save(bestPrice);
        }
        return null;
    }

    @Scheduled(fixedRate = timeIntervalInMS)
    public AggregatedPrice getAggregatePricesForBTCUSDT() {
        String binanceResponse = fetchPriceFromBinance();
        String huobiResponse = fetchPriceFromHuobi();

        AggregatedPrice bestPrice = getBestPrice("BTCUSDT", binanceResponse, huobiResponse);

        if (bestPrice != null) {
            return aggregatedPriceRepository.save(bestPrice);
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

    private AggregatedPrice getBestPrice(String symbol, String binanceResponse, String huobiResponse) {
        try {
            // Initialize bestBid and bestAsk with the lowest possible value
            double bestBid = Double.MIN_VALUE;  // Represents an uninitialized bestBid
            double bestAsk = Double.MAX_VALUE;  // Represents an uninitialized bestAsk
            String bestExchange = null;

            // Parse Binance response
            JSONArray binancePrices = new JSONArray(binanceResponse);
            for (int i = 0; i < binancePrices.length(); i++) {
                JSONObject obj = binancePrices.getJSONObject(i);
                if (symbol.equals(obj.getString("symbol"))) {
                    double bid = Double.parseDouble(obj.getString("bidPrice"));
                    double ask = Double.parseDouble(obj.getString("askPrice"));

                    if (bid > bestBid) {
                        bestBid = bid;
                    }

                    if (ask < bestAsk) {
                        bestAsk = ask;
                    }
                }
            }

            // Parse Huobi response
            JSONObject huobiData = new JSONObject(huobiResponse);
            JSONArray huobiPrices = huobiData.getJSONArray("data");
            for (int i = 0; i < huobiPrices.length(); i++) {
                JSONObject obj = huobiPrices.getJSONObject(i);
                if (symbol.toLowerCase().equals(obj.getString("symbol"))) {
                    double bid = Double.parseDouble(obj.get("bid").toString());
                    double ask = Double.parseDouble(obj.get("ask").toString());

                    if (bid > bestBid) {
                        bestBid = bid;
                    }

                    if (ask < bestAsk) {
                        bestAsk = ask;
                    }
                }
            }

            // If valid bestBid and bestAsk are found, create the result
            if (bestBid != Double.MIN_VALUE && bestAsk != Double.MAX_VALUE) {
                AggregatedPrice result = new AggregatedPrice();
                result.setTickerSymbol(symbol);
                result.setBidPrice(bestBid);
                result.setAskPrice(bestAsk);
                result.setExchange(bestExchange);
                result.setTimeStamp(Instant.now());
                return result;
            }

        } catch (Exception e) {
            // Handle exception (log it or rethrow)
        }

        return null;
    }

    public TradeHistory buy(long id, TradeRequest tradeRequest, AggregatedPrice latestPrice) {
        double totalAmount = latestPrice.getAskPrice() * tradeRequest.getQuantity();

        Optional<User> optionalUser = userService.getUserById(id);
        optionalUser.ifPresentOrElse(user -> {
            Wallet USDTWallet = user.getWalletBySymbol("USDT");
            double USDTBalance = USDTWallet.getBalance();

            if(totalAmount > USDTBalance) {
                throw new RuntimeException("INSUFFICIENT AMOUNT");
            }

            Wallet curSymbolWallet = user.getWalletBySymbol(tradeRequest.getSymbol());
            if(curSymbolWallet == null) {
                Wallet newWallet = new Wallet();
                newWallet.setSymbol(tradeRequest.getSymbol());
                newWallet.setBalance(totalAmount);
                newWallet.setUser(user);
                user.getWallets().add(newWallet);
            } else {
                double curSymbolBalance = curSymbolWallet.getBalance();
                curSymbolWallet.setBalance(curSymbolBalance + totalAmount);
            }
            USDTWallet.setBalance(USDTBalance - totalAmount);
            userService.updateUser(id, user);

            TradeHistory tradeHistory = new TradeHistory();
            tradeHistory.setSymbol(tradeRequest.getSymbol());
            tradeHistory.setQuantity(tradeRequest.getQuantity());
            tradeHistory.setPrice(totalAmount);
            tradeHistory.setSide("BUY");
            tradeHistory.setUserId(id);
            tradeHistory.setTimeStamp(Instant.now());
            tradeHistoryRespository.save(tradeHistory);


        }, () -> {});

        return null;
    }

    public TradeHistory sell(long id, TradeRequest tradeRequest, AggregatedPrice latestPrice) {
        //calculate the total amount earned by the bid price
        double totalAmount = latestPrice.getBidPrice() * tradeRequest.getQuantity();

        Optional<User> optionalUser = userService.getUserById(id);
        optionalUser.ifPresentOrElse(user -> {
            Wallet curSymbolWallet = user.getWalletBySymbol(tradeRequest.getSymbol());
            double curSymbolBalance = curSymbolWallet.getBalance();

            if(tradeRequest.getQuantity() > curSymbolBalance || totalAmount > curSymbolBalance) {
                throw new RuntimeException("INSUFFICIENT QUANTITY OR AMOUNT TO BE ABLE SELL");
            }

            if(curSymbolWallet != null) {
                curSymbolWallet.setBalance(curSymbolBalance - tradeRequest.getQuantity());
            }

            Wallet USDTWallet = user.getWalletBySymbol("USDT");
            USDTWallet.setBalance(USDTWallet.getBalance() + totalAmount);

            userService.updateUser(id, user);

            TradeHistory tradeHistory = new TradeHistory();
            tradeHistory.setSymbol(tradeRequest.getSymbol());
            tradeHistory.setQuantity(tradeRequest.getQuantity());
            tradeHistory.setPrice(totalAmount);
            tradeHistory.setSide("SELL");
            tradeHistory.setUserId(id);
            tradeHistory.setTimeStamp(Instant.now());
            tradeHistoryRespository.save(tradeHistory);


        }, () -> {});

        return null;
    }

}
