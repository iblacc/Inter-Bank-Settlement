package com.queuepay.ibs.repositories;

import com.queuepay.ibs.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
