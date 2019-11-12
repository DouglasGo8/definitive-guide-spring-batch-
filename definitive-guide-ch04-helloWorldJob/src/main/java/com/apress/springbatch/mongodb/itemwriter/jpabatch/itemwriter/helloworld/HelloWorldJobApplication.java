package com.apress.springbatch.mongodb.itemwriter.jpabatch.itemwriter.helloworld;


import com.apress.springbatch.mongodb.itemwriter.jpabatch.itemwriter.helloworld.batch.DailyJobTimestamper;
import com.apress.springbatch.mongodb.itemwriter.jpabatch.itemwriter.helloworld.batch.JobLoggerListener;
import com.apress.springbatch.mongodb.itemwriter.jpabatch.itemwriter.helloworld.batch.ParameterValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.listener.JobListenerFactoryBean;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

import static java.lang.System.out;

/**
 * @author dbatista
 */
@EnableBatchProcessing
@SpringBootApplication
public class HelloWorldJobApplication {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public CompositeJobParametersValidator validator() {

        final CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
        final DefaultJobParametersValidator defaultJobParametersValidator = new
                DefaultJobParametersValidator(new String[]{"fileName"}, new String[]{"name", "currentDate"});

        defaultJobParametersValidator.afterPropertiesSet();
        validator.setValidators(Arrays.asList(new ParameterValidator(), defaultJobParametersValidator));

        return validator;
    }

    @Bean
    public Step step1() {
        return this.stepBuilderFactory.get("step1")
                /*.tasklet((stepContribution, chunkContext) -> {

                    final String name = (String) chunkContext.getStepContext()
                            .getJobParameters()
                            .get("name");

                    out.println(String.format("Hi %s, Hello, world from Spring Batch!", name));
                    return RepeatStatus.FINISHED;
                })*/
                .tasklet(this.helloWorldTasklet(null, null)) // late binding
                .build();
    }


    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("basicJob")
                .start(this.step1())
                .validator(this.validator())
                .incrementer(new DailyJobTimestamper())
                .listener(JobListenerFactoryBean.getListener(new JobLoggerListener()))
                .build();

    }

    /**
     * @param name     auto increment
     * @param fileName file csv
     * @return tasklet With Incrementer
     */
    @Bean
    @StepScope
    public Tasklet helloWorldTasklet(
            @Value("#{jobParameters['name']}") String name,
            @Value("#{jobParameters['fileName']}") String fileName) {
        return (stepContribution, chunkContext) -> {
            out.println(String.format("Hi %s with file %s, Hello, world from Spring Batch!", name, fileName));
            return RepeatStatus.FINISHED;
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(HelloWorldJobApplication.class, args);
    }
}
