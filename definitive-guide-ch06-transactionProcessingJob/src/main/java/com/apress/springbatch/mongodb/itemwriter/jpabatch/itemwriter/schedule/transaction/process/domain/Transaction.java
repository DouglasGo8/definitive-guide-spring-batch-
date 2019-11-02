package com.apress.springbatch.mongodb.itemwriter.jpabatch.itemwriter.schedule.transaction.process.domain;


import lombok.Data;

import java.util.Date;

/**
 * @author dbatista
 */
@Data
public class Transaction {

    private double amount;
    private Date timestamp;
    private String accountNumber;

}
