package com.bank.fixedlength.cassandra;


import com.bank.fixedlength.cassandra.batch.PatrimonyRepository;
import com.bank.fixedlength.cassandra.domain.Patrimony;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import javax.sql.DataSource;

/**
 * @author dbatista
 */
@EnableBatchProcessing
@SpringBootApplication
@EnableCassandraRepositories(basePackageClasses = Patrimony.class)
public class FixedLengthTKEJApplication {


    @Autowired
    private JobBuilderFactory job;

    @Autowired
    private StepBuilderFactory step;


    @Bean
    @StepScope
    public FlatFileItemReader<Patrimony> reader(@Value("#{jobParameters['patrimonyFile']}") Resource inputFile) {
        return new FlatFileItemReaderBuilder<Patrimony>()
                .name("reader")
                .resource(inputFile)
                .fixedLength()
                .columns(new Range[]{new Range(1, 14), new Range(15, 26)})
                .names(new String[]{"ssn", "pyBalance"})
                .targetType(Patrimony.class)
                .build();
    }

    @Bean
    public RepositoryItemWriter writer(final PatrimonyRepository repo) {

        return new RepositoryItemWriterBuilder<Patrimony>()
                .repository(repo)
                .methodName("insert")
                .build();
    }

    @Bean
    public Step copyFixedLengthFile() {
        return this.step.get("copyFixedLengthFile")
                .<Patrimony, Patrimony>chunk(50)
                .reader(reader(null))
                .writer(this.writer(null))
                .build();
    }

    @Bean
    public Job job() {
        return this.job.get("fixedLengthTKEJJob")
                .incrementer(new RunIdIncrementer())
                .start(this.copyFixedLengthFile())
                .build();


    }

    public static void main(String[] args) {
        SpringApplication.run(FixedLengthTKEJApplication.class, args);
    }
}
