package com.apress.springbatch.composite.item.writer;


import com.apress.springbatch.composite.item.writer.domain.Customer;
import lombok.AllArgsConstructor;
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
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.oxm.xstream.XStreamMarshaller;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dbatista
 */
@AllArgsConstructor
@EnableBatchProcessing
@SpringBootApplication
public class CompositeItemApplication {


    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> compositeItemWriterItemReader(
            @Value("#{jobParameters['customerFile']}") Resource inputFile) {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerFileReader")
                .resource(inputFile)
                .delimited()
                .names(new String[]{
                        "firstName",
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
    public CompositeItemWriter<Customer> compositeItemWriter() {
        return new CompositeItemWriterBuilder<Customer>()
                .delegates(Arrays.asList(
                        this.xmlDelegateItemWriter(null),
                        this.jdbcDelegateItemWriter(null)))
                .build();
    }

    @Bean
    @StepScope
    public StaxEventItemWriter<Customer> xmlDelegateItemWriter(
            @Value("#{jobParameters['outputFile']}") Resource outputFile) {

        final Map<String, Class<?>> aliases = new HashMap<>();
        final XStreamMarshaller marshaller = new XStreamMarshaller();

        aliases.put("customer", Customer.class);
        marshaller.setAliases(aliases);
        marshaller.afterPropertiesSet();

        return new StaxEventItemWriterBuilder<Customer>()
                .name("xmlDelegateItemWriter")
                .resource(outputFile)
                .marshaller(marshaller)
                .rootTagName("customers")
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Customer> jdbcDelegateItemWriter(DataSource dataSource) {

        return new JdbcBatchItemWriterBuilder<Customer>()
                .namedParametersJdbcTemplate(new NamedParameterJdbcTemplate(dataSource))
                .sql("INSERT INTO TBL_CUSTOMER_WRITER (firstname, middleinitial, lastname, address, city, " +
                        "state, " +
                        "zipcode) " +
                        "VALUES(:firstName, " +
                        ":middleInitial, " +
                        ":lastName, " +
                        ":address, " +
                        ":city, " +
                        ":state, " +
                        ":zip)")
                .beanMapped()
                .build();
    }


    @Bean
    public Step compositeWriterStep() {
        return this.stepBuilderFactory.get("compositeWriterStep")
                .<Customer, Customer>chunk(10)
                .reader(this.compositeItemWriterItemReader(null))
                .writer(this.compositeItemWriter())
                .build();

    }


    @Bean
    public Job compositeWriterJob() {
        return this.jobBuilderFactory.get("compositeWriterJob")
                .start(this.compositeWriterStep())
                .build();
    }


    public static void main(String[] args) {
        SpringApplication.run(CompositeItemApplication.class, args);
    }

}
