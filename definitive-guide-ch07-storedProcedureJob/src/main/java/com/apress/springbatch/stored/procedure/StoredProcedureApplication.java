package com.apress.springbatch.stored.procedure;


import com.apress.springbatch.stored.procedure.domain.Customer;
import com.apress.springbatch.stored.procedure.domain.CustomerRowMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.StoredProcedureItemReader;
import org.springframework.batch.item.database.builder.StoredProcedureItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;

import javax.sql.DataSource;
import java.sql.Types;

import static java.lang.System.out;

/**
 * @author dbatista
 */
@EnableBatchProcessing
@SpringBootApplication
public class StoredProcedureApplication {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public StoredProcedureItemReader storedProcedureItemReader(DataSource dataSource,
                                                               @Value("#{jobParameters['city']}") String city) {
        return new StoredProcedureItemReaderBuilder<Customer>()
                .name("storedProcedureItemReader")
                .dataSource(dataSource)
                .procedureName("call customer_list")
                .parameters(new SqlParameter[]{
                        new SqlParameter("cityOption", Types.NVARCHAR),
                        new SqlInOutParameter("result", Types.REF_CURSOR)
                }).preparedStatementSetter(new ArgumentPreparedStatementSetter(new Object[]{city, null}))
                .rowMapper(new CustomerRowMapper())
                .build();
    }

    @Bean
    public ItemWriter<Customer> itemWriter() {
        return (items) -> items.parallelStream().forEach(out::println);
    }

    @Bean
    public Step copyFileStep() {
        return this.stepBuilderFactory
                .get("copyFileStep")
                .<Customer, Customer>chunk(10)
                .reader(this.storedProcedureItemReader(null, null))
                .writer(this.itemWriter())
                .build();
    }

    @Bean
    public Job job() {
        return this.jobBuilderFactory
                .get("job")
                .start(this.copyFileStep())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(StoredProcedureApplication.class, args);
    }
}
