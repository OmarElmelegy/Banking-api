package com.bank.api.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue()
    private Long id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    public enum TransactionType {
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER
    }

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "source_account_id")
    private Account sourceAccount;

    @ManyToOne
    @JoinColumn(name = "target_account_id")
    private Account targetAccount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal sourceBalanceAfter;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal targetBalanceAfter;

    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    // Constructors
    public Transaction() {
    }

    public Transaction(BigDecimal amount, TransactionType type, LocalDateTime timestamp,
            Account sourceAccount, Account targetAccount, BigDecimal sourceBalanceAfter, BigDecimal targetBalanceAfter,
            User initiator) {
        this.amount = amount;
        this.type = type;
        this.timestamp = timestamp;
        this.sourceAccount = sourceAccount;
        this.targetAccount = targetAccount;
        this.sourceBalanceAfter = sourceBalanceAfter;
        this.targetBalanceAfter = targetBalanceAfter;
        this.initiator = initiator;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Account getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(Account sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    public Account getTargetAccount() {
        return targetAccount;
    }

    public void setTargetAccount(Account targetAccount) {
        this.targetAccount = targetAccount;
    }

    public BigDecimal getSourceBalanceAfter() {
        return sourceBalanceAfter;
    }

    public void setSourceBalanceAfter(BigDecimal sourceBalanceAfter) {
        this.sourceBalanceAfter = sourceBalanceAfter;
    }

    public BigDecimal getTargetBalanceAfter() {
        return targetBalanceAfter;
    }

    public void setTargetBalanceAfter(BigDecimal targetBalanceAfter) {
        this.targetBalanceAfter = targetBalanceAfter;
    }

    public User getInitiator() {
        return initiator;
    }

    public void setInitiator(User initiator) {
        this.initiator = initiator;
    }
}
