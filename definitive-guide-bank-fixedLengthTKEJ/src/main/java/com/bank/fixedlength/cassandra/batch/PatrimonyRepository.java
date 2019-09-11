package com.bank.fixedlength.cassandra.batch;

import com.bank.fixedlength.cassandra.domain.Patrimony;
import org.springframework.data.cassandra.repository.CassandraRepository;

/**
 *
 */
public interface PatrimonyRepository extends CassandraRepository<Patrimony, String> {
}
