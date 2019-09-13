package com.apress.springbatch.hibernate.cursor.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author dbatista
 */
@Data
@Entity()
@Table(name = "TBL_CUSTOMER")
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
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
