package com.apress.springbatch.jpabatch.itemwriter.multifile.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author dbatista
 */
@Data
@NoArgsConstructor
public class Transaction {

    private String accountNumber;
    private Date transactionDate;

    private Double amount;

    @Override
    public String toString() {
        return "Transaction{" +
                "accountNumber='" + accountNumber + '\'' +
                ", transactionDate=" + new SimpleDateFormat("MM/dd/yyyy").format(transactionDate) +
                ", amount=" + amount +
                '}';
    }
}
