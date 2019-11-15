package com.apress.springbatch.data.repository;


import com.apress.springbatch.data.repository.domain.Customer;
import com.apress.springbatch.data.repository.domain.CustomerRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;

import java.util.Collections;

import static java.lang.System.out;

/**
 * @author dbatista
 */
@EnableBatchProcessing
@SpringBootApplication
public class SpringDataRepositoryApplication {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    @Bean
    @StepScope
    public RepositoryItemReader<Customer> customerItemReader(CustomerRepository repository,
                                                             @Value("#{jobParameters['city']}") String city) {
        return new RepositoryItemReaderBuilder<Customer>()
                .name("customerItemReader")
                .arguments(Collections.singletonList(city))
                .methodName("findByCity")
                .repository(repository)
                .sorts(Collections.singletonMap("lastName", Sort.Direction.ASC))
                .build();
    }


    @Bean
    public ItemWriter<Customer> itemWriter() {
        return (items) -> items.parallelStream().forEach(out::println);
    }

    @Bean
    public Step copyFileStep() {
        return this.stepBuilderFactory.get("copyFileStep")
                .<Customer, Customer>chunk(10)
                .reader(this.customerItemReader(null, null))
                .writer(this.itemWriter())
                .build();
    }

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("job")
                .start(this.copyFileStep())
                .build();

    }


    public static void main(String[] args) {
        SpringApplication.run(SpringDataRepositoryApplication.class, args);
    }
}


