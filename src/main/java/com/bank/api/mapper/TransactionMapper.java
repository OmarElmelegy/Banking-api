package com.bank.api.mapper;

import com.bank.api.dto.AccountSummaryDTO;
import com.bank.api.dto.TransactionResponseDTO;
import com.bank.api.model.Account;
import com.bank.api.model.Transaction;


public class TransactionMapper {

    // MAIN MAPPER METHOD

    public static TransactionResponseDTO toDTO(Transaction entity) {
        if (entity == null) {
            return null;
        }

        TransactionResponseDTO dto = new TransactionResponseDTO();

        // 1. Map Simple Fields
        dto.setId(entity.getId());
        dto.setAmount(entity.getAmount());
        dto.setType(entity.getType()); // Assuming Enum
        dto.setTimestamp(entity.getTimestamp());

        // 2. Map Snapshot Fields (The critical financial data)
        dto.setSourceHistoricalBalance(entity.getSourceBalanceAfter());
        dto.setTargetHistoricalBalance(entity.getTargetBalanceAfter());

        // 3. Map Complex/Nested Objects using helper methods
        dto.setInitiator(UserMapper.toSummaryDTO(entity.getInitiator()));
        dto.setSourceAccount(mapAccountSummary(entity.getSourceAccount()));
        dto.setTargetAccount(mapAccountSummary(entity.getTargetAccount()));

        return dto;
    }

    // HELPERS

    private static AccountSummaryDTO mapAccountSummary(Account account) {
        if (account == null) return null;
        
        AccountSummaryDTO dto = new AccountSummaryDTO();
        dto.setId(account.getId());
        dto.setAccountHolderName(account.getAccountHolderName());
        
        // Reuse the User mapper here to handle nesting
        dto.setUser(UserMapper.toSummaryDTO(account.getUser()));
        
        return dto;
    }
}
