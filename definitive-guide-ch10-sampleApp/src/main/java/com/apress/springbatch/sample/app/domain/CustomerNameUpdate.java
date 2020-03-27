package com.apress.springbatch.sample.app.domain;

import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public class CustomerNameUpdate extends CustomerUpdate {


    private final String firstName;
    private final String middleName;
    private final String lastName;

    public CustomerNameUpdate(long customerId, String firstName, String middleName, String lastName) {
        super(customerId);
        this.firstName = StringUtils.hasText(firstName) ? firstName : null;
        this.middleName = StringUtils.hasText(middleName) ? middleName : null;
        this.lastName = StringUtils.hasText(lastName) ? lastName : null;
    }


    @Override
    public String toString() {
        return "CustomerNameUpdate{" +
                "firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", customerId=" + super.getCustomerId() +
                '}';
    }

}
