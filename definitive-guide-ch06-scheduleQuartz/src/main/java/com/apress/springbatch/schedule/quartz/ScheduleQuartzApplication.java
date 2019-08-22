package com.apress.springbatch.schedule.quartz;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dbatista
 */
@EnableBatchProcessing
@SpringBootApplication
public class ScheduleQuartzApplication {


    public static void main(String[] args) {
        SpringApplication.run(ScheduleQuartzApplication.class, args);
    }

    /**
     *
     */
    @Configuration
    public class BatchConfiguration {

        @Autowired
        private JobBuilderFactory job;

        @Autowired
        private StepBuilderFactory step;

        @Bean
        public Job job() {
            return this.job.get("job")
                    .incrementer(new RunIdIncrementer())
                    .start(this.step1())
                    .build();

        }

        @Bean
        public Step step1() {
            return this.step.get("step1")
                    .tasklet((stepContribution, chunkContext) -> {
                                System.out.println("step1 run With Quartz Framework!");
                                return RepeatStatus.FINISHED;
                            }
                    ).build();
        }

    }

}
