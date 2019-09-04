package com.apress.springbatch.delimited.tokenize.batch;

import org.springframework.batch.item.file.transform.DefaultFieldSetFactory;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.FieldSetFactory;
import org.springframework.batch.item.file.transform.LineTokenizer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dbatista
 */
public class CustomerFileLineTokenizer implements LineTokenizer {

    private final String[] names = new String[]{"firstName",
            "middleInitial",
            "lastName",
            "addressNumber",
            "city",
            "state",
            "zipCode"};

    private FieldSetFactory fieldSetFactory = new DefaultFieldSetFactory();

    @Override
    public FieldSet tokenize(String record) {

        String[] fields = record.split(",");

        final List<String> parsedFields = new ArrayList<String>();

        for (int i = 0; i < fields.length; i++) {
            if (i == 4) {
                parsedFields.set(i - 1, parsedFields.get(i - 1) + " " + fields[i]);
            } else {
                parsedFields.add(fields[i]);
            }
        }

        return fieldSetFactory.create(parsedFields.toArray(new String[0]), names);
    }
}
