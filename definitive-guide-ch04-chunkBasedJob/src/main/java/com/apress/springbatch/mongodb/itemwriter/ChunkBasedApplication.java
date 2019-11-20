package com.apress.springbatch.mongodb.itemwriter;

import com.apress.springbatch.mongodb.itemwriter.chunkbased.batch.LoggingStepStartStopListener;
import com.apress.springbatch.mongodb.itemwriter.chunkbased.batch.RandomChunkSizePolicy;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.policy.CompositeCompletionPolicy;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.policy.TimeoutTerminationPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.lang.System.out;

/**
 * @author dbatista
 */
@EnableBatchProcessing
@SpringBootApplication
public class ChunkBasedApplication {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("job")
                .start(this.step1())
                .build();
    }

    @Bean
    public Step step1() {
        return this.stepBuilderFactory.get("step1")
                //.<String, String>chunk(10)
                //.<String, String>chunk(this.completionPolicy())
                .<String, String>chunk(this.randomCompletionPolicy())
                .reader(this.itemReader())
                .writer(this.itemWriter())
                .listener(new LoggingStepStartStopListener())
                .build();
    }

    /*@Bean
    @StepScope
    public FlatFileItemReader<String> itemReader(@Value("#{jobParameters['inputFile']}") Resource inputFile) {
        return new FlatFileItemReaderBuilder<String>()
                .name("itemReader")
                .resource(inputFile)
                .lineMapper(new PassThroughLineMapper())
                .build();
    }*/

    @Bean
    public ListItemReader<String> itemReader() {

        final List<String> items = new ArrayList<>(100);

        for (int i = 0; i < 100; i++)
            items.add(UUID.randomUUID().toString());

        return new ListItemReader<String>(items);
    }

    /*@Bean
    @StepScope
    public FlatFileItemWriter<String> itemWriter(@Value("#{jobParameters['outputFile']}") Resource outputFile) {
        return new FlatFileItemWriterBuilder<String>()
                .name("itemWriter")
                .resource(outputFile)
                .lineAggregator(new PassThroughLineAggregator<>())
                .build();
    }*/

    @Bean
    public ItemWriter<String> itemWriter() {
        return items -> {
            Collections.singletonList(items).forEach(item -> {
                out.println(item + "\n");
            });
        };
    }

    @Bean
    public CompletionPolicy completionPolicy() {
        CompositeCompletionPolicy policy = new CompositeCompletionPolicy();

        policy.setPolicies(new CompletionPolicy[]{
                new TimeoutTerminationPolicy(3),
                new SimpleCompletionPolicy(50)
        });

        return policy;
    }

    @Bean
    public CompletionPolicy randomCompletionPolicy() {
        return new RandomChunkSizePolicy();
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(ChunkBasedApplication.class, args);
    }
}
