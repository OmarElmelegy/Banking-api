package com.bank.api.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.bank.api.model.Account;
import com.bank.api.model.Account.AccountType;
import com.bank.api.model.Transaction;
import com.bank.api.repository.AccountRepository;
import com.bank.api.repository.TransactionRepository;

@Service
public class InterestJobService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransactionRepository transactionRepository;

    // @Scheduled(cron = "0 0 0 1 * ?")
    @Scheduled(fixedRate = 30000)
    public void calculateAndPayInterest() {

        List<Account> savingsAccounts = accountRepository.findByaccountType(AccountType.SAVINGS);

        for (Account account : savingsAccounts) {
            BigDecimal annualInterest = account.getBalance().multiply(account.getInterestRate());

            BigDecimal monthlyInterest = annualInterest.divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);

            if (monthlyInterest.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal newBalance = account.getBalance().add(monthlyInterest);
                account.setBalance(newBalance);

                Transaction interestTx = new Transaction(
                        monthlyInterest,
                        Transaction.TransactionType.INTEREST,
                        LocalDateTime.now(),
                        null, // No source account
                        account, // The account getting paid
                        null,
                        newBalance,
                        null // No initiator, the System did this
                );

                transactionRepository.save(interestTx);
                accountRepository.save(account);

                System.out.println("Paid " + monthlyInterest + " interest to Account ID: " + account.getId());
            }
        }
        System.out.println("Monthly interest calculation complete.");
    }
}
