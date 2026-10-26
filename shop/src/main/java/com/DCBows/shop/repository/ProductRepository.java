package com.DCBows.shop.repository;

import org.springframework.data.repository.ListCrudRepository;

import com.DCBows.shop.domain.Product;

public interface ProductRepository extends ListCrudRepository<Product, Long>{
    
}
