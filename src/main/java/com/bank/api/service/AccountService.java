package com.bank.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.api.model.Account;
import com.bank.api.repository.AccountRepository;

@Service
public class AccountService {

    @Autowired
    private AccountRepository repository;

    public Account deposit(Long id, double amount) {
        // Find the account
        Optional<Account> accountOptional = repository.findById(id);
        if (!accountOptional.isPresent()) {
            throw new RuntimeException("Account not found");
        }

        Account account = accountOptional.get();

        if (amount <= 0) {
            throw new RuntimeException("Invalid amount");
        }
        
        // Some math
        double newBalance = account.getBalance() + amount;
        account.setBalance(newBalance);

        // Save and return
        return repository.save(account);
    }


    public Account createAccount(Account account) {
        return repository.save(account);
    }

    public List<Account> getAllAccounts() {
        return repository.findAll();
    }

    public Account withdraw(Long  id, double amount) {
        // Find the account
        Optional<Account> accountOptional = repository.findById(id);
        if (!accountOptional.isPresent()) {
            throw new RuntimeException("Account not found");
        }

        Account account = accountOptional.get();

        if (amount <= 0) {
            throw new RuntimeException("Invalid amount");
        }

        if (account.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds");
        }

        double newBalance = account.getBalance() - amount;
        account.setBalance(newBalance);

        return repository.save(account);
    }
}
