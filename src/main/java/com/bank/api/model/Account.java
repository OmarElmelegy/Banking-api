package com.bank.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity // 1. Tells Spring: "This class is a Database Table"
public class Account {

    @Id // 2. Tells Spring: "This is the Primary Key"
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 3. Tells Spring: "Auto-Increment this ID"
    private Long id;

    private String accountHolderName;
    private double balance;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // --- CONSTRUCTORS ---
    // Spring needs an empty constructor to work
    public Account() {
    }

    public Account(String accountHolderName, double balance) {
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

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}