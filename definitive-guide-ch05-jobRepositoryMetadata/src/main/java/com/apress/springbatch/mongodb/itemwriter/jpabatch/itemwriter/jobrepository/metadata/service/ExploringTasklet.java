package com.apress.springbatch.mongodb.itemwriter.jpabatch.itemwriter.jobrepository.metadata.service;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.List;

/**
 * @author dbatista
 */
@AllArgsConstructor
public class ExploringTasklet implements Tasklet {

    private final JobExplorer jobExplorer;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        final String jobName = chunkContext.getStepContext().getJobName();

        List<JobInstance> instances = jobExplorer.getJobInstances(jobName, 0, Integer.MAX_VALUE);

        System.out.println(String.format("-- There are %d job instances for the job %s --",
                instances.size(), jobName));

        System.out.println("They have had the following results");
        System.out.println("************************************");

        for (JobInstance instance : instances) {
            List<JobExecution> jobExecutions =
                    this.jobExplorer.getJobExecutions(instance);

            System.out.println(
                    String.format("Instance %d had %d executions",
                            instance.getInstanceId(),
                            jobExecutions.size()));

            for (JobExecution jobExecution : jobExecutions) {
                System.out.println(
                        String.format("\tExecution %d resulted in Exit Status %s",
                                jobExecution.getId(),
                                jobExecution.getExitStatus()));
            }
        }

        return RepeatStatus.FINISHED;
    }
}
