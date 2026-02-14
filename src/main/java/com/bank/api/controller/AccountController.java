package com.bank.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.api.model.Account;
import com.bank.api.service.AccountService;

@RestController // 1. Tells Spring: "This class handles HTTP requests"
@RequestMapping("/api/accounts") // 2. The base URL: http://localhost:8080/api/accounts
public class AccountController {

    @Autowired // 3. Dependency Injection: Spring gives us the Repository automatically
    private AccountService service;

    // POST /api/accounts/{id}/deposit

    @PostMapping("/{id}/deposit")
    public Account deposit(@PathVariable Long id, @RequestBody java.util.Map<String, Double> request) {
        double amount = request.get("amount");
        return service.deposit(id, amount);
    }

    // GET /api/accounts
    // Returns a list of all accounts

    @GetMapping
    public List<Account> getAllAccounts() {
        return service.getAllAccounts();
    }

    // POST /api/accounts
    // Creates a new account. Expects JSON data in the request body
    @PostMapping
    public Account createAccount(@RequestBody Account newAccount) {
        return service.createAccount(newAccount);
    }

    // POST  /api/accounts/{id}/withdraw
    @PostMapping("/{id}/withdraw")
    public Account withdraw(@PathVariable Long id, @RequestBody java.util.Map<String, Double> request) {
        double amount = request.get("amount");
        return service.withdraw(id, amount);
    }
}
