package com.apress.springbatch.sample.app.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Statement {

    private final Customer customer;

    private List<Account> accounts = new ArrayList<>();

    public Statement(Customer customer) {
        this.customer = customer;
    }

    public Statement(Customer customer, List<Account> accounts) {
        this.customer = customer;
        this.accounts.addAll(accounts);
    }
}
