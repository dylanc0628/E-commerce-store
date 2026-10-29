package com.DCBows.shop.repository;

import org.springframework.data.repository.ListCrudRepository;

import com.DCBows.shop.domain.Customer;

public interface CustomerRepository extends ListCrudRepository<Customer, Long> {
    
}
