package com.bank.api.controller;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bank.api.dto.AccountResponseDTO;
import com.bank.api.dto.DepositRequestDTO;
import com.bank.api.dto.TransactionResponseDTO;
import com.bank.api.dto.TransferRequestDTO;
import com.bank.api.dto.WithdrawRequestDTO;
import com.bank.api.mapper.AccountResponseMapper;
import com.bank.api.model.Account;
import com.bank.api.service.AccountService;
import com.bank.exception.InvalidArgumentException;

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
        Account created = service.createAccount(newAccount, principal);
        AccountResponseDTO responseDTO = AccountResponseMapper.toDto(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<Account> deposit(@PathVariable Long id, @RequestBody DepositRequestDTO request,
            Principal principal) {
        if (request.getAmount() == null) {
            throw new InvalidArgumentException("Amount is required");
        }
    
        Account account = service.deposit(id, request.getAmount(), principal);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<Account> withdraw(@PathVariable Long id, @RequestBody WithdrawRequestDTO request,
            Principal principal) {
        if (request.getAmount() == null) {
            throw new InvalidArgumentException("Amount is required");
        }
        
        Account account = service.withdraw(id, request.getAmount(), principal);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequestDTO request, Principal principal) {
        if (request.getFromId() == null || request.getToId() == null || request.getAmount() == null) {
            throw new InvalidArgumentException("Missing required parameters");
        }
        
        service.transfer(request.getFromId(), request.getToId(), request.getAmount(), principal);
        return ResponseEntity.ok("Transfer successful");
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<Page<TransactionResponseDTO>> getHistory(@PathVariable Long id, Principal principal, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Page<TransactionResponseDTO> transactions = service.getTransationHistory(id, principal.getName(), page, size);

        return ResponseEntity.ok(transactions);
    }
}
