package com.bank.api.dto;

import java.math.BigDecimal;

import com.bank.api.model.Account.AccountType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AccountRequestDTO {
    @NotBlank(message = "Owner name is required")
    private String ownerName;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    @NotNull(message = "Initial balance is required")
    @DecimalMin(value = "0.0", message = "Balance must be non-negative")
    private BigDecimal initialBalance;

    public AccountRequestDTO() {
    }

    public AccountRequestDTO(String ownerName, AccountType accountType, BigDecimal initialBalance) {
        this.ownerName = ownerName;
        this.accountType = accountType;
        this.initialBalance = initialBalance;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }
}
