package com.apress.springbatch.item.processor.adapter.service;


import com.apress.springbatch.item.processor.adapter.domain.Customer;
import org.springframework.stereotype.Service;

@Service
public class UpperCaseNameService {

    public Customer upperCase(Customer customer) {

        Customer newCustomer = new Customer(customer);

        newCustomer.setFirstName(newCustomer.getFirstName().toUpperCase());
        newCustomer.setMiddleInitial(newCustomer.getMiddleInitial().toUpperCase());
        newCustomer.setLastName(newCustomer.getLastName().toUpperCase());

        return newCustomer;
    }

}
