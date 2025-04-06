package com.crypto.trading.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
public class AggregatedPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String exchange;
    private String tickerSymbol;
    private double bidPrice;
    private double askPrice;
    private Instant timeStamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }

    public double getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(double bidPrice) {
        this.bidPrice = bidPrice;
    }

    public double getAskPrice() {
        return askPrice;
    }

    public void setAskPrice(double askPrice) {
        this.askPrice = askPrice;
    }

    public Instant getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Instant timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AggregatedPrice that = (AggregatedPrice) o;
        return Objects.equals(id, that.id) && Objects.equals(exchange, that.exchange) && Objects.equals(tickerSymbol, that.tickerSymbol) && Objects.equals(bidPrice, that.bidPrice) && Objects.equals(askPrice, that.askPrice) && Objects.equals(timeStamp, that.timeStamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, exchange, tickerSymbol, bidPrice, askPrice, timeStamp);
    }

    @Override
    public String toString() {
        return "AggregatedPrice{" +
                "id=" + id +
                ", exchange='" + exchange + '\'' +
                ", tickerSymbol='" + tickerSymbol + '\'' +
                ", bidPrice=" + bidPrice +
                ", askPrice=" + askPrice +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
