package com.apress.springbatch.custom.input;

import com.apress.springbatch.custom.input.batch.CustomerItemListener;
import com.apress.springbatch.custom.input.batch.CustomerItemReader;
import com.apress.springbatch.custom.input.domain.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static java.lang.System.out;

/**
 *
 */
@EnableBatchProcessing
@SpringBootApplication
public class CustomInputApplication {


    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    @Bean
    public CustomerItemReader customerItemReader() {
        CustomerItemReader customerItemReader = new CustomerItemReader();

        customerItemReader.setName("customerItemReader");

        return customerItemReader;
    }

    @Bean
    public ItemWriter<Customer> itemWriter() {
        return (items) -> items.parallelStream().forEach(out::println);
    }

    @Bean
    public Step copyFileStep() {
        return this.stepBuilderFactory
                .get("copyFileStep")
                .<Customer, Customer>chunk(10)
                .reader(this.customerItemReader())
                .writer(this.itemWriter())
                .listener(new CustomerItemListener())
                .build();
    }

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("job")
                .start(this.copyFileStep())
                .build();
    }

    public static void main(String[] args) {

        SpringApplication.run(CustomInputApplication.class, args);
    }

}
