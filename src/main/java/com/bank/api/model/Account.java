package com.bank.api.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    private String ownerName;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public enum AccountType {
        CHECKING,
        SAVINGS
    }

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private BigDecimal interestRate = BigDecimal.ZERO;

    // --- CONSTRUCTORS ---
    // Spring needs an empty constructor to work
    public Account() {
    }

    public Account(String ownerName, BigDecimal balance) {
        this.ownerName = ownerName;
        this.balance = balance;
    }

    public Account(String ownerName, BigDecimal balance, User user, AccountType accountType) {
        this.ownerName = ownerName;
        this.balance = balance;
        this.user = user;
        this.accountType = accountType;
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

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
}