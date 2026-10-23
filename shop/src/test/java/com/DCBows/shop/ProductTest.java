package com.DCBows.shop;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.DCBows.shop.domain.Product;

@SpringBootTest
public class ProductTest {
    
    @Test 
    void productMethodsWork() {
        Product newProduct = new Product(1L, "Bow", "Recurve", "199.99");

        assertThat(newProduct.getId()).isEqualTo(1);
        assertThat(newProduct.getName()).isEqualTo("Bow");
        assertThat(newProduct.getType()).isEqualTo("Recurve");
        assertThat(newProduct.getPrice()).isInstanceOf(BigDecimal.class);
        assertThat((BigDecimal) newProduct.getPrice()).isEqualByComparingTo(new BigDecimal("199.99"));
    }    
}
