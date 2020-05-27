package com.apress.springbatch.partitioner.master.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
public class Transaction {
    private String account;
    private Date timestamp;
    private BigDecimal amount;
}
