package com.apress.springbatch.schedule.quartz.openshift.infra;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class JmsWrapperTemplate extends RouteBuilder {

    @Override
    public void configure() throws Exception {


        onException(Exception.class)
                .logExhausted(true)
                .logStackTrace(true)
                .logExhaustedMessageHistory(true)
        .end();

        from("seda:jmsWrapper?concurrentConsumers=5")
                .log("${body}")
                .to("wmq:DEV.QUEUE.1")
        .end();
   }
}
