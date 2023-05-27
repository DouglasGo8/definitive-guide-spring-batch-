package com.apress.springbatch;


import com.apress.springbatch.domain.Pojo;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.SimpleJvmExitCodeMapper;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@EnableBatchProcessing
@SpringBootApplication
public class TwentyMillionOfApp {

  @Autowired
  private JobBuilderFactory jobBuilderFactory;
  @Autowired
  private StepBuilderFactory stepBuilderFactory;


  @Bean
  @StepScope
  public FlatFileItemReader<Pojo> uuidsRead(
          @Value("#{jobParameters['uuidsFile']}") Resource inputFile) {
    return new FlatFileItemReaderBuilder<Pojo>()
            .name("customerFileReader")
            .resource(inputFile)
            .delimited()
            //.delimiter("\n")
            .names(new String[]{"info"})
            .targetType(Pojo.class)
            .build();
  }

  @Bean
  public ItemWriter<Pojo> uuidsWriter() {
    return (items) -> items.parallelStream().map(Pojo::toString);

  }


  @Bean
  public Step delimitedStep() {
    return this.stepBuilderFactory
            .get("delimitedStep")
            .<Pojo, Pojo>chunk(1000000)
            .reader(this.uuidsRead(null))
            .writer(this.uuidsWriter())
            .taskExecutor(new SimpleAsyncTaskExecutor())
            .build();
  }

  @Bean
  public Job twentyMillionOfJob() {
    return this.jobBuilderFactory
            .get("twentyMillionOfJob")
            .start(this.delimitedStep())
            //.listener(JobListenerFactoryBean.getListener(new TwentyMillionListener()))
            .build();
  }

  public static void main(String[] args) {
    SpringApplication.run(TwentyMillionOfApp.class, args);
  }


  private static final class TwentyMillionListener {
    @AfterJob
    public void afterJob(JobExecution jobExecution) {

      final SimpleJvmExitCodeMapper mapper = new SimpleJvmExitCodeMapper();
      final String END_MESSAGE = "***** %s has completed with the status %s *****";

      System.out.printf((END_MESSAGE) + "%n", jobExecution.getJobInstance().getJobName(),
              jobExecution.getStatus());
      //
      System.exit(mapper.intValue(jobExecution.getExitStatus().getExitCode()));
    }
  }
}
