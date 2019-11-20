package com.apress.springbatch.custom.item.processor.batch;

import com.apress.springbatch.custom.item.processor.domain.Customer;
import org.springframework.batch.item.ItemProcessor;

/**
 *
 */
public class EvenFilteringItemProcessor implements ItemProcessor<Customer, Customer> {
    @Override
    public Customer process(Customer customer) throws Exception {
        return Integer.parseInt(customer.getZip()) % 2 == 0 ? null: customer;
    }
}
