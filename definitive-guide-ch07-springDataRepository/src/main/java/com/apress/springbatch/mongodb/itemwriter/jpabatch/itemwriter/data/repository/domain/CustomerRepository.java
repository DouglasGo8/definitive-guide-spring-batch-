package com.apress.springbatch.mongodb.itemwriter.jpabatch.itemwriter.data.repository.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;


/**
 *
 */
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {
    Page<Customer> findByCity(String city, Pageable pageRequest);
}
