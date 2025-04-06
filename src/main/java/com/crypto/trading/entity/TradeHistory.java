package com.crypto.trading.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "tradehistory")
public class TradeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long userId;
    private String symbol;
    private double quantity;
    private String side;
    private double price;
    private Instant timeStamp;


    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getSide() {
        return side;
    }

    public double getPrice() {
        return price;
    }

    public Instant getTimeStamp() {
        return timeStamp;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setTimeStamp(Instant timeStamp) {
        this.timeStamp = timeStamp;
    }
}
