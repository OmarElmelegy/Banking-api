package com.bank.api.service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.api.model.Account;
import com.bank.api.model.User;
import com.bank.api.repository.AccountRepository;
import com.bank.api.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    public Account createAccount(Account account, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate account data
        if (account.getAccountHolderName() == null || account.getAccountHolderName().isBlank()) {
            throw new RuntimeException("Account holder name is required");
        }

        if (account.getBalance() < 0) {
            throw new RuntimeException("Initial balance cannot be negative");
        }

        account.setUser(user);
        return accountRepository.save(account);
    }

    public List<Account> getAllAccounts(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return accountRepository.findByUserId(user.getId());
    }

    public Account deposit(Long id, double amount, Principal principal) {
        // Find the account
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (!accountOptional.isPresent()) {
            throw new RuntimeException("Account not found");
        }

        Account account = accountOptional.get();
        String currentUsername = principal.getName();

        if (!account.getUser().getUsername().equals(currentUsername)) {
            throw new RuntimeException("You do not own this account");
        }

        if (amount <= 0) {
            throw new RuntimeException("Invalid amount");
        }

        // Some math
        double newBalance = account.getBalance() + amount;
        account.setBalance(newBalance);

        // Save and return
        return accountRepository.save(account);
    }

    public Account withdraw(Long id, double amount, Principal principal) {
        // Find the account
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (!accountOptional.isPresent()) {
            throw new RuntimeException("Account not found");
        }

        Account account = accountOptional.get();
        String currentUsername = principal.getName();

        if (!account.getUser().getUsername().equals(currentUsername)) {
            throw new RuntimeException("You do not own this account");
        }

        if (amount <= 0) {
            throw new RuntimeException("Invalid amount");
        }

        if (account.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds");
        }

        double newBalance = account.getBalance() - amount;
        account.setBalance(newBalance);

        return accountRepository.save(account);
    }

    @Transactional
    public void transfer(Long fromId, Long toId, double amount, Principal principal) {
        Optional<Account> sourceAccountOptional = accountRepository.findById(fromId);
        if (!sourceAccountOptional.isPresent()) {
            throw new RuntimeException("Sending Account not found");
        }

        Account sourceAccount = sourceAccountOptional.get();
        String currentUsername = principal.getName();

        if (!sourceAccount.getUser().getUsername().equals(currentUsername)) {
            throw new RuntimeException("You do not own this account");
        }

        Optional<Account> destinationAccountOptional = accountRepository.findById(toId);
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

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);
    }
}
