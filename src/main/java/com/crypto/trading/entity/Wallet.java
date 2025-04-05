package com.crypto.trading.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.Objects;

@Entity
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ID;

    private String currency;

    private double balance;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Constructors, Getters, Setters
    public Wallet(String currency, double balance) {
        this.currency = currency;
        this.balance = balance;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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
        return ID == wallet.ID && Double.compare(balance, wallet.balance) == 0 && Objects.equals(currency, wallet.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, currency, balance);
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "ID=" + ID +
                ", currency='" + currency + '\'' +
                ", balance=" + balance +
                '}';
    }
}
