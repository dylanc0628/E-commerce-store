package com.DCBows.shop.repository;

import org.springframework.data.repository.CrudRepository;

import com.DCBows.shop.domain.Product;

public interface ProductRepository extends CrudRepository<Product, Long>{
    
}
