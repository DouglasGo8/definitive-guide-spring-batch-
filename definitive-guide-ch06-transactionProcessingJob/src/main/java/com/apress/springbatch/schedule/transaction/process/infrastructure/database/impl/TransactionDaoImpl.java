package com.apress.springbatch.schedule.transaction.process.infrastructure.database.impl;

import com.apress.springbatch.schedule.transaction.process.domain.Transaction;
import com.apress.springbatch.schedule.transaction.process.infrastructure.database.TransactionDao;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author dbatista
 */
public class TransactionDaoImpl extends JdbcTemplate implements TransactionDao {
    
    public TransactionDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Transaction> getTransactionsByAccountNumber(String accountNumber) {
        return null;
    }
}
