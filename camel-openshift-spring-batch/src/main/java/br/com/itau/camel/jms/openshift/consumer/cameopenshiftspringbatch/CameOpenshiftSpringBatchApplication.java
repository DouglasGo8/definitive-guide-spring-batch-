package br.com.itau.camel.jms.openshift.consumer.cameopenshiftspringbatch;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;


@SpringBootApplication
@ImportResource({"classpath:/spring/app-context.xml"})
public class CameOpenshiftSpringBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(CameOpenshiftSpringBatchApplication.class, args);
	}


	@Component
	final class JmsOpenshiftConsumer extends RouteBuilder {


		@Override
		public void configure() throws Exception {
	
	
			onException(Exception.class)
					.logExhausted(true)
					.logStackTrace(true)
					.logExhaustedMessageHistory(true)
			.end();
	
			from("wmq:DEV.QUEUE.1")
					.log("${body}")					
			.end();
	   }

	}

}
