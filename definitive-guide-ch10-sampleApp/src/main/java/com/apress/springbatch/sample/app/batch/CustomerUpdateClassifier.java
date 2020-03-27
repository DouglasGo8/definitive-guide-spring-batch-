package com.apress.springbatch.sample.app.batch;

import com.apress.springbatch.sample.app.domain.CustomerAddressUpdate;
import com.apress.springbatch.sample.app.domain.CustomerContactUpdate;
import com.apress.springbatch.sample.app.domain.CustomerNameUpdate;
import com.apress.springbatch.sample.app.domain.CustomerUpdate;
import lombok.AllArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.classify.Classifier;


@AllArgsConstructor
public class CustomerUpdateClassifier implements Classifier<CustomerUpdate, ItemWriter<? super CustomerUpdate>> {

    private final JdbcBatchItemWriter<CustomerUpdate> recordType1ItemWriter;
    private final JdbcBatchItemWriter<CustomerUpdate> recordType2ItemWriter;
    private final JdbcBatchItemWriter<CustomerUpdate> recordType3ItemWriter;

    @Override
    public ItemWriter<? super CustomerUpdate> classify(CustomerUpdate classifiable) {

        if (classifiable instanceof CustomerNameUpdate) {
            return recordType1ItemWriter;
        } else if (classifiable instanceof CustomerAddressUpdate) {
            return recordType2ItemWriter;
        } else if (classifiable instanceof CustomerContactUpdate) {
            return recordType3ItemWriter;
        } else {
            throw new IllegalArgumentException("Invalid type: " + classifiable.getClass().getCanonicalName());
        }
    }
}
