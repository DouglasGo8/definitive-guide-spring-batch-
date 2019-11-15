package com.apress.springbatch.helloworld.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;

/**
 * @author dbatista
 */
public class JobLoggerListener {

    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        final String START_MESSAGE = "----> %s is beginning execution";
        System.out.println(String.format(START_MESSAGE, jobExecution.getJobInstance().getJobName()));
    }

    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        final String END_MESSAGE = "-----> %s has completed with the status %s";
        System.out.println(String.format(END_MESSAGE, jobExecution.getJobInstance().getJobName(),
                jobExecution.getStatus()));
    }
}
