package com.bank.api.dto;

import java.math.BigDecimal;

public class WithdrawRequestDTO {
    private BigDecimal amount;

    public WithdrawRequestDTO() {
    }

    public WithdrawRequestDTO(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
