package com.bank.api.service;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.api.model.Account;
import com.bank.api.model.Transaction;
import com.bank.api.model.User;
import com.bank.api.repository.AccountRepository;
import com.bank.api.repository.TransactionRepository;
import com.bank.api.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

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

    public List<Account> getAllAccountsAdmin() {
        return accountRepository.findAll();
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

        User initiator = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Transaction depositTransaction = new Transaction(
                BigDecimal.valueOf(amount),
                Transaction.TransactionType.DEPOSIT, // Use enum, not String
                LocalDateTime.now(),
                null, // No source account for deposits
                account, // Pass Account object, not ID
                initiator // User object
        );

        transactionRepository.save(depositTransaction); // SAVE THE TRANSACTION!

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

        User initiator = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Transaction withdrawalTransaction = new Transaction(
                BigDecimal.valueOf(amount),
                Transaction.TransactionType.WITHDRAWAL, // Use enum, not String
                LocalDateTime.now(),
                account,
                null,
                initiator);

        transactionRepository.save(withdrawalTransaction);

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

        User initiator = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Transaction transferTransaction = new Transaction(
                BigDecimal.valueOf(amount),
                Transaction.TransactionType.TRANSFER, // Use enum, not String
                LocalDateTime.now(),
                sourceAccount,
                destinationAccount,
                initiator);

        transactionRepository.save(transferTransaction);

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);
    }

    public List<Transaction> getTransationHistory(Long accountId, String username) {
        Account account = accountRepository.findById(accountId)
        .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You do not own this account");
        }

        return transactionRepository.findBySourceAccountIdOrTargetAccountIdOrderByTimeStampDesc(accountId, accountId);
    }
}
