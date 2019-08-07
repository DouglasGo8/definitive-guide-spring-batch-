package com.apress.springbatch.chunkbased.batch;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;

/**
 * @author dbatista
 */
public class LoggingStepStartStopListener {

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        System.out.println(stepExecution.getStepName() + " has begun!");
    }

    @AfterStep
    public ExitStatus
    afterStep(StepExecution stepExecution) {
        System.out.println(stepExecution.getStepName() + " has ended!");

        return stepExecution.getExitStatus();
    }
}
