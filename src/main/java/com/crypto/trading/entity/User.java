package com.crypto.trading.entity;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "`user`")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ID;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Wallet> wallets;

    private String userName;

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public List<Wallet> getWallets() {
        return wallets;
    }

    public void setWallets(List<Wallet> wallet) {
        this.wallets = wallet;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return ID == user.ID && Objects.equals(wallets, user.wallets) && Objects.equals(userName, user.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, wallets, userName);
    }

    @Override
    public String toString() {
        return "User{" +
                "ID=" + ID +
                ", wallet=" + wallets +
                ", userName='" + userName + '\'' +
                '}';
    }

    public Wallet getWalletBySymbol(String symbol) {
        return wallets.stream()
                .filter(w -> symbol.equalsIgnoreCase(w.getSymbol()))
                .findFirst()
                .orElse(null);
    }
}
