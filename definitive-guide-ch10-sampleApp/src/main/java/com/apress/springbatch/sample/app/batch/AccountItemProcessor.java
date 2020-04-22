package com.apress.springbatch.sample.app.batch;

import com.apress.springbatch.sample.app.domain.AccountResultSetExtractor;
import com.apress.springbatch.sample.app.domain.Statement;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class AccountItemProcessor implements ItemProcessor<Statement, Statement> {

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public AccountItemProcessor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Statement process(Statement statement) throws Exception {

        statement.setAccounts(this.jdbcTemplate.query("select a.account_id," +
                        "       a.balance," +
                        "       a.last_statement_date," +
                        "       t.transaction_id," +
                        "       t.description," +
                        "       t.credit," +
                        "       t.debit," +
                        "       t.timestamp " +
                        "from tbl_account a left join " +
                        "    tbl_transaction t on a.account_id = t.account_account_id " +
                        "where a.account_id in " +
                        "	(select account_account_id " +
                        "	from tbl_customer_account " +
                        "	where customer_customer_id = ?) " +
                        "order by t.timestamp",
                new Object[] {statement.getCustomer().getId()},
                new AccountResultSetExtractor()));

        return statement;
    }
}
