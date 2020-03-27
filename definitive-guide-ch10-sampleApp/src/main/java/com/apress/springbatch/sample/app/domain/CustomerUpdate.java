package com.apress.springbatch.sample.app.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class CustomerUpdate {

    @Getter
    private final long customerId;

    @Override
    public String toString() {
        return "CustomerUpdate{customerId=" + customerId + '}';
    }
}
