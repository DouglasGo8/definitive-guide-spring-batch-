package com.apress.springbatch.jpabatch.itemwriter.custom.input.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dbatista
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    private Long id;

    private String city;
    private String state;
    private String street;
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
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                '}';

    }
}
