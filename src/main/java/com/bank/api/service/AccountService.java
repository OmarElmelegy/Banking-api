package com.bank.api.service;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bank.api.dto.AccountRequestDTO;
import com.bank.api.dto.AccountResponseDTO;
import com.bank.api.dto.TransactionResponseDTO;
import com.bank.api.mapper.AccountResponseMapper;
import com.bank.api.mapper.TransactionMapper;
import com.bank.api.model.Account;
import com.bank.api.model.Account.AccountType;
import com.bank.api.model.Transaction;
import com.bank.api.model.User;
import com.bank.api.repository.AccountRepository;
import com.bank.api.repository.TransactionRepository;
import com.bank.api.repository.UserRepository;
import com.bank.exception.InsufficientFundsException;
import com.bank.exception.InvalidArgumentException;
import com.bank.exception.ResourceNotFoundException;
import com.bank.exception.UnauthorizedActionException;

import jakarta.transaction.Transactional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public AccountResponseDTO createAccount(AccountRequestDTO accountRequestDTO, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Validate account data
        if (accountRequestDTO.getOwnerName() == null || accountRequestDTO.getOwnerName().isBlank()) {
            throw new InvalidArgumentException("Account holder name is required");
        }

        if (accountRequestDTO.getAccountType() == null) {
            throw new InvalidArgumentException("Account Type is required");
        }

        if (accountRequestDTO.getInitialBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidArgumentException("Initial balance cannot be negative");
        }

        Account account = new Account(
                accountRequestDTO.getOwnerName(),
                accountRequestDTO.getInitialBalance(),
                user,
                accountRequestDTO.getAccountType());

        if (account.getAccountType() == AccountType.SAVINGS) {
            // Set a default interest rate for savings accounts (e.g., 4.5%)
            account.setInterestRate(new BigDecimal("0.045"));
        } else {
            // Checking accounts get 0% interest
            account.setInterestRate(BigDecimal.ZERO);
        }

        Account savedAccount = accountRepository.save(account);
        AccountResponseDTO accountDTO = AccountResponseMapper.toDto(savedAccount);
        return accountDTO;
    }

    public List<Account> getAllAccounts(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return accountRepository.findByUserId(user.getId());
    }

    public List<Account> getAllAccountsAdmin() {
        return accountRepository.findAll();
    }

    @Transactional
    public AccountResponseDTO deposit(Long id, BigDecimal amount, Principal principal) {
        // Find the account
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (!accountOptional.isPresent()) {
            throw new ResourceNotFoundException("Account not found");
        }

        Account account = accountOptional.get();
        String currentUsername = principal.getName();

        if (!account.getUser().getUsername().equals(currentUsername)) {
            throw new UnauthorizedActionException("You do not own this account");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidArgumentException("Invalid amount");
        }

        // Some math
        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);

        User initiator = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Transaction depositTransaction = new Transaction(
                amount,
                Transaction.TransactionType.DEPOSIT, // Use enum, not String
                LocalDateTime.now(),
                null, // No source account for deposits
                account, // Pass Account object, not ID
                null,
                newBalance,
                initiator // User object
        );

        transactionRepository.save(depositTransaction); // SAVE THE TRANSACTION!

        // Save and return
        Account savedAccount = accountRepository.save(account);
        AccountResponseDTO accountDTO = AccountResponseMapper.toDto(savedAccount);
        return accountDTO;
    }

    @Transactional
    public AccountResponseDTO withdraw(Long id, BigDecimal amount, Principal principal) {
        // Find the account
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (!accountOptional.isPresent()) {
            throw new ResourceNotFoundException("Account not found");
        }

        Account account = accountOptional.get();
        String currentUsername = principal.getName();

        if (!account.getUser().getUsername().equals(currentUsername)) {
            throw new UnauthorizedActionException("You do not own this account");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidArgumentException("Invalid amount");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);

        User initiator = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Transaction withdrawalTransaction = new Transaction(
                amount,
                Transaction.TransactionType.WITHDRAWAL, // Use enum, not String
                LocalDateTime.now(),
                account,
                null,
                newBalance,
                null,
                initiator);

        transactionRepository.save(withdrawalTransaction);
        Account savedAccount = accountRepository.save(account);
        AccountResponseDTO accountDTO = AccountResponseMapper.toDto(savedAccount);
        return accountDTO;
    }

    @Transactional
    public void transfer(Long fromId, Long toId, BigDecimal amount, Principal principal) {
        Optional<Account> sourceAccountOptional = accountRepository.findById(fromId);
        if (!sourceAccountOptional.isPresent()) {
            throw new ResourceNotFoundException("Sending Account not found");
        }

        Account sourceAccount = sourceAccountOptional.get();
        String currentUsername = principal.getName();

        if (!sourceAccount.getUser().getUsername().equals(currentUsername)) {
            throw new UnauthorizedActionException("You do not own this account");
        }

        Optional<Account> destinationAccountOptional = accountRepository.findById(toId);
        if (!destinationAccountOptional.isPresent()) {
            throw new ResourceNotFoundException("Receiving Account not found");
        }

        Account destinationAccount = destinationAccountOptional.get();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidArgumentException("Invalid amount");
        }

        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Source Account does not have enough balance");
        }

        if (fromId.equals(toId)) {
            throw new InvalidArgumentException(
                    "Invalid destination Id, source and destination accounts cannot be the same");
        }

        BigDecimal sourceNewBalance = sourceAccount.getBalance().subtract(amount);
        sourceAccount.setBalance(sourceNewBalance);

        BigDecimal destinationNewBalance = destinationAccount.getBalance().add(amount);
        destinationAccount.setBalance(destinationNewBalance);

        User initiator = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Transaction transferTransaction = new Transaction(
                amount,
                Transaction.TransactionType.TRANSFER, // Use enum, not String
                LocalDateTime.now(),
                sourceAccount,
                destinationAccount,
                sourceNewBalance,
                destinationNewBalance,
                initiator);

        transactionRepository.save(transferTransaction);

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);
    }

    public Page<TransactionResponseDTO> getTransationHistory(Long accountId, String username, int page, int size) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!account.getUser().getUsername().equals(username)) {
            throw new UnauthorizedActionException("You do not own this account");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<Transaction> transactions = transactionRepository
                .findAccountTransactions(accountId, pageable);

        return transactions.map(TransactionMapper::toDTO);
    }
}
