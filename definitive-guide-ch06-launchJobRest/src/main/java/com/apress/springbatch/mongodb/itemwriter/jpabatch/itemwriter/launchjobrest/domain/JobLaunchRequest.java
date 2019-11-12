package com.apress.springbatch.mongodb.itemwriter.jpabatch.itemwriter.launchjobrest.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

import java.util.Properties;

/**
 * @author dbatista
 */
@Data
@NoArgsConstructor
public class JobLaunchRequest {

    private String name;
    private Properties jobParameters;

    public JobParameters getJobParameters() {
        Properties properties = new Properties();
        properties.putAll(this.jobParameters);

        return new JobParametersBuilder(properties)
                .toJobParameters();
    }
}