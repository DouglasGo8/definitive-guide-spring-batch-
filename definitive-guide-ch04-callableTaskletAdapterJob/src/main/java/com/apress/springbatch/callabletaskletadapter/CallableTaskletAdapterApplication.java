package com.apress.springbatch.callabletaskletadapter;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.CallableTaskletAdapter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.Callable;

/**
 * @author dbatista
 */
@EnableBatchProcessing
@SpringBootApplication
public class CallableTaskletAdapterApplication {


    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job callableJob() {
        return this.jobBuilderFactory.get("callableJob")
                .start(callableStep())
                .build();
    }

    @Bean
    public Step callableStep() {
        return this.stepBuilderFactory.get("callableStep")
                .tasklet(this.tasklet()).build();
    }

    @Bean
    public CallableTaskletAdapter tasklet() {

        System.out.println(String.format("Executed in the thread %s", Thread.currentThread().getName()));

        CallableTaskletAdapter callableTaskletAdapter = new CallableTaskletAdapter();
        callableTaskletAdapter.setCallable(callableTaskletObject());

        return callableTaskletAdapter;

    }

    @Bean
    public Callable<RepeatStatus> callableTaskletObject() {
        return () -> {
            System.out.println(String.format("this was executed in another thread %s", Thread.currentThread().getName()));
            return RepeatStatus.FINISHED;
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(CallableTaskletAdapterApplication.class, args);
    }
}
