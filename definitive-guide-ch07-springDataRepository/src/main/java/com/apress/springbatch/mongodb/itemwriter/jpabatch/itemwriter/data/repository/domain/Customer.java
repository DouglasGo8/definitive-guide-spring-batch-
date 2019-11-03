package com.apress.springbatch.mongodb.itemwriter.jpabatch.itemwriter.data.repository.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author dbatista
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "TBL_CUSTOMER_READER")
public class Customer {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "zipcode")
    private String zipCode;

    @Column(name = "address")
    private String address;

    @Column(name = "lastname")
    private String lastName;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "middleinitial")
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
