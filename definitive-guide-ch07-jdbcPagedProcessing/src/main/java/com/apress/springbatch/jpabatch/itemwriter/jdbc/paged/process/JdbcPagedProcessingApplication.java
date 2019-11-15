package com.apress.springbatch.jpabatch.itemwriter.jdbc.paged.process;

import com.apress.springbatch.jpabatch.itemwriter.jdbc.paged.process.domain.Customer;
import com.apress.springbatch.jpabatch.itemwriter.jdbc.paged.process.domain.CustomerRowMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.out;

/**
 * @author dbatista
 */
@EnableBatchProcessing
@SpringBootApplication
public class JdbcPagedProcessingApplication {


    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public JdbcPagingItemReader<Customer> customerJdbcPagingReader(DataSource dataSource,
                                                                   PagingQueryProvider queryProvider,
                                                                   @Value("#{jobParameters['city']}") String city) {

        Map<String, Object> parametersValues = new HashMap<>();
        parametersValues.put("city", city);

        return new JdbcPagingItemReaderBuilder<Customer>()
                .name("customerJdbcPagingReader")
                .dataSource(dataSource)
                .queryProvider(queryProvider)
                .parameterValues(parametersValues)
                .pageSize(5)
                .rowMapper(new CustomerRowMapper())
                .build();

    }


    @Bean
    public SqlPagingQueryProviderFactoryBean pagingQueryProvider(DataSource dataSource) {
        SqlPagingQueryProviderFactoryBean factoryBean = new SqlPagingQueryProviderFactoryBean();

        factoryBean.setDataSource(dataSource);
        factoryBean.setSelectClause("select *");
        factoryBean.setFromClause("from TBL_CUSTOMER_READER");
        factoryBean.setWhereClause("WHERE city = :city");
        factoryBean.setSortKey("lastName");
        return factoryBean;
    }

    @Bean
    public ItemWriter<Customer> itemWriter() {
        return (items) -> items.parallelStream().forEach(out::println);
    }

    @Bean
    public Step copyFileStep() {
        return this.stepBuilderFactory.get("copyFileStep")
                .<Customer, Customer>chunk(10)
                .reader(this.customerJdbcPagingReader(null, null, null))
                .writer(this.itemWriter())
                .build();
    }

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("job")
                .start(this.copyFileStep())
                .build();
    }


    public static void main(String[] args) {
        SpringApplication.run(JdbcPagedProcessingApplication.class, args);
    }
}
