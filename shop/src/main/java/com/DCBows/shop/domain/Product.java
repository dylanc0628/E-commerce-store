package com.DCBows.shop.domain;

import java.math.BigDecimal;

public class Product {
    private Long id;
    private String name;
    private String type;
    private BigDecimal price;

    public Product() {};

    public Product(Long id, String name, String type, String price) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = new BigDecimal(price);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
