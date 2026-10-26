package com.DCBows.shop.dto;

import java.math.BigDecimal;

import com.DCBows.shop.domain.Product;

public record ProductResponse(
        Long id, 
        String name,
        String type,
        BigDecimal price) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getType(), product.getPrice());
    }
}
