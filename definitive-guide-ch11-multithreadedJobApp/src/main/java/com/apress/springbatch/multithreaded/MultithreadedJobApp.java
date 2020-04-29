package com.apress.springbatch.multithreaded;

import com.apress.springbatch.multithreaded.domain.Transaction;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import static java.lang.System.out;

@EnableBatchProcessing
@SpringBootApplication
public class MultithreadedJobApp {


    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    public static void main(String[] args) {
        SpringApplication.run(MultithreadedJobApp.class, args);
    }

    @Bean
    public Job multithreaddJob() {
        return this.jobBuilderFactory.get("multithreadedJob")
                .incrementer(new RunIdIncrementer())
                .start(this.step1())
                .build();
    }

    @Bean
    public Step step1() {
        return this.stepBuilderFactory.get("step1")
                .<Transaction, Transaction>chunk(100)
                .reader(this.fileTransactionReader(null))
                .writer(this.writer())
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();

    }


    @Bean
    @StepScope
    public FlatFileItemReader<Transaction> fileTransactionReader(@Value("#{jobParameters['inputFlatFile']}") Resource resource) {
        return new FlatFileItemReaderBuilder<Transaction>()
                .name("transactionItemReader")
                .resource(resource)
                //
                .saveState(false)
                //
                .delimited()
                .names(new String[]{"accountId", "amount", "timestamp"})
                .fieldSetMapper(fieldSet -> {
                    final Transaction transaction = new Transaction();
                    //
                    transaction.setAccountId(fieldSet.readString("accountId"));
                    transaction.setAmount(fieldSet.readDouble("amount"));
                    transaction.setTimestamp(fieldSet.readDate("timestamp", "yyyy-MM-dd HH:mm:ss"));

                    return transaction;
                })
                .build();

    }

    @Bean
    @StepScope
    public ItemWriter<Transaction> writer() {
        return (items) -> items.forEach(out::println);
    }

}
