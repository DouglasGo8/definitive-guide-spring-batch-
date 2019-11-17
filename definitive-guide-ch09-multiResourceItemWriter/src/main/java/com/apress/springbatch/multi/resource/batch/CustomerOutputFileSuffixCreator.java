package com.apress.springbatch.multi.resource.batch;

import org.springframework.batch.item.file.ResourceSuffixCreator;
import org.springframework.stereotype.Component;

/**
 * @author dbatista
 */
@Component
public class CustomerOutputFileSuffixCreator implements ResourceSuffixCreator {

    @Override
    public String getSuffix(int i) {
        return i + ".xml";
    }
}
