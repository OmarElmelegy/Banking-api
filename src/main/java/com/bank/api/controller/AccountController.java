package com.bank.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.api.model.Account;
import com.bank.api.service.AccountService;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService service;

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(service.getAllAccounts());
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account newAccount) {
        try {
            Account created = service.createAccount(newAccount);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<?> deposit(@PathVariable Long id, @RequestBody java.util.Map<String, Double> request) {
        try {
            Double amount = request.get("amount");
            if (amount == null || amount <= 0) {
                return ResponseEntity.badRequest().body("Invalid amount");
            }
            Account account = service.deposit(id, amount);
            return ResponseEntity.ok(account);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Account not found or operation failed");
        }
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable Long id, @RequestBody java.util.Map<String, Double> request) {
        try {
            Double amount = request.get("amount");
            if (amount == null || amount <= 0) {
                return ResponseEntity.badRequest().body("Invalid amount");
            }
            Account account = service.withdraw(id, amount);
            return ResponseEntity.ok(account);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Account not found or insufficient funds");
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody java.util.Map<String, Object> request) {
        try {
            // Validate input
            if (!request.containsKey("fromId") || !request.containsKey("toId") || !request.containsKey("amount")) {
                return ResponseEntity.badRequest().body("Missing required parameters");
            }

            Long sourceId = Long.valueOf(request.get("fromId").toString());
            Long distId = Long.valueOf(request.get("toId").toString());
            double amount = Double.valueOf(request.get("amount").toString());

            if (amount <= 0) {
                return ResponseEntity.badRequest().body("Amount must be positive");
            }

            if (sourceId == distId) {
                return ResponseEntity.badRequest()
                        .body("Invalid destination Id, source and destination accounts cannot be the same");
            }
            service.transfer(sourceId, distId, amount);
            return ResponseEntity.ok("Transfer successful");

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid parameter format");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Transfer failed: " + e.getMessage());
        }
    }
}
