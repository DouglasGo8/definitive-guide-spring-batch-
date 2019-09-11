package com.bank.fixedlength.cassandra.domain;


import com.datastax.driver.core.DataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;


/**
 * @author dbatista
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("TBL_PATRIMONY")
public class Patrimony {

    @PrimaryKey
    @Column("ssn")
    private String ssn;

    @Column("py_balance")
    @CassandraType(type = DataType.Name.DOUBLE)
    private double pyBalance;

}
