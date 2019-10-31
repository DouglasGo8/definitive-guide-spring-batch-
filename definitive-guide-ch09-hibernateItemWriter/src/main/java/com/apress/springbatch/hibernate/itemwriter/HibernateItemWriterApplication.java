package com.apress.springbatch.hibernate.itemwriter;

import com.apress.springbatch.hibernate.itemwriter.domain.Customer;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.HibernateItemWriter;
import org.springframework.batch.item.database.builder.HibernateItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import javax.persistence.EntityManagerFactory;

/**
 *
 */
@EnableBatchProcessing
@SpringBootApplication
public class HibernateItemWriterApplication {


    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

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
    public HibernateItemWriter<Customer> hibernateItemWriter(EntityManagerFactory entityManagerFactory) {
        return new HibernateItemWriterBuilder<Customer>()
                .sessionFactory(entityManagerFactory.unwrap(SessionFactory.class))
                .build();
    }

    @Bean
    public Step hibernateFormatStep() {
        return this.stepBuilderFactory
                .get("hibernateFormatStep")
                .<Customer, Customer>chunk(10)
                .reader(this.customerFileReader(null))
                .writer(this.hibernateItemWriter(null))
                .build();
    }

    @Bean
    public Job hibernateFormatJob() {
        return this.jobBuilderFactory
                .get("jobBuilderFactory")
                .start(this.hibernateFormatStep())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(HibernateItemWriterApplication.class, args);
    }

}
