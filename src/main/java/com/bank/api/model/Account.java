package com.bank.api.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity // 1. Tells Spring: "This class is a Database Table"
@Table(name = "accounts")
public class Account {

    @Id // 2. Tells Spring: "This is the Primary Key"
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 3. Tells Spring: "Auto-Increment this ID"
    private Long id;

    private String accountHolderName;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // --- CONSTRUCTORS ---
    // Spring needs an empty constructor to work
    public Account() {
    }

    public Account(String accountHolderName, BigDecimal balance) {
        this.accountHolderName = accountHolderName;
        this.balance = balance;
    }

    // --- GETTERS AND SETTERS ---
    // (You can generate these in your IDE: Right Click -> Source -> Generate
    // Getters/Setters)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}