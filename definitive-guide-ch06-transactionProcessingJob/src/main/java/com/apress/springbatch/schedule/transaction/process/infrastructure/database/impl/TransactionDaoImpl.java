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
        return super.query("select t.account_summary_id, t.timestamp, t.amount " +
                "from transaction t inner join account_summary a on " +
                "a.id = t.account_summary_id " +
                "where a.account_number = ?", new Object[]{accountNumber}, (rs, rowNum) -> {

            Transaction trans = new Transaction();
            trans.setAmount(rs.getDouble("amount"));
            trans.setTimestamp(rs.getDate("timestamp"));

            //System.out.println(trans);

            return trans;
        });
    }
}
