package com.apress.springbatch.itemwriter.adapter;


import com.apress.springbatch.itemwriter.adapter.domain.Customer;
import com.apress.springbatch.itemwriter.adapter.service.CustomerService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

/**
 * @author dbatista
 */
@EnableBatchProcessing
@SpringBootApplication
public class ItemWriterAdapterApplication {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public ItemWriterAdapterApplication(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> customerFileReader(@Value("#{jobParameters['customerFile']}")
                                                                   Resource inputFile) {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerFileReader")
                .resource(inputFile)
                .delimited()
                .names(new String[]{"firstName",
                        "middleInitial",
                        "lastName",
                        "address",
                        "city",
                        "state",
                        "zip"})
                .targetType(Customer.class)
                .build();
    }

    @Bean
    public ItemWriterAdapter<Customer> itemWriter(CustomerService customerService) {
        ItemWriterAdapter<Customer> customerItemWriterAdapter =
                new ItemWriterAdapter<>();
        customerItemWriterAdapter.setTargetObject(customerService);
        customerItemWriterAdapter.setTargetMethod("log");


        return customerItemWriterAdapter;
    }


    @Bean
    public Step formatStep() {
        return this.stepBuilderFactory
                .get("formatStep")
                .<Customer, Customer>chunk(10)
                .reader(this.customerFileReader(null))
                .writer(this.itemWriter(null))
                .build();
    }

    @Bean
    public Job itemWriterAdapterFormatJob() {
        return this.jobBuilderFactory
                .get("itemWriterAdapterFormatJob")
                .start(this.formatStep())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(ItemWriterAdapterApplication.class, args);
    }
}
