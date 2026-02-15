package com.bank.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.api.model.Account;
import com.bank.api.repository.AccountRepository;

import jakarta.transaction.Transactional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository repository;

    public Account createAccount(Account account) {
        return repository.save(account);
    }

    public List<Account> getAllAccounts() {
        return repository.findAll();
    }

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

    public Account withdraw(Long id, double amount) {
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

    @Transactional
    public void transfer(Long fromId, Long toId, double amount) {
        Optional<Account> sourceAccountOptional = repository.findById(fromId);
        if (!sourceAccountOptional.isPresent()) {
            throw new RuntimeException("Sending Account not found");
        }

        Account sourceAccount = sourceAccountOptional.get();

        Optional<Account> destinationAccountOptional = repository.findById(toId);
        if (!destinationAccountOptional.isPresent()) {
            throw new RuntimeException("Receiving Account not found");
        }

        Account destinationAccount = destinationAccountOptional.get();

        if (amount <= 0) {
            throw new RuntimeException("Invalid amount");
        }

        if (sourceAccount.getBalance() < amount) {
            throw new RuntimeException("Source Account does not have enough balance");
        }

        double newBalance = sourceAccount.getBalance() - amount;
        sourceAccount.setBalance(newBalance);

        double oldBalance = destinationAccount.getBalance();
        destinationAccount.setBalance(oldBalance + amount);

        repository.save(sourceAccount);
        repository.save(destinationAccount);
    }
}
