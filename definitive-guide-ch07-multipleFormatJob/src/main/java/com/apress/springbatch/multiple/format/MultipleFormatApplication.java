package com.apress.springbatch.multiple.format;

import com.apress.springbatch.multiple.format.batch.TransactionFieldSetMapper;
import com.apress.springbatch.multiple.format.domain.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import java.util.HashMap;
import java.util.Map;

import static java.lang.System.out;

/**
 * @author dbatista
 */
@EnableBatchProcessing
@SpringBootApplication
@SuppressWarnings("unchecked")
public class MultipleFormatApplication {


    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public FlatFileItemReader customerItemReader(@Value("#{jobParameters['customerFile']}") Resource inputFile) {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerItemReader")
                .lineMapper(this.lineTokenizer())
                .resource(inputFile)
                .build();
    }

    @Bean
    public PatternMatchingCompositeLineMapper lineTokenizer() {

        Map<String, LineTokenizer> lineTokenizers = new HashMap<>(2);

        lineTokenizers.put("CUST*", this.customerLineTokenizer());
        lineTokenizers.put("TRANS*", this.transactionLineTokenizer());

        Map<String, FieldSetMapper> fieldSetMappers = new HashMap<>(2);
        BeanWrapperFieldSetMapper<Customer> customerFieldSetMapper = new BeanWrapperFieldSetMapper<>();

        customerFieldSetMapper.setTargetType(Customer.class);

        fieldSetMappers.put("CUST*", customerFieldSetMapper);
        fieldSetMappers.put("TRANS*", new TransactionFieldSetMapper());

        PatternMatchingCompositeLineMapper compositeLineMappers = new PatternMatchingCompositeLineMapper();

        compositeLineMappers.setTokenizers(lineTokenizers);
        compositeLineMappers.setFieldSetMappers(fieldSetMappers);

        return compositeLineMappers;
    }

    @Bean
    public DelimitedLineTokenizer transactionLineTokenizer() {
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames("prefix", "accountNumber", "transactionDate", "amount");
        return lineTokenizer;
    }

    @Bean
    public DelimitedLineTokenizer customerLineTokenizer() {

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();

        lineTokenizer.setNames("firstName", "middleInitial", "lastName", "address", "city", "state", "zipCode");

        lineTokenizer.setIncludedFields(1, 2, 3, 4, 5, 6, 7);

        return lineTokenizer;
    }

    @Bean
    public ItemWriter itemWriter() {
        return (items) -> items.parallelStream().forEach(out::println);
    }

    @Bean
    @SuppressWarnings("unchecked")
    public Step copyFileStep() {
        return this.stepBuilderFactory.get("copyFileStep")
                .<Customer, Customer>chunk(10)
                .reader(this.customerItemReader(null))
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
        SpringApplication.run(MultipleFormatApplication.class, args);
    }
}
