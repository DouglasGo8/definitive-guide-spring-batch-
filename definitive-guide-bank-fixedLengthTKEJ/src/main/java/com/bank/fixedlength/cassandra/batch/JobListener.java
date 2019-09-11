package com.bank.fixedlength.cassandra.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.launch.support.SimpleJvmExitCodeMapper;

public class JobListener {

    @AfterJob
    public void afterJob(JobExecution jobExecution) {

        final SimpleJvmExitCodeMapper mapper = new SimpleJvmExitCodeMapper();
        final String END_MESSAGE = "***** %s has completed with the status %s *****";

        System.out.println(String.format(END_MESSAGE, jobExecution.getJobInstance().getJobName(),
                jobExecution.getStatus()));
        //
        System.exit(mapper.intValue(jobExecution.getExitStatus().getExitCode()));
    }
}
