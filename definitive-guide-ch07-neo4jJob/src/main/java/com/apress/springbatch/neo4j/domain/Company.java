package com.apress.springbatch.neo4j.domain;

import lombok.Data;
import org.neo4j.ogm.annotation.Property;
import org.springframework.data.neo4j.annotation.QueryResult;

@Data
@QueryResult
public class Company {

    @Property("s.companyName")
    private String company;
    @Property("c.categoryName")
    private String categoryName;

    @Override
    public String toString() {
        return "Company{" +
                "company='" + company + '\'' +
                "categoryName='" + categoryName + '\'' +
//				", categories=" + categories.stream().collect(Collectors.joining(",")) +
                '}';
    }
}
