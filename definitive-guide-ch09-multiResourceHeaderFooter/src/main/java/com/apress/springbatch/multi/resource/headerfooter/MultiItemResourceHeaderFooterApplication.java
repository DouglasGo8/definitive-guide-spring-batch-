package com.apress.springbatch.multi.resource.headerfooter;

import com.apress.springbatch.multi.resource.headerfooter.batch.CustomerRecordCountFooterCallback;
import com.apress.springbatch.multi.resource.headerfooter.domain.Customer;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemWriter;
import org.springframework.batch.item.file.builder.MultiResourceItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.FormatterLineAggregator;
import org.springframework.beans.BeanWrapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;

/**
 * @author dbatista
 */
@AllArgsConstructor
@EnableBatchProcessing
@SpringBootApplication
public class MultiItemResourceHeaderFooterApplication {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public JdbcCursorItemReader<Customer> multiResourceJdbcReader(DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<Customer>()
                .name("customerJdbcCursorItemReader")
                .dataSource(dataSource)
                .sql("SELECT * FROM TBL_CUSTOMER_READER")
                .rowMapper(new BeanPropertyRowMapper<>(Customer.class))
                .build();
    }

    @Bean
    public MultiResourceItemWriter<Customer> multiFlatFileItemWriter() {
        return new MultiResourceItemWriterBuilder<Customer>()
                .name("multiFlatFileItemWriter")
                .delegate(delegateCustomerItemWriter(null))
                .itemCountLimitPerResource(25)
                .resource(new FileSystemResource("src/main/resources/output/"))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<Customer> delegateCustomerItemWriter(CustomerRecordCountFooterCallback footerCallback) {
        BeanWrapperFieldExtractor<Customer> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"firstName", "lastName", "address", "city", "state", "zip"});
        fieldExtractor.afterPropertiesSet();

        FormatterLineAggregator<Customer> lineAggregator = new FormatterLineAggregator<>();
        lineAggregator.setFormat("%s %s lives at %s %s in %s, %s.");
        lineAggregator.setFieldExtractor(fieldExtractor);

        FlatFileItemWriter<Customer> itemWriter = new FlatFileItemWriter<>();

        itemWriter.setName("delegateCustomerItemWriter");
        itemWriter.setLineAggregator(lineAggregator);
        itemWriter.setAppendAllowed(true);
        itemWriter.setFooterCallback(footerCallback);

        return itemWriter;
    }

    @Bean
    public Step multiFlatFileGeneratorStep() {
        return this.stepBuilderFactory
                .get("multiFlatFileGeneratorStep")
                .<Customer, Customer>chunk(10)
                .reader(this.multiResourceJdbcReader(null))
                .writer(this.multiFlatFileItemWriter())
                .build();
    }

    @Bean
    public Job multiFlatFileGeneratorJob() {
        return this.jobBuilderFactory
                .get("multiFlatFileGeneratorJob")
                .start(this.multiFlatFileGeneratorStep())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(MultiItemResourceHeaderFooterApplication.class, args);
    }
}
