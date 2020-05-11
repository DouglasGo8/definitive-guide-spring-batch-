package com.apress.springbatch.schedule.quartz.openshift;


import lombok.AllArgsConstructor;
import org.apache.camel.ProducerTemplate;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import java.time.LocalDateTime;

/**
 * @author dbatista
 */
@EnableBatchProcessing
@SpringBootApplication
@ImportResource({"classpath:/spring/app-context.xml"})
public class ScheduleQuartzApplication {


    public static void main(String[] args) {
        SpringApplication.run(ScheduleQuartzApplication.class, args);
    }

    /**
     *
     */
    @Configuration
    @AllArgsConstructor
    public static class BatchConfiguration {
        @Autowired
        private ProducerTemplate template;

        private final JobBuilderFactory job;
        private final StepBuilderFactory step;

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
                    .tasklet(this::taskletMethod).build();
        }

        /**
         * @param stepContribution step from Job
         * @param chunkContext chuck data
         * @return Job Status
         */
        private RepeatStatus taskletMethod(StepContribution stepContribution, ChunkContext chunkContext) {
            this.template.asyncSendBody("seda:jmsWrapper",
                    String.format("Message from Apache Camel at %s", LocalDateTime.now().toString()));
            return RepeatStatus.FINISHED;
        }

    }

}
