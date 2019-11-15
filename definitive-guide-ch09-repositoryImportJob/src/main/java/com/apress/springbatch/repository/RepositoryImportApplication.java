package com.apress.springbatch.repository;


import com.apress.springbatch.repository.domain.Customer;
import com.apress.springbatch.repository.domain.CustomerRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author dbatista
 */
@EnableBatchProcessing
@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = Customer.class)
public class RepositoryImportApplication {


    @Autowired
    private  JobBuilderFactory jobBuilderFactory;
    @Autowired
    private  StepBuilderFactory stepBuilderFactory;


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
                        "city",
                        "state",
                        "zip"})
                .targetType(Customer.class)
                .build();
    }

    @Bean
    public RepositoryItemWriter<Customer> repositoryItemWriter(CustomerRepository repository) {
        return new RepositoryItemWriterBuilder<Customer>()
                .repository(repository)
                .methodName("save")
                .build();
    }

    @Bean
    public Step repositoryFormatStep() {
        return this.stepBuilderFactory
                .get("repositoryFormatStep")
                .<Customer, Customer>chunk(10)
                .reader(customerFileReader(null))
                .writer(repositoryItemWriter(null))
                .build();
    }

    @Bean
    public Job repositoryFormatJob() {
        return this.jobBuilderFactory
                .get("repositoryFormatJob")
                .start(this.repositoryFormatStep())
                .build();
    }


    public static void main(String[] args) {
        SpringApplication.run(RepositoryImportApplication.class, args);
    }


}
