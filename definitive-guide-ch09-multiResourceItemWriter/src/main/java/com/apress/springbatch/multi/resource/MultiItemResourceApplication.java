package com.apress.springbatch.multi.resource;


import com.apress.springbatch.multi.resource.batch.CustomerOutputFileSuffixCreator;
import com.apress.springbatch.multi.resource.batch.CustomerXmlHeaderCallback;
import com.apress.springbatch.multi.resource.domain.Customer;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.MultiResourceItemWriter;
import org.springframework.batch.item.file.builder.MultiResourceItemWriterBuilder;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
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
public class MultiItemResourceApplication {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;


    @Bean
    public JdbcCursorItemReader<Customer> customerJdbcCursorItemReader(DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<Customer>()
                .name("customerJdbcCursorItemReader")
                .dataSource(dataSource)
                .sql("SELECT * FROM TBL_CUSTOMER_READER")
                .rowMapper(new BeanPropertyRowMapper<>(Customer.class))
                .build();
    }

    @Bean
    public MultiResourceItemWriter<Customer> multiCustomerFileWriter(CustomerOutputFileSuffixCreator suffixCreator) {
        return new MultiResourceItemWriterBuilder<Customer>()
                .name("multiResourceItemWriter")
                .delegate(this.delegateItemWriter(null))
                .itemCountLimitPerResource(25)
                .resource(new FileSystemResource("src/main/resources/output/"))
                .resourceSuffixCreator(suffixCreator)
                .build();
    }

    @Bean
    @StepScope
    public StaxEventItemWriter<Customer> delegateItemWriter(CustomerXmlHeaderCallback headerCallBack) {

        Map<String, Class<?>> aliases = new HashMap<>();
        aliases.put("customer", Customer.class);

        XStreamMarshaller marshaller = new XStreamMarshaller();

        marshaller.setAliases(aliases);

        marshaller.afterPropertiesSet();


        return new StaxEventItemWriterBuilder<Customer>()
                .name("customerItemWriter")
                .marshaller(marshaller)
                .rootTagName("customers")
                .headerCallback(headerCallBack)
                .build();


    }

    @Bean
    public Step multiXmlGeneratorStep() {
        return this.stepBuilderFactory
                .get("multiXmlGeneratorStep")
                .<Customer, Customer>chunk(10)
                .reader(this.customerJdbcCursorItemReader(null))
                .writer(this.multiCustomerFileWriter(null))
                .build();
    }

    @Bean
    public Job xmlGeneratorJob() {
        return this.jobBuilderFactory
                .get("xmlGeneratorJob")
                .start(this.multiXmlGeneratorStep())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(MultiItemResourceApplication.class, args);
    }
}
