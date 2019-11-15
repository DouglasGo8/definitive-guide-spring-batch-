package com.apress.springbatch.jpabatch.itemwriter.jdbcbatch.itemwriter;

import com.apress.springbatch.jpabatch.itemwriter.jdbcbatch.itemwriter.domain.Customer;
import com.apress.springbatch.jpabatch.itemwriter.jdbcbatch.itemwriter.domain.CustomerItemPreparedStatementSetter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;

/**
 * @author dbatista
 */
@EnableBatchProcessing
@SpringBootApplication
public class JdbcBatchItemWriterApplication {


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
    public JdbcBatchItemWriter<Customer> jdbcCustomerWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Customer>()
                .dataSource(dataSource)
                .sql("INSERT INTO TBL_CUSTOMER_READER (firstname, middleinitial, lastname, address, city, state, zipcode)" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)")
                .itemPreparedStatementSetter(new CustomerItemPreparedStatementSetter())
                .build();
    }

    /*@Bean
    public JdbcBatchItemWriter<Customer> jdbcCustomerWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Customer>()
                .dataSource(dataSource)
                .sql("INSERT INTO TBL_CUSTOMER (firstname,middleinitial, lastname, address, " +
                        "city, state, zipcode) VALUES (:firstName, " +
                        ":middleInitial, " +
                        ":lastName, " +
                        ":address, " +
                        ":city, " +
                        ":state, " +
                        ":zip)")
                .beanMapped()
                .build();
    }*/


    @Bean
    public Step jdbcFormatStep() {
        return this.stepBuilderFactory
                .get("jdbcFormatStep")
                .<Customer, Customer>chunk(10)
                .reader(this.customerFileReader(null))
                .writer(this.jdbcCustomerWriter(null))
                .build();
    }


    @Bean
    public Job jdbcFormatJob() {
        return this.jobBuilderFactory
                .get("jdbcFormatJob")
                .start(this.jdbcFormatStep())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(JdbcBatchItemWriterApplication.class, args);
    }
}
