package com.apress.springbatch.jpabatch.itemwriter.existing.service.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dbatista
 */
@Data
@NoArgsConstructor

public class Customer {


    private Long id;
    private String city;
    private String state;
    private String zipCode;
    private String address;
    private String lastName;
    private String firstName;
    private String middleInitial;

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", middleInitial='" + middleInitial + '\'' +
                ", lastName='" + lastName + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                '}';

    }

}
