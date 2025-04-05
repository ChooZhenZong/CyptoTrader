package com.crypto.trading.controller;

import com.crypto.trading.entity.AggregatedPrice;
import com.crypto.trading.repository.AggregatedPriceRepository;
import com.crypto.trading.service.TradingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/v1/api/trade")
public class TradingController {

    private TradingService tradingService;

    private AggregatedPriceRepository aggregatedPriceRepository;

    private static final String[] CURRENT_SUPPORTED_SYMBOLS = {"ETHUSDT", "BTCUSDT"};

    public TradingController(TradingService tradingService, AggregatedPriceRepository aggregatedPriceRepository) {
        this.tradingService = tradingService;
        this.aggregatedPriceRepository = aggregatedPriceRepository;
    }

    @GetMapping("/getAggregatedPrices")
    public ResponseEntity<AggregatedPrice> getLatestAggregatedPrices(@RequestParam String symbol) {
        if (!Arrays.asList(CURRENT_SUPPORTED_SYMBOLS).contains(symbol)) {
            return ResponseEntity.badRequest().build();
        }

        AggregatedPrice latestPrice = aggregatedPriceRepository.findFirstByTickerSymbolOrderByTimeStampDesc(symbol);
        return latestPrice != null ? ResponseEntity.ok(latestPrice) : ResponseEntity.notFound().build();
    }
}
