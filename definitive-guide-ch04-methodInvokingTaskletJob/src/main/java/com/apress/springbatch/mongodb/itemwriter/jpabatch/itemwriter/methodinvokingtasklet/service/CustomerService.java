package com.apress.springbatch.mongodb.itemwriter.jpabatch.itemwriter.methodinvokingtasklet.service;

import org.springframework.stereotype.Service;

/**
 * @author dbatista
 */
@Service
public class CustomerService {

    /**
     *
     * @param message from JobParameter
     */
    public void serviceMethod(String message) {
        System.out.println(String.format("Got your param %s", message));
    }
}
