package com.apress.springbatch.sample.app.domain;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccountResultSetExtractor implements ResultSetExtractor<List<Account>> {

    private final List<Account> accounts = new ArrayList<>();
    private Account curAccount;

    @Nullable
    @Override
    public List<Account> extractData(ResultSet rs) throws SQLException, DataAccessException {
        while (rs.next()) {
            try {
                if (curAccount == null) {
                    curAccount = new Account(
                            rs.getLong("account_id"),
                            rs.getBigDecimal("balance"),
                            rs.getDate("last_statement_date"));
                } else if (rs.getLong("account_id") != curAccount.getId()) {
                    accounts.add(curAccount);

                    curAccount = new Account(rs.getLong("account_id"),
                            rs.getBigDecimal("balance"),
                            rs.getDate("last_statement_date"));
                }

                if (StringUtils.hasText(rs.getString("description"))) {
                    curAccount.addTransaction(
                            new Transaction(1L, 1L, "foo", BigDecimal.ONE, BigDecimal.TEN, Date.from(Instant.now()))
                            /*new Transaction(rs.getLong("transaction_id"),
                                    rs.getLong("account_id"),
                                    rs.getString("description"),
                                    rs.getBigDecimal("credit"),
                                    rs.getBigDecimal("debit"),
                                    new Date(rs.getTimestamp("timestamp").getTime()))*/
                    );

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (curAccount != null) {
            accounts.add(curAccount);
        }

        return accounts;
    }
}
