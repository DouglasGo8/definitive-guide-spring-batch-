package com.apress.springbatch.repository.domain;

import org.springframework.data.repository.CrudRepository;

/**
 * @author dbatista
 */
public interface CustomerRepository extends CrudRepository<Customer, Long> {
}
