package com.apress.springbatch.delimited.tokenize;

import com.apress.springbatch.delimited.tokenize.batch.CustomerFieldSetMapper;
import com.apress.springbatch.delimited.tokenize.batch.CustomerFileLineTokenizer;
import com.apress.springbatch.delimited.tokenize.domain.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import static java.lang.System.out;

/**
 * @author dbatista
 */
@EnableBatchProcessing
@SpringBootApplication
public class DelimitedLineTokenizeApplication {


    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> customerItemTokenizerReader
            (@Value("#{jobParameters['customerFile']}") Resource inputFile) {

        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerItemTokenizerReader")
                .lineTokenizer(new CustomerFileLineTokenizer())
                .fieldSetMapper(new CustomerFieldSetMapper())
                .resource(inputFile)
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
                .reader(this.customerItemTokenizerReader(null))
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
        SpringApplication.run(DelimitedLineTokenizeApplication.class, args);
    }
}
