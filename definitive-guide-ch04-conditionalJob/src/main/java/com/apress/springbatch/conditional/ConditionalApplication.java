package com.apress.springbatch.conditional;


import com.apress.springbatch.conditional.batch.RandomDecider;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
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
public class ConditionalApplication {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Tasklet passTasklet() {
        return (stepContribution, chunkContext) -> {
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet successTasklet() {
        return (stepContribution, chunkContext) -> {
            System.out.println("SUCCESS!");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet failTasklet() {
        return (stepContribution, chunkContext) -> {
            System.out.println("FAILURE!");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public JobExecutionDecider decider() {
        return new RandomDecider();
    }

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("conditionalJob")
                .start(this.firstStep())
                .next(this.decider())
                .from(this.decider())
                    .on("FAILED")
                        //.end() Ending the Job Exit Status Code
                        //.fail() Failing the Job Exit Status Code
                        //.stopAndRestart(this.successStep())
                        .to(this.failureStep())
                .from(this.decider())
                    .on("*")
                    .to(this.successStep())
                .end()
                .build();
    }

    @Bean
    public Step firstStep() {
        return this.stepBuilderFactory.get("firstStep")
                .tasklet(this.passTasklet())
                .build();
    }

    @Bean
    public Step successStep() {
        return this.stepBuilderFactory.get("successStep")
                .tasklet(this.successTasklet())
                .build();
    }

    @Bean
    public Step failureStep() {
        return this.stepBuilderFactory.get("failureStep")
                .tasklet(failTasklet())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(ConditionalApplication.class, args);
    }
}
