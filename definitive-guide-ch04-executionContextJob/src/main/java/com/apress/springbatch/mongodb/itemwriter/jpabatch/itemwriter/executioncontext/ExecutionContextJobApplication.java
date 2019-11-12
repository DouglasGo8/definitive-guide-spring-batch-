package com.apress.springbatch.mongodb.itemwriter.jpabatch.itemwriter.executioncontext;


import com.apress.springbatch.mongodb.itemwriter.jpabatch.itemwriter.executioncontext.batch.ExecutionContextTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author dbatista
 */
@EnableBatchProcessing
@SpringBootApplication
public class ExecutionContextJobApplication {


    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job executioContextJob() {
        return this.jobBuilderFactory.get("executionContextJob")
                .start(executionContextStep())
                .next(this.step2())
                .build();
    }

    @Bean
    public Step executionContextStep() {
        return this.stepBuilderFactory.get("executionStepContext")
                .tasklet(this.executionContextTasklet())
                .listener(this.promotionListener())
                .build();
    }

    @Bean
    public Step step2() {
        return this.stepBuilderFactory.get("step2")
                .tasklet(((stepContribution, chunkContext) -> {

                    final String name = (String) chunkContext.getStepContext()
                            .getJobParameters().get("name");

                    System.out.println(String.format("Good Bye with %s!", name));

                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    @StepScope
    public Tasklet executionContextTasklet() {
        return new ExecutionContextTasklet();
    }

    @Bean
    public StepExecutionListener promotionListener() {
        ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();

        listener.setKeys(new String[]{"name"});

        return listener;

    }

    public static void main(String[] args) {
        SpringApplication.run(ExecutionContextJobApplication.class, args);
    }

}
