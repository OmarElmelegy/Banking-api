package com.bank.api.mapper;

import com.bank.api.dto.AccountResponseDTO;
import com.bank.api.model.Account;

public class AccountResponseMapper {
    public static AccountResponseDTO toDto(Account entity) {

        if (entity == null) {
            return null;
        }

        AccountResponseDTO dto = new AccountResponseDTO();

        dto.setId(entity.getId());
        dto.setAccountHolderName(entity.getAccountHolderName());
        dto.setBalance(entity.getBalance());
        dto.setUser(UserMapper.toSummaryDTO(entity.getUser()));

        return dto;
    }
}
