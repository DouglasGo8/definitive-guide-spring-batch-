package com.apress.springbatch.multithreaded.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class Transaction {

    private String accountId;
    private double amount;
    private Date timestamp;
}
