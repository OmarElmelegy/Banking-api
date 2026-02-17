package com.bank.api.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.api.model.Account;
import com.bank.api.service.AccountService;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin")
public class UserController {

    private final AccountService service;

    public UserController(AccountService service) {
        this.service = service;
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAllAccounts(Principal principal) {
        return ResponseEntity.ok(service.getAllAccounts(principal.getName()));
    }
}
