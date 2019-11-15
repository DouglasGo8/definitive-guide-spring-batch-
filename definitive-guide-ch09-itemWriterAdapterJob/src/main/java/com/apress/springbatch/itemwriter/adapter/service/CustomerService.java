package com.apress.springbatch.itemwriter.adapter.service;

import com.apress.springbatch.itemwriter.adapter.domain.Customer;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    public void log(Customer customer) {
        System.out.println(customer);
    }

    public void logCustomerAddress(String address,
                                   String city,
                                   String state,
                                   String zip) {
        System.out.println(
                String.format("I just saved the address:\n%s\n%s, %s\n%s",
                        address,
                        city,
                        state,
                        zip));
    }
}
