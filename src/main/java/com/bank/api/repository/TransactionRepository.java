package com.bank.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.api.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>{
    List<Transaction> findBySourceAccountIdOrTargetAccountIdOrderByTimeStampDesc(Long sourceId, Long targetId);
}
