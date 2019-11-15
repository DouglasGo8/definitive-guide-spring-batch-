package com.apress.springbatch.property.extract;


import com.apress.springbatch.property.extract.domain.Customer;
import com.apress.springbatch.property.extract.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.adapter.PropertyExtractingDelegatingItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;


/**
 * @author dbatista
 */
@AllArgsConstructor
@EnableBatchProcessing
@SpringBootApplication
public class PropertyExtractDelegateApplication {


    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

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
    public PropertyExtractingDelegatingItemWriter<Customer> itemWriter(CustomerService customerService) {
        PropertyExtractingDelegatingItemWriter<Customer> propItem =
                new PropertyExtractingDelegatingItemWriter<>();

        propItem.setTargetObject(customerService);
        propItem.setTargetMethod("logCustomerAddress");
        propItem.setFieldsUsedAsTargetMethodArguments(new String[] {"address", "city", "state", "zip"});

        return propItem;
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
    public Job propertiesFormatJob() {
        return this.jobBuilderFactory
                .get("propertiesFormatJob")
                .start(this.formatStep())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(PropertyExtractDelegateApplication.class, args);
    }
}
