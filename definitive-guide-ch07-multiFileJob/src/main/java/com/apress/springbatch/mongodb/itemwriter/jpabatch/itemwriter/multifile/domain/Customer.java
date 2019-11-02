package com.apress.springbatch.mongodb.itemwriter.jpabatch.itemwriter.multifile.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author dbatista
 */
@Data
@NoArgsConstructor
public class Customer {

    private String firstName;
    private String middleInitial;
    private String lastName;

    private String address;
    private String city;
    private String state;
    private String zipCode;

    private List<Transaction> transactions;

}
