package com.DCBows.shop;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.DCBows.shop.domain.Product;

@SpringBootTest
public class ProductTest {
    
    @Test
    void constructorSetsAllFields() {
        Product product = new Product(1L, "Bow", "Recurve", new BigDecimal("199.99"));

        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo("Bow");
        assertThat(product.getType()).isEqualTo("Recurve");
        assertThat(product.getPrice()).isEqualByComparingTo("199.99");
    }

    @Test
    void settersUpdateFields() {
        Product product = new Product();

        product.setId(2L);
        product.setName("Arrow");
        product.setType("Carbon");
        product.setPrice(new BigDecimal("49.99"));

        assertThat(product.getId()).isEqualTo(2L);
        assertThat(product.getName()).isEqualTo("Arrow");
        assertThat(product.getType()).isEqualTo("Carbon");
        assertThat(product.getPrice()).isEqualByComparingTo("49.99");
    }
}
