package com.apress.springbatch.stax.itemwriter;


import com.apress.springbatch.stax.itemwriter.domain.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dbatista
 */
@EnableBatchProcessing
@SpringBootApplication
public class StaxItemWriterApplication {


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
                .names(new String[] {"firstName",
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
    @StepScope
    public StaxEventItemWriter<Customer> xmlCustomerWriter(@Value("#{jobParameters['outputFile']}")
                                                                       Resource outputFile) {

        final Map<String, Class> aliases = new HashMap<>();

        aliases.put("customer", Customer.class);

        final XStreamMarshaller marshaller = new XStreamMarshaller();

        marshaller.setAliases(aliases);;
        marshaller.afterPropertiesSet();

        return new StaxEventItemWriterBuilder<Customer>()
                .name("customerItemWriter")
                .resource(outputFile)
                .marshaller(marshaller)
                .rootTagName("customers")
                .build();
    }

    @Bean
    public Step xmlFormatStep() {
        return this.stepBuilderFactory
                .get("xmlFormatStep")
                .<Customer, Customer>chunk(10)
                .reader(this.customerFileReader(null))
                .writer(this.xmlCustomerWriter(null))
                .build();
    }

    @Bean
    public Job xmlFormatJob() {
        return this.jobBuilderFactory
                .get("xmlFormatJob")
                .start(this.xmlFormatStep())
                .build();
    }


    public static void main(String[] args) {
        SpringApplication.run(StaxItemWriterApplication.class, args);
    }
}
