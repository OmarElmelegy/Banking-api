package com.bank.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bank.api.model.Transaction;

public class TransactionResponseDTO {
    private Long id;
    private BigDecimal amount;
    private Transaction.TransactionType type;
    private LocalDateTime timestamp;

    // The snapshot fields
    private BigDecimal sourceHistoricalBalance;
    private BigDecimal targetHistoricalBalance;

    // The nested Objects
    private AccountSummaryDTO sourceAccount;
    private AccountSummaryDTO targetAccount;
    private UserSummaryDTO initiator;

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

    public Transaction.TransactionType getType() {
        return type;
    }

    public void setType(Transaction.TransactionType type) {
        this.type = type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getSourceHistoricalBalance() {
        return sourceHistoricalBalance;
    }

    public void setSourceHistoricalBalance(BigDecimal sourceHistoricalBalance) {
        this.sourceHistoricalBalance = sourceHistoricalBalance;
    }

    public BigDecimal getTargetHistoricalBalance() {
        return targetHistoricalBalance;
    }

    public void setTargetHistoricalBalance(BigDecimal targetHistoricalBalance) {
        this.targetHistoricalBalance = targetHistoricalBalance;
    }

    public AccountSummaryDTO getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(AccountSummaryDTO sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    public AccountSummaryDTO getTargetAccount() {
        return targetAccount;
    }

    public void setTargetAccount(AccountSummaryDTO targetAccount) {
        this.targetAccount = targetAccount;
    }

    public UserSummaryDTO getInitiator() {
        return initiator;
    }

    public void setInitiator(UserSummaryDTO initiator) {
        this.initiator = initiator;
    }
}
