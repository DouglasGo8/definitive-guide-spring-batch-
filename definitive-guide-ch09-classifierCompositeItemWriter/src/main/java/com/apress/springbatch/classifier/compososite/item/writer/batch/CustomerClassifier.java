package com.apress.springbatch.classifier.compososite.item.writer.batch;

import com.apress.springbatch.classifier.compososite.item.writer.domain.Customer;
import lombok.AllArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.classify.Classifier;

/**
 * @author dbatista
 */
@AllArgsConstructor
public class CustomerClassifier implements Classifier<Customer, ItemWriter<? super Customer>> {

    private final ItemWriter<Customer> fileItemWriter;
    private final ItemWriter<Customer> jdbcItemWriter;

    @Override
    public ItemWriter<? super Customer> classify(Customer customer) {
        if (customer.getState().matches("^[A-M].*")) {
            return fileItemWriter;
        } else {
            return jdbcItemWriter;
        }
    }
}
