package com.DCBows.shop.dto;

import java.math.BigDecimal;

import com.DCBows.shop.domain.Product;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown=true)
public record  ProductRequest(
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Size(max = 199) String type,
        @NotNull @DecimalMin(value = "0.00", inclusive=false) @Digits(integer=8, fraction=2) BigDecimal price) {
    
    public Product toProduct() {
        return new Product(null, name, type, price);
    }
}
