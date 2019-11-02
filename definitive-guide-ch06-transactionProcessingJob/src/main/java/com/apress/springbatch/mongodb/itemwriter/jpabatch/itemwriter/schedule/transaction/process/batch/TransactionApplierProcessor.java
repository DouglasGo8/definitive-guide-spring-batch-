package com.apress.springbatch.mongodb.itemwriter.jpabatch.itemwriter.schedule.transaction.process.batch;

import com.apress.springbatch.mongodb.itemwriter.jpabatch.itemwriter.schedule.transaction.process.domain.AccountSummary;
import com.apress.springbatch.mongodb.itemwriter.jpabatch.itemwriter.schedule.transaction.process.infrastructure.database.TransactionDao;
import lombok.AllArgsConstructor;
import org.springframework.batch.item.ItemProcessor;

/**
 * @author dbatista
 */
@AllArgsConstructor
public class TransactionApplierProcessor implements ItemProcessor<AccountSummary, AccountSummary> {

    private TransactionDao transactionDao;

    @Override
    public AccountSummary process(AccountSummary accountSummary) throws Exception {

        transactionDao.getTransactionsByAccountNumber(accountSummary.getAccountNumber())
                .forEach(t -> {
                    accountSummary.setCurrentBalance(t.getAmount());
                });

        return  accountSummary;
    }
}
