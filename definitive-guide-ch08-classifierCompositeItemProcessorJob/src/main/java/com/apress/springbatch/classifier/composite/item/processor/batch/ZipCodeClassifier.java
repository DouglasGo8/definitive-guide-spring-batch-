package com.apress.springbatch.classifier.composite.item.processor.batch;

import com.apress.springbatch.classifier.composite.item.processor.domain.Customer;
import lombok.AllArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.classify.Classifier;

/**
 *
 */
@AllArgsConstructor
public class ZipCodeClassifier implements Classifier<Customer, ItemProcessor<Customer, Customer>> {

    private ItemProcessor<Customer, Customer> oddItemProcessor;
    private ItemProcessor<Customer, Customer> evenItemProcessor;

    @Override
    public ItemProcessor<Customer, Customer> classify(Customer classifiable) {
        if (Integer.parseInt(classifiable.getZip()) % 2 == 0) {
            return evenItemProcessor;
        } else {
            return oddItemProcessor;
        }
    }
}
