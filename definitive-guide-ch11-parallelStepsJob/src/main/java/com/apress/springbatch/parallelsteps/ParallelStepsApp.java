package com.apress.springbatch.parallelsteps;

import com.apress.springbatch.parallelsteps.domain.Transaction;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import static java.lang.System.out;


@EnableBatchProcessing
@SpringBootApplication
public class ParallelStepsApp {


    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    public static void main(String[] args) {
        SpringApplication.run(ParallelStepsApp.class, args);
    }

    @Bean
    public Job parallelStepsJob() {

        final Flow secondFlow = new FlowBuilder<Flow>("secondFlow")
                .start(this.step2())
                .build();

        final Flow parallelFlow = new FlowBuilder<Flow>("parallelFlow")
                .start(this.step1())
                .split(new SimpleAsyncTaskExecutor())
                .add(secondFlow)
                .build();

        return this.jobBuilderFactory.get("parallelStepsJob")
                .incrementer(new RunIdIncrementer())
                .start(parallelFlow)
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return this.stepBuilderFactory.get("step1")
                .<Transaction, Transaction>chunk(100)
                .reader(this.xmlTransactionReader(null))
                .writer(this.writer())
                .build();
    }

    @Bean
    public Step step2() {
        return this.stepBuilderFactory.get("step2")
                .<Transaction, Transaction>chunk(100)
                .reader(this.fileTransactionReader(null))
                .writer(this.writer())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Transaction> fileTransactionReader(@Value("#{jobParameters['inputFlatFile']}") Resource resource) {
        return new FlatFileItemReaderBuilder<Transaction>()
                .name("fileTransactionReader")
                .resource(resource)
                .delimited()
                .names(new String[]{"accountId", "amount", "timestamp"})
                .fieldSetMapper(fieldSet -> {
                    final Transaction transaction = new Transaction();
                    //
                    transaction.setAccountId(fieldSet.readString("accountId"));
                    transaction.setAmount(fieldSet.readDouble("amount"));
                    transaction.setTimestamp(fieldSet.readDate("timestamp", "yyyy-MM-dd HH:mm:ss"));

                    return transaction;
                }).build();

    }
    @Bean
    @StepScope
    public StaxEventItemReader<Transaction> xmlTransactionReader(@Value("#{jobParameters['inputXmlFile']}") Resource resource) {

        final Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setClassesToBeBound(Transaction.class);
        //
        return new StaxEventItemReaderBuilder<Transaction>()
                .name("xmlTransactionReader")
                .resource(resource)
                .addFragmentRootElements("transaction")
                .unmarshaller(unmarshaller)
                .build();
    }

    @Bean
    @StepScope
    public ItemWriter<Transaction> writer() {
        return (items) -> items.forEach(out::println);
    }


}
