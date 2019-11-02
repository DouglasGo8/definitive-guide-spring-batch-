package com.apress.springbatch.jpabatch.itemwriter.schedule.transaction.process.infrastructure.database;

import com.apress.springbatch.jpabatch.itemwriter.schedule.transaction.process.domain.Transaction;

import java.util.List;

/**
 * @author dbatista
 */
public interface TransactionDao {
    List<Transaction> getTransactionsByAccountNumber(String accountNumber);
}
