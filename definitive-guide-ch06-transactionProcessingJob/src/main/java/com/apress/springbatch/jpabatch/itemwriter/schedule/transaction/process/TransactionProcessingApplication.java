package com.apress.springbatch.jpabatch.itemwriter.schedule.transaction.process;

import com.apress.springbatch.jpabatch.itemwriter.schedule.transaction.process.batch.TransactionReader;
import com.apress.springbatch.jpabatch.itemwriter.schedule.transaction.process.domain.AccountSummary;
import com.apress.springbatch.jpabatch.itemwriter.schedule.transaction.process.domain.Transaction;
import com.apress.springbatch.jpabatch.itemwriter.schedule.transaction.process.infrastructure.database.TransactionDao;
import com.apress.springbatch.jpabatch.itemwriter.schedule.transaction.process.infrastructure.database.impl.TransactionDaoImpl;
import com.apress.springbatch.jpabatch.itemwriter.schedule.transaction.process.batch.TransactionApplierProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.PassThroughFieldSetMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;

/**
 * @author dbatista
 */
@EnableBatchProcessing
@SpringBootApplication
public class TransactionProcessingApplication {


    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public TransactionReader transactionReader() {
        return new TransactionReader(this.fileItemReader(null));
    }

    @Bean
    @StepScope
    public FlatFileItemReader<FieldSet> fileItemReader(@Value("#{jobParameters['transactionFile']}") Resource inputFile) {
        //System.out.println(String.format("File Exists (true|false) => (%s)", inputFile.exists()));
        return new FlatFileItemReaderBuilder<FieldSet>()
                .name("fileItemReader")
                .resource(inputFile)
                .lineTokenizer(new DelimitedLineTokenizer())
                .fieldSetMapper(new PassThroughFieldSetMapper())
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Transaction> transactionWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO TRANSACTION (ACCOUNT_SUMMARY_ID, TIMESTAMP, AMOUNT) " +
                        "VALUES ((SELECT ID FROM ACCOUNT_SUMMARY WHERE ACCOUNT_NUMBER = :accountNumber), " +
                        ":timestamp, :amount)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Step importTransactionFileStep() {
        return this.stepBuilderFactory.get("importTransactionFileStep")
                .<Transaction, Transaction>chunk(50)
                .reader(this.transactionReader())
                .writer(this.transactionWriter(null))
                .allowStartIfComplete(true)
                .listener(transactionReader())
                .build();
    }

    @Bean
    public JdbcCursorItemReader<AccountSummary> accountSummaryReader(DataSource dataSource) {
        final String sqlStatement = "SELECT ACCOUNT_NUMBER, CURRENT_BALANCE FROM ACCOUNT_SUMMARY A " +
                "WHERE A.ID IN (SELECT DISTINCT T.ACCOUNT_SUMMARY_ID FROM TRANSACTION T)  " +
                "ORDER BY A.ACCOUNT_NUMBER";

        return new JdbcCursorItemReaderBuilder<AccountSummary>()
                .name("accountSummaryReader")
                .dataSource(dataSource)
                .sql(sqlStatement)
                .rowMapper((resultSet, i) -> {
                    AccountSummary summary = new AccountSummary();
                    summary.setAccountNumber(resultSet.getString("ACCOUNT_NUMBER"));
                    summary.setCurrentBalance(resultSet.getDouble("CURRENT_BALANCE"));
                    //System.out.println(summary);
                    return summary;
                })
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<AccountSummary> accountSummaryWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<AccountSummary>()
                .dataSource(dataSource)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("UPDATE ACCOUNT_SUMMARY SET CURRENT_BALANCE = :currentBalance WHERE ACCOUNT_NUMBER = :accountNumber")
                .build();
    }

    @Bean
    public Step applyTransactionStep() {
        return this.stepBuilderFactory.get("applyTransactionStep")
                .<AccountSummary, AccountSummary>chunk(50)
                .reader(this.accountSummaryReader(null))
                .processor(this.transactionApplierProcessor())
                .writer(this.accountSummaryWriter(null))
                .build();
    }

    @Bean
    public TransactionApplierProcessor transactionApplierProcessor() {
        return new TransactionApplierProcessor(transactionDao(null));
    }

    @Bean
    public TransactionDao transactionDao(DataSource dataSource) {
        return new TransactionDaoImpl(dataSource);
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<AccountSummary> accountSummaryFileWriter(
            @Value("#{jobParameters['summaryFile']}") Resource summaryFile) {

        DelimitedLineAggregator<AccountSummary> lineAggregator = new DelimitedLineAggregator<>();
        BeanWrapperFieldExtractor<AccountSummary> fieldExtractor = new BeanWrapperFieldExtractor<>();

        fieldExtractor.setNames(new String[]{"accountNumber", "currentBalance"});
        fieldExtractor.afterPropertiesSet();
        lineAggregator.setFieldExtractor(fieldExtractor);

        return new FlatFileItemWriterBuilder<AccountSummary>()
                .name("accountSummaryFileWriter")
                .resource(summaryFile)
                .lineAggregator(lineAggregator)
                .build();
    }

    @Bean
    public Step generateAccountSummaryStep() {
        return this.stepBuilderFactory.get("generateAccountSummaryStep")
                .<AccountSummary, AccountSummary>chunk(50)
                .reader(this.accountSummaryReader(null))
                .writer(this.accountSummaryFileWriter(null))
                .build();
    }

    @Bean
    public Job transactionJob() {
        return this.jobBuilderFactory.get("transactionJob")
                .incrementer(new RunIdIncrementer())
               //.preventRestart()
                .start(this.importTransactionFileStep())//.on("STOPPED")
                .next(this.applyTransactionStep())
                .next(this.generateAccountSummaryStep())
                /*.stopAndRestart(this.importTransactionFileStep())
                .from(this.importTransactionFileStep()).on("*").to(this.applyTransactionStep())
                .from(this.applyTransactionStep()).next(this.generateAccountSummaryStep())
                .end()*/
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(TransactionProcessingApplication.class, args);
    }
}
