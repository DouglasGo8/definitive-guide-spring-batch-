package com.apress.springbatch.methodinvokingtasklet.service;

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
