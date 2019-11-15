package com.apress.springbatch.jmsitem.writer;


import com.apress.springbatch.jmsitem.writer.domain.Customer;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.jms.JmsItemReader;
import org.springframework.batch.item.jms.JmsItemWriter;
import org.springframework.batch.item.jms.builder.JmsItemReaderBuilder;
import org.springframework.batch.item.jms.builder.JmsItemWriterBuilder;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.oxm.xstream.XStreamMarshaller;

import javax.jms.ConnectionFactory;
import java.util.HashMap;
import java.util.Map;


/**
 * @author dbatista
 */
@AllArgsConstructor
@EnableBatchProcessing
@SpringBootApplication
public class JmsItemWriterApplication {


    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;


    /**
     * @return Serialized message content to json using TextMessage
     */
    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(connectionFactory);
        cachingConnectionFactory.afterPropertiesSet();
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setDefaultDestinationName("customers");
        jmsTemplate.setReceiveTimeout(5000L);

        return jmsTemplate;
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> customerFileReader(@Value("#{jobParameters['customerFile']}")
                                                                   Resource inputFile) {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerItemReader")
                .delimited()
                .names(new String[]{"firstName",
                        "middleInitial",
                        "lastName",
                        "address",
                        "city",
                        "state",
                        "zip"})
                .targetType(Customer.class)
                .resource(inputFile)
                .build();
    }

    @Bean
    @StepScope
    public StaxEventItemWriter<Customer> xmlOutputWriter(@Value("#{jobParameters['outputFile']}") Resource outputFile) {

        Map<String, Class<?>> aliases = new HashMap<>();
        aliases.put("customer", Customer.class);

        XStreamMarshaller marshaller = new XStreamMarshaller();
        marshaller.setAliases(aliases);

        return new StaxEventItemWriterBuilder<Customer>()
                .name("xmlOutputWriter")
                .resource(outputFile)
                .marshaller(marshaller)
                .rootTagName("customers")
                .build();
    }

    @Bean
    public JmsItemReader<Customer> jmsItemReader(JmsTemplate jmsTemplate) {
        return new JmsItemReaderBuilder<Customer>()
                .jmsTemplate(jmsTemplate)
                .itemType(Customer.class)
                .build();
    }

    @Bean
    public JmsItemWriter<Customer> jmsItemWriter(JmsTemplate jmsTemplate) {
        return new JmsItemWriterBuilder<Customer>()
                .jmsTemplate(jmsTemplate)
                .build();
    }

    @Bean
    public Step formatInputStep() {
        return this.stepBuilderFactory
                .get("formatInputStep")
                .<Customer, Customer>chunk(10)
                .reader(this.customerFileReader(null))
                .writer(this.jmsItemWriter(null))
                .build();
    }

    @Bean
    public Step formatOutputStep() {
        return this.stepBuilderFactory
                .get("formatOutputStep")
                .<Customer, Customer>chunk(10)
                .reader(this.jmsItemReader(null))
                .writer(this.xmlOutputWriter(null))
                .build();
    }


    @Bean
    public Job jmsFormatJob() {
        return this.jobBuilderFactory
                .get("jmsFormatJob")
                .start(this.formatInputStep())
                .next(this.formatOutputStep())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(JmsItemWriterApplication.class, args);
    }

}
