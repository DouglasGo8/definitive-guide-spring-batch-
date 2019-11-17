package com.apress.springbatch.composite.item.processor;

import com.apress.springbatch.composite.item.processor.domain.Customer;
import com.apress.springbatch.composite.item.processor.domain.UniqueLastNameValidator;
import com.apress.springbatch.composite.item.processor.service.UpperCaseNameService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemProcessorAdapter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.support.ScriptItemProcessor;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import java.util.Arrays;

import static java.lang.System.out;


/**
 * @author dbatista
 */
@EnableBatchProcessing
@SpringBootApplication
public class CompositeItemProcessorApplication {


    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    @Bean
    @StepScope
    public FlatFileItemReader<Customer> customerItemReader(@Value("#{jobParameters['customerFile']}")
                                                                   Resource inputFile) {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerItemReader")
                .delimited()
                .names(new String[]{"firstName",
                        "middleInitial",
                        "lastName",
                        "address",
                        "city",
                        "state",
                        "zip"})
                .targetType(Customer.class)
                .resource(inputFile)
                .build();


    }

    @Bean
    public ItemWriter<Customer> itemWriter() {
        return (items) -> items.forEach(out::println);
    }

    @Bean
    public UniqueLastNameValidator validator() {
        UniqueLastNameValidator uniqueLastNameValidator = new UniqueLastNameValidator();

        uniqueLastNameValidator.setName("validator");

        return uniqueLastNameValidator;
    }

    @Bean
    public ValidatingItemProcessor<Customer> customerValidatingItemProcessor() {
        ValidatingItemProcessor<Customer> itemProcessor = new ValidatingItemProcessor<>(validator());

        itemProcessor.setFilter(true);

        return itemProcessor;
    }

    @Bean
    public ItemProcessorAdapter<Customer, Customer> upperCaseItemProcessor(UpperCaseNameService service) {
        ItemProcessorAdapter<Customer, Customer> adapter = new ItemProcessorAdapter<>();

        adapter.setTargetObject(service);
        adapter.setTargetMethod("upperCase");

        return adapter;
    }


    @Bean
    @StepScope
    public ScriptItemProcessor<Customer, Customer> lowerCaseItemProcessor(
            @Value("#{jobParameters['script']}") Resource script) {

        ScriptItemProcessor<Customer, Customer> itemProcessor = new ScriptItemProcessor<>();

        itemProcessor.setScript(script);

        return itemProcessor;
    }

    @Bean
    public CompositeItemProcessor<Customer, Customer> itemProcessor() {
        final CompositeItemProcessor<Customer, Customer> itemProcessor = new CompositeItemProcessor<>();

        itemProcessor.setDelegates(Arrays.asList(this.customerValidatingItemProcessor(),
                this.upperCaseItemProcessor(null),
                this.lowerCaseItemProcessor(null)));

        return itemProcessor;

    }


    @Bean
    public Step copyFileStep() {
        return this.stepBuilderFactory
                .get("stepBuilderFactory")
                .<Customer, Customer>chunk(5)
                .reader(this.customerItemReader(null))
                .processor(this.itemProcessor())
                .writer(this.itemWriter())
                .build();

    }

    @Bean
    public Job job() {
        return this.jobBuilderFactory
                .get("job")
                .start(this.copyFileStep())
                .build();
    }


    public static void main(String[] args) {
        SpringApplication.run(CompositeItemProcessorApplication.class, args);
    }


}
