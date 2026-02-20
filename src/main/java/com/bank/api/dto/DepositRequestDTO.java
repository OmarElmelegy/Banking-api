package com.bank.api.dto;

import java.math.BigDecimal;

public class DepositRequestDTO {
    private BigDecimal amount;

    public DepositRequestDTO() {
    }

    public DepositRequestDTO(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
