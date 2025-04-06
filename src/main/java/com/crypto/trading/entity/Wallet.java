package com.crypto.trading.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ID;

    private String symbol;

    private double balance;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wallet wallet = (Wallet) o;
        return ID == wallet.ID && Double.compare(balance, wallet.balance) == 0 && Objects.equals(symbol, wallet.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, symbol, balance);
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "ID=" + ID +
                ", symbol='" + symbol + '\'' +
                ", balance=" + balance +
                '}';
    }
}
