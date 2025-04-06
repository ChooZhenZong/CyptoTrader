package com.crypto.trading.entity;

public class TradeRequest {
    private String symbol;
    private double quantity;
    private String side;

    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getQuantity() {
        return quantity;
    }
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getSide() {
        return side;
    }
    public void setSide(String side) {
        this.side = side;
    }
}
