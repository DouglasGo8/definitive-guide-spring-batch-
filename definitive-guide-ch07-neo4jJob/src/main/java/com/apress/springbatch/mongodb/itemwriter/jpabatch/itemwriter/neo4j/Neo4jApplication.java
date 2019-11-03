package com.apress.springbatch.mongodb.itemwriter.jpabatch.itemwriter.neo4j;



import org.neo4j.ogm.session.SessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.Neo4jItemReader;
import org.springframework.batch.item.data.builder.Neo4jItemReaderBuilder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Collections;
import java.util.EventListener;
import java.util.Map;

import static java.lang.System.out;

/**
 *
 */
@EnableBatchProcessing
@SpringBootApplication
@EnableConfigurationProperties(Neo4jProperties.class)
public class Neo4jApplication {


    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public SessionFactory sessionFactory(org.neo4j.ogm.config.Configuration configuration,
                                         ObjectProvider<EventListener> eventListeners) {
        SessionFactory sessionFactory = new SessionFactory(configuration,
                "com.apress.springbatch.neo4j.domain");

        eventListeners.stream().forEach(out::println);

        return sessionFactory;
    }

    @Bean
    public org.neo4j.ogm.config.Configuration configuration(Neo4jProperties properties) {
        return properties.createConfiguration();
    }

    @Bean
    public Neo4jItemReader<Map> categoriesBySupplierItemReader(SessionFactory sessionFactory) {
        return new Neo4jItemReaderBuilder<Map>()
                .name("categoriesBySupplierItemReader")
                .startStatement("n=node(*)")
                .matchStatement("(s:Supplier)-->(:Product)-->(c:Category)")
                .returnStatement("s.companyName, collect(distinct c.categoryName) as categories")
                .returnStatement("s.companyName, c.categoryName")
                .returnStatement("s.companyName")
                .orderByStatement("s.companyName")
                .parameterValues(Collections.emptyMap())
                .pageSize(10)
                //.targetType(Company.class)
                .targetType(Map.class)
                //.targetType(String.class)
                .sessionFactory(sessionFactory)
                .build();
    }

    @Bean
    public ItemWriter<Map> itemWriter() {
        return (items) -> items.parallelStream().forEach(out::println);
    }


    @Bean
    public Step copyFileStep() {
        return this.stepBuilderFactory
                .get("copyFileStep")
                .<Map, Map>chunk(10)
                .reader(this.categoriesBySupplierItemReader(null))
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
        SpringApplication.run(Neo4jApplication.class, args);
    }

}
