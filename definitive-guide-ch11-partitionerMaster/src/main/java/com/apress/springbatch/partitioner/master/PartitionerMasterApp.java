package com.apress.springbatch.partitioner.master;

import com.apress.springbatch.partitioner.master.domain.Transaction;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
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

/**
 * @author dbatista
 */
@EnableBatchProcessing
@SpringBootApplication
public class PartitionerMasterApp {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    @Bean
    @StepScope
    public ItemWriter<Transaction> writer() {
        return (items) -> items.forEach(out::println);
    }


    @Bean
    @StepScope
    public FlatFileItemReader<Transaction> fileTransactionReader(@Value("#{stepExecutionContext['file']}")
                                                                         Resource resource) {
        return new FlatFileItemReaderBuilder<Transaction>()
                .name("fileTransactionReader")
                .resource(resource)
                .delimited()
                .names(new String[]{"account", "timestamp", "amount"})
                .fieldSetMapper(fieldSet -> {
                    final Transaction transaction = new Transaction();
                    //
                    transaction.setAccount(fieldSet.readRawString("account"));
                    transaction.setAmount(fieldSet.readBigDecimal("amount"));
                    transaction.setTimestamp(fieldSet.readDate("timestamp", "yyyy-MM-dd HH:mm:ss"));
                    //
                    return transaction;
                })
                .build();
    }

    @Bean
    @StepScope
    public MultiResourcePartitioner partitioner(@Value("#{jobParameters['inputFiles']}") Resource[] resource) {
        final MultiResourcePartitioner partitioner = new MultiResourcePartitioner();
        //
        partitioner.setKeyName("file");
        partitioner.setResources(resource);
        return partitioner;
    }

    /**
     * Worker Step
     *
     * @return work step
     */
    @Bean
    public Step step1() {
        return this.stepBuilderFactory.get("step1")
                .<Transaction, Transaction>chunk(100)
                .reader(this.fileTransactionReader(null))
                .writer(this.writer())
                .build();

    }

    /**
     * SimpleAsyncTaskExecutor just only to Tests
     *
     * @return
     */
    @Bean
    public TaskExecutorPartitionHandler partitionHandler() {
        final TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(this.step1());
        partitionHandler.setTaskExecutor(new SimpleAsyncTaskExecutor());
        //
        return partitionHandler;
    }

    /**
     * Partitioned Step
     *
     * @return partitioned Step
     */
    @Bean
    public Step partitionedMaster() {
        return this.stepBuilderFactory.get("step1")
                .partitioner(this.step1().getName(), partitioner(null))
                .partitionHandler(this.partitionHandler())
                .build();
    }

    @Bean
    public Job partitionerJob() {
        return this.jobBuilderFactory.get("partitionerJob")
                .start(this.partitionedMaster())
                .build();
    }


    public static void main(String[] args) {
        SpringApplication.run(PartitionerMasterApp.class, args);
    }

}
