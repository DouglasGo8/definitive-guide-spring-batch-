package com.apress.springbatch.message.partitioner.master;


import lombok.AllArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.partition.RemotePartitioningMasterStepBuilderFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.amqp.Amqp;
import org.springframework.messaging.Message;

/**
 * @author dbatista
 */
@Configuration
@Profile("master")
@AllArgsConstructor
@EnableBatchIntegration
public class MasterConfigurationPartitionerApp {


    private final JobBuilderFactory jobBuilderFactory;
    private final RemotePartitioningMasterStepBuilderFactory masterStepBuilderFactory;

    public static void main(String[] args) {
        SpringApplication.run(MasterConfigurationPartitionerApp.class, args);
    }

    @Bean
    public DirectChannel requests() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow outboundFlow(AmqpTemplate amqpTemplate) {
        return IntegrationFlows.from(this.requests())
                .handle(Amqp.outboundAdapter(amqpTemplate).routingKey("requests"))
                .get();
    }

    @Bean
    public DirectChannel replies() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow inboundFlow(ConnectionFactory connectionFactory) {
        return IntegrationFlows.from((Publisher<Message<?>>) Amqp.inboundAdapter(connectionFactory, "replies"))
                .channel(replies())
                .get();

    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();

        connectionFactory.setAddresses("localhost");
        connectionFactory.setUsername("user");
        connectionFactory.setPassword("welcome1");
        return  connectionFactory;
    }

    @Bean
    public AmqpTemplate amqpTemplate() {
        return  null;
    }
}
