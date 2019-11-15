package com.apress.springbatch.jpabatch.itemwriter.multiple.format.domain;

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

    private Double amount;

    private Date transactionDate;

    private String accountNumber;

    @Override
    public String toString() {
        return "Transaction{" +
                "accountNumber='" + accountNumber + '\'' +
                ", transactionDate=" + new SimpleDateFormat("MM/dd/yyyy").format(this.transactionDate) +
                ", amount=" + amount +
                '}';
    }


}
