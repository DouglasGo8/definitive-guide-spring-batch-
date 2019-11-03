package com.apress.springbatch.mongodb.itemwriter.jpabatch.itemwriter.composite.item.processor.service;


import com.apress.springbatch.mongodb.itemwriter.jpabatch.itemwriter.composite.item.processor.domain.Customer;
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
