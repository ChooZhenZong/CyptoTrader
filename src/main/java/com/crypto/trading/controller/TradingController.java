package com.crypto.trading.controller;

import com.crypto.trading.entity.AggregatedPrice;
import com.crypto.trading.entity.Authentication;
import com.crypto.trading.entity.TradeRequest;
import com.crypto.trading.repository.AggregatedPriceRepository;
import com.crypto.trading.service.TradingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/v1/api/trade")
public class TradingController {

    private AggregatedPriceRepository aggregatedPriceRepository;
    private TradingService tradingService;

    private static final String[] CURRENT_SUPPORTED_SYMBOLS = {"ETHUSDT", "BTCUSDT"};

    public TradingController(AggregatedPriceRepository aggregatedPriceRepository, TradingService tradingService) {
        this.aggregatedPriceRepository = aggregatedPriceRepository;
        this.tradingService = tradingService;
    }

    @GetMapping("/getAggregatedPrices")
    public ResponseEntity<?> getLatestAggregatedPrices(@RequestParam String symbol) {
        if (!Arrays.asList(CURRENT_SUPPORTED_SYMBOLS).contains(symbol)) {
            return ResponseEntity.badRequest().body("Symbol not supported");
        }

        AggregatedPrice latestPrice = aggregatedPriceRepository.findFirstByTickerSymbolOrderByTimeStampDesc(symbol);
        return latestPrice != null ? ResponseEntity.ok(latestPrice) : ResponseEntity.notFound().build();
    }

    @PostMapping("/buy")
    public ResponseEntity<?> buyPosition(@RequestHeader("X-USER-ID") long userId, @RequestBody TradeRequest tradeRequest) {
        if(tradeRequest == null || tradeRequest.getSymbol() == null|| tradeRequest.getSymbol().isBlank()) {
            return ResponseEntity.badRequest().body("Missing or empty 'symbol' parameter");
        }

        if (!Arrays.asList(CURRENT_SUPPORTED_SYMBOLS).contains(tradeRequest.getSymbol())) {
            return ResponseEntity.badRequest().body("Symbol not supported");
        }

        AggregatedPrice latestPrice = aggregatedPriceRepository.findFirstByTickerSymbolOrderByTimeStampDesc(tradeRequest.getSymbol());
        tradingService.buy(userId, tradeRequest, latestPrice);

        return null;
    }

    @PostMapping("/sell")
    public ResponseEntity<?> sellPosition(@RequestHeader("X-USER-ID") long userId, @RequestBody TradeRequest tradeRequest) {
        if(tradeRequest == null || tradeRequest.getSymbol() == null|| tradeRequest.getSymbol().isBlank()) {
            return ResponseEntity.badRequest().body("Missing or empty 'symbol' parameter");
        }

        if (!Arrays.asList(CURRENT_SUPPORTED_SYMBOLS).contains(tradeRequest.getSymbol())) {
            return ResponseEntity.badRequest().body("Symbol not supported");
        }

        AggregatedPrice latestPrice = aggregatedPriceRepository.findFirstByTickerSymbolOrderByTimeStampDesc(tradeRequest.getSymbol());
        tradingService.sell(userId, tradeRequest, latestPrice);

        return null;
    }
}
