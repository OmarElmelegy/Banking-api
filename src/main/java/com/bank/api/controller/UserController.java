package com.bank.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.api.model.Account;
import com.bank.api.model.User;
import com.bank.api.service.AccountService;
import com.bank.api.service.UserService;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin")
public class UserController {

    @Autowired
    AccountService accountService;

    @Autowired
    UserService userService;

    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccountsAdmin());
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
