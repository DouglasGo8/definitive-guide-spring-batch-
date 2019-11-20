package com.apress.springbatch.multiple.format.domain;

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
                "firstName='" + firstName + '\'' +
                ", middleInitial='" + middleInitial + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                '}';
    }

}
