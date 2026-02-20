package com.bank.api.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.api.dto.AccountResponseDTO;
import com.bank.api.dto.DepositRequestDTO;
import com.bank.api.dto.TransactionResponseDTO;
import com.bank.api.dto.TransferRequestDTO;
import com.bank.api.dto.WithdrawRequestDTO;
import com.bank.api.mapper.AccountResponseMapper;
import com.bank.api.model.Account;
import com.bank.api.service.AccountService;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService service;

    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> getAllAccounts(Principal principal) {
        List<Account> rawAccounts = service.getAllAccounts(principal.getName());

        List<AccountResponseDTO> safeAccounts = rawAccounts.stream()
                .map(AccountResponseMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(safeAccounts);
    }

    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@RequestBody Account newAccount, Principal principal) {
        try {
            Account created = service.createAccount(newAccount, principal);
            AccountResponseDTO responseDTO = AccountResponseMapper.toDto(created);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<?> deposit(@PathVariable Long id, @RequestBody DepositRequestDTO request,
            Principal principal) {
        try {
            if (request.getAmount() == null) {
                return ResponseEntity.badRequest().body("Amount is required");
            }
            if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("Invalid amount");
            }
            Account account = service.deposit(id, request.getAmount(), principal);
            return ResponseEntity.ok(account);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Account not found or operation failed");
        }
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable Long id, @RequestBody WithdrawRequestDTO request,
            Principal principal) {
        try {
            if (request.getAmount() == null) {
                return ResponseEntity.badRequest().body("Amount is required");
            }
            if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("Invalid amount");
            }
            Account account = service.withdraw(id, request.getAmount(), principal);
            return ResponseEntity.ok(account);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Account not found or insufficient funds");
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequestDTO request, Principal principal) {
        try {
            // Validate input
            if (request.getFromId() == null || request.getToId() == null || request.getAmount() == null) {
                return ResponseEntity.badRequest().body("Missing required parameters");
            }

            if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("Amount must be positive");
            }

            if (request.getFromId().equals(request.getToId())) {
                return ResponseEntity.badRequest()
                        .body("Invalid destination Id, source and destination accounts cannot be the same");
            }
            service.transfer(request.getFromId(), request.getToId(), request.getAmount(), principal);
            return ResponseEntity.ok("Transfer successful");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Transfer failed: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionResponseDTO>> getHistory(@PathVariable Long id, Principal principal) {
        List<TransactionResponseDTO> history = service.getTransationHistory(id, principal.getName());

        return ResponseEntity.ok(history);
    }
}
