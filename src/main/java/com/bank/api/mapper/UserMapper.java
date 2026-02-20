package com.bank.api.mapper;

import com.bank.api.dto.UserSummaryDTO;
import com.bank.api.model.User;

public class UserMapper {
    
    public static UserSummaryDTO toSummaryDTO(User user) {
        if (user == null) return null;
        return new UserSummaryDTO(user.getId(), user.getUsername());
    }
}