package com.apress.springbatch.classifier.compososite.item.writer;


import com.apress.springbatch.classifier.compososite.item.writer.batch.CustomerClassifier;
import com.apress.springbatch.classifier.compososite.item.writer.domain.Customer;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.item.support.builder.ClassifierCompositeItemWriterBuilder;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.classify.Classifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.oxm.xstream.XStreamMarshaller;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dbatista
 */
@AllArgsConstructor
@EnableBatchProcessing
@SpringBootApplication
public class ClassifierCompositeItemApplication {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> classifierCompositeWriterItemReader(
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
    public ClassifierCompositeItemWriter<Customer> compositeItemWriter() {

        final Classifier<Customer, ItemWriter<? super Customer>> classifier =
                new CustomerClassifier(this.xmlDelegateItemWriter(null),
                        this.jdbcDelegateItemWriter(null));

        return new ClassifierCompositeItemWriterBuilder<Customer>()
                .classifier(classifier)
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
    public Step classifierCompositeWriterStep() {
        return this.stepBuilderFactory.get("compositeWriterStep")
                .<Customer, Customer>chunk(10)
                .reader(this.classifierCompositeWriterItemReader(null))
                .writer(this.compositeItemWriter())
                .stream(this.xmlDelegateItemWriter(null))
                .build();

    }


    @Bean
    public Job classifierCompositeWriterJob() {
        return this.jobBuilderFactory.get("compositeWriterJob")
                .start(this.classifierCompositeWriterStep())
                .build();
    }


    public static void main(String[] args) {
        SpringApplication.run(ClassifierCompositeItemApplication.class, args);
    }
}
