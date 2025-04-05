package com.crypto.trading.repository;

import com.crypto.trading.entity.AggregatedPrice;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AggregatedPriceRepository extends JpaRepository<AggregatedPrice, Long> {
    AggregatedPrice findFirstByTickerSymbolOrderByTimeStampDesc(String tickerSymbol);
}
