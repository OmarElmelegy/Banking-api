package com.bank.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bank.api.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t WHERE t.sourceAccount.id = :accountId OR t.targetAccount.id = :accountId ORDER BY t.timestamp DESC")
    List<Transaction> findBySourceAccountIdOrTargetAccountIdOrderByTimestampDesc(@Param("accountId") Long accountId);
}
