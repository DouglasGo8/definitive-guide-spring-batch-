package com.apress.springbatch.parallelsteps.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@Data
@NoArgsConstructor
@XmlRootElement(name = "transaction")
public class Transaction {

    private String accountId;
    private double amount;
    private Date timestamp;
}
