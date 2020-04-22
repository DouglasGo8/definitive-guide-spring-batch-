package com.apress.springbatch.schedule.quartz.openshift.service;


import lombok.AllArgsConstructor;
import org.quartz.JobExecutionContext;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author dbatista
 */
@AllArgsConstructor
public class BatchScheduledJob extends QuartzJobBean {

    private final Job job;
    private final JobLauncher jobLauncher;
    private final JobExplorer jobExplorer;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {

        JobParameters jobParameters = new JobParametersBuilder(this.jobExplorer)
                .getNextJobParameters(this.job)
                .toJobParameters();
        try {
            this.jobLauncher.run(this.job, jobParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
