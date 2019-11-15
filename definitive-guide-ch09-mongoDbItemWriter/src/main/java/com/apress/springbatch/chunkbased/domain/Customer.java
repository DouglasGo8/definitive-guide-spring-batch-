package com.apress.springbatch.chunkbased.domain;


import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * @author dbatista
 */
@Data
public class Customer implements Serializable {

    @Id
    private String id; // Must be string in Mongodb
    private String firstName;
    private String middleInitial;
    private String lastName;
    private String address;
    private String city;
    private String state;
    private String zip;

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
                ", zip='" + zip + '\'' +
                '}';
    }
}
