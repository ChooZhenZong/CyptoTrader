package com.crypto.trading.repository;

import com.crypto.trading.entity.TradeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeHistoryRespository extends JpaRepository<TradeHistory, Long> {
    List<TradeHistory> findByUserId(Long userId);
}
