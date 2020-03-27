package com.apress.springbatch.sample.app;

import com.apress.springbatch.sample.app.batch.CustomerItemValidator;
import com.apress.springbatch.sample.app.batch.CustomerUpdateClassifier;
import com.apress.springbatch.sample.app.domain.CustomerAddressUpdate;
import com.apress.springbatch.sample.app.domain.CustomerContactUpdate;
import com.apress.springbatch.sample.app.domain.CustomerNameUpdate;
import com.apress.springbatch.sample.app.domain.CustomerUpdate;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.file.transform.PatternMatchingCompositeLineTokenizer;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@EnableBatchProcessing
@SpringBootApplication
public class SampleApplication {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;


    public static void main(String... args) {
        SpringApplication.run(SampleApplication.class, args);
    }

    @Bean
    public Job job() throws Exception {
        return this.jobBuilderFactory.get("importJob")
                .incrementer(new RunIdIncrementer())
                .start(this.importCustomerUpdates())
                .build();
    }

    @Bean
    public Step importCustomerUpdates() throws Exception {
        return this.stepBuilderFactory.get("importCustomerUpdates")
                .<CustomerUpdate, CustomerUpdate>chunk(10)
                .reader(this.customerUpdateItemReader(null))
                .processor(this.customerValidatingItemProcessor(null))
                .writer(this.customerUpdateItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<CustomerUpdate> customerUpdateItemReader(@Value("#{jobParameters['customerUpdateFile']}") Resource inputFile) throws Exception {

        return new FlatFileItemReaderBuilder<CustomerUpdate>()
                .name("customerUpdateItemReader")
                .resource(inputFile)
                .lineTokenizer(this.customerUpdatesLineTokenizer())
                .fieldSetMapper(this.customerUpdateFieldSetMapper())
                .build();

    }


    /*@Bean
    public ItemWriter<CustomerUpdate> customerUpdateItemWriter() {
        return (items) -> items.forEach(System.out::println);
    }*/

    @Bean
    public ClassifierCompositeItemWriter<CustomerUpdate> customerUpdateItemWriter() {

        CustomerUpdateClassifier classifier =
                new CustomerUpdateClassifier(customerNameUpdateItemWriter(null),
                        customerAddressUpdateItemWriter(null),
                        customerContactUpdateItemWriter(null));

        ClassifierCompositeItemWriter<CustomerUpdate> compositeItemWriter =
                new ClassifierCompositeItemWriter<>();

        compositeItemWriter.setClassifier(classifier);

        return compositeItemWriter;
    }

    @Bean
    public ValidatingItemProcessor<CustomerUpdate> customerValidatingItemProcessor(CustomerItemValidator validator) {

        final ValidatingItemProcessor<CustomerUpdate> customerUpdateValidatingItemProcessor =
                new ValidatingItemProcessor<>(validator);

        customerUpdateValidatingItemProcessor.setFilter(true); // ignores validator

        return customerUpdateValidatingItemProcessor;
    }

    @Bean
    public JdbcBatchItemWriter<CustomerUpdate> customerNameUpdateItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<CustomerUpdate>()
                .beanMapped()
                .sql("UPDATE TBL_CUSTOMER " +
                        "SET FIRST_NAME = COALESCE(:firstName, FIRST_NAME), " +
                        "MIDDLE_NAME = COALESCE(:middleName, MIDDLE_NAME), " +
                        "LAST_NAME = COALESCE(:lastName, LAST_NAME) " +
                        "WHERE CUSTOMER_ID = :customerId")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<CustomerUpdate> customerAddressUpdateItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<CustomerUpdate>()
                .beanMapped()
                .sql("UPDATE TBL_CUSTOMER SET " +
                        "ADDRESS1 = COALESCE(:address1, ADDRESS1), " +
                        "ADDRESS2 = COALESCE(:address2, ADDRESS2), " +
                        "CITY = COALESCE(:city, CITY), " +
                        "STATE = COALESCE(:state, STATE), " +
                        "POSTAL_CODE = COALESCE(:postalCode, POSTAL_CODE) " +
                        "WHERE CUSTOMER_ID = :customerId")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<CustomerUpdate> customerContactUpdateItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<CustomerUpdate>()
                .beanMapped()
                .sql("UPDATE TBL_CUSTOMER SET " +
                        "EMAIL_ADDRESS = COALESCE(:emailAddress, EMAIL_ADDRESS), " +
                        "HOME_PHONE = COALESCE(:homePhone, HOME_PHONE), " +
                        "CELL_PHONE = COALESCE(:cellPhone, CELL_PHONE), " +
                        "WORK_PHONE = COALESCE(:workPhone, WORK_PHONE), " +
                        "NOTIFICATION_PREF = COALESCE(:notificationPreferences, NOTIFICATION_PREF) " +
                        "WHERE CUSTOMER_ID = :customerId")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public LineTokenizer customerUpdatesLineTokenizer() throws Exception {

        final DelimitedLineTokenizer recordType1 = new DelimitedLineTokenizer();

        recordType1.setNames("recordId", "customerId", "firstName", "middleName", "lastName");
        recordType1.afterPropertiesSet();

        final DelimitedLineTokenizer recordType2 = new DelimitedLineTokenizer();

        recordType2.setNames("recordId", "customerId", "address1", "address2", "city", "state", "postalCode");
        recordType2.afterPropertiesSet();

        final DelimitedLineTokenizer recordType3 = new DelimitedLineTokenizer();

        recordType3.setNames("recordId", "customerId", "emailAddress", "homePhone", "cellPhone", "workPhone", "notificationPreference");
        recordType3.afterPropertiesSet();

        final Map<String, LineTokenizer> tokenizers = new HashMap<>(3);

        tokenizers.put("1*", recordType1);
        tokenizers.put("2*", recordType2);
        tokenizers.put("3*", recordType3);

        final PatternMatchingCompositeLineTokenizer lineTokenizer = new PatternMatchingCompositeLineTokenizer();

        lineTokenizer.setTokenizers(tokenizers);

        return lineTokenizer;
    }

    @Bean
    public FieldSetMapper<CustomerUpdate> customerUpdateFieldSetMapper() {
        return fieldSet -> {
            switch (fieldSet.readInt("recordId")) {
                case 1:
                    return new CustomerNameUpdate(fieldSet.readLong("customerId"),
                            fieldSet.readString("firstName"),
                            fieldSet.readString("middleName"),
                            fieldSet.readString("lastName"));
                case 2:
                    return new CustomerAddressUpdate(fieldSet.readLong("customerId"),
                            fieldSet.readString("address1"),
                            fieldSet.readString("address2"),
                            fieldSet.readString("city"),
                            fieldSet.readString("state"),
                            fieldSet.readString("postalCode"));
                case 3:
                    String rawPreference = fieldSet.readString("notificationPreference");

                    Integer notificationPreference = null;

                    if (StringUtils.hasText(rawPreference)) {
                        notificationPreference = Integer.parseInt(rawPreference);
                    }

                    return new CustomerContactUpdate(fieldSet.readLong("customerId"),
                            fieldSet.readString("emailAddress"),
                            fieldSet.readString("homePhone"),
                            fieldSet.readString("cellPhone"),
                            fieldSet.readString("workPhone"),
                            notificationPreference);
                default:
                    throw new IllegalArgumentException("Invalid record type was found:" + fieldSet.readInt("recordId"));
            }
        };
    }


}
