package com.apress.springbatch.schedule.transaction.process.domain;


import lombok.Data;

/**
 * @author dbatista
 */
@Data
public class AccountSummary {
    private int id;
    private String accountNumber;
    private Double currentBalance;
}
