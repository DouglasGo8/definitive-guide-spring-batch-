package com.apress.springbatch.schedule.transaction.process.batch;

import com.apress.springbatch.schedule.transaction.process.domain.Transaction;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.transform.FieldSet;

/**
 * @author dbatista
 */
public class TransactionReader implements ItemStreamReader<Transaction> {

    private int recordCount = 0;
    private int expectedRecordCount = 0;
    private StepExecution stepExecution;
    private ItemStreamReader<FieldSet> fieldSetReader;

    public TransactionReader(ItemStreamReader<FieldSet> fieldSetReader) {
        this.fieldSetReader = fieldSetReader;
    }

    @Override
    public Transaction read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        return this.process(fieldSetReader.read());
    }

    @AfterStep
    public ExitStatus afterStep(StepExecution execution) {

        //System.out.println(recordCount);
        //System.out.println(expectedRecordCount);

        if (recordCount == expectedRecordCount) {
            return execution.getExitStatus();
        } else {
            return ExitStatus.STOPPED;
        }
    }

    private Transaction process(FieldSet fieldSet) {

        /*if (this.recordCount == 25) {
            throw new ParseException("This isn't what I hoped to happen");
        }*/


        Transaction transactionLineField = null;

        if (fieldSet != null) {
            if (fieldSet.getFieldCount() > 1) {
                transactionLineField = new Transaction();
                transactionLineField.setAccountNumber(fieldSet.readString(0));
                transactionLineField.setTimestamp(fieldSet.readDate(1, "yyyy-MM-DD HH:mm:ss"));
                transactionLineField.setAmount(fieldSet.readDouble(2));
                recordCount++;
            } else {
                expectedRecordCount = fieldSet.readInt(0);
            }
        }

        //System.out.println(recordCount + '/' + expectedRecordCount);
        return transactionLineField;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.fieldSetReader.open(executionContext);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        this.fieldSetReader.update(executionContext);
    }

    @Override
    public void close() throws ItemStreamException {
        this.fieldSetReader.close();
    }

    public void setFieldSetReader(ItemStreamReader<FieldSet> fieldSetReader) {
        this.fieldSetReader = fieldSetReader;
    }
}
