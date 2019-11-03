package com.apress.springbatch.mongodb.itemwriter.jpabatch.itemwriter.helloworld.batch;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;

import java.util.Date;

/**
 * @author dbatista
 */
public class DailyJobTimestamper implements JobParametersIncrementer {
    @Override
    public JobParameters getNext(JobParameters jobParameters) {
        return new JobParametersBuilder(jobParameters)
                .addDate("currentDate", new Date())
                .toJobParameters();
    }
}
