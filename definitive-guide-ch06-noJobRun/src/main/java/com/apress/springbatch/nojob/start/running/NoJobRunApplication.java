package com.apress.springbatch.nojob.start.running;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@EnableBatchProcessing
@SpringBootApplication
public class NoJobRunApplication {


    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepFactoryBean;

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("job")
                .start(this.step1())
                .build();
    }

    @Bean
    public Step step1() {
        return this.stepFactoryBean.get("step1")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("step1 ran If Enabled property in Yaml file is true!");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    public static void main(String[] args) {

        SpringApplication.run(NoJobRunApplication.class, args);

    }
}
