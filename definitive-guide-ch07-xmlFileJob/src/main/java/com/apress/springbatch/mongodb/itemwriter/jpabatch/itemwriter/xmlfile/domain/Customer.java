package com.apress.springbatch.mongodb.itemwriter.jpabatch.itemwriter.xmlfile.domain;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author dbatista
 */
@Getter
@XmlRootElement
public class Customer {

    @Setter
    private String firstName;
    @Setter
    private String middleInitial;
    @Setter
    private String lastName;
    @Setter
    private String address;
    @Setter
    private String city;
    @Setter
    private String state;
    @Setter
    private String zipCode;
    
    private List<Transaction> transactions;

    @XmlElementWrapper(name = "transactions")
    @XmlElement(name = "transaction")
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        final StringBuilder output = new StringBuilder();

        output.append(firstName);
        output.append(" ");
        output.append(middleInitial);
        output.append(". ");
        output.append(lastName);

        if (transactions != null && transactions.size() > 0) {
            output.append(" has ");
            output.append(transactions.size());
            output.append(" transactions.");
        } else {
            output.append(" has no transactions.");
        }

        return output.toString();
    }
}