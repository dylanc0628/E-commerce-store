package com.DCBows.shop;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.DCBows.shop.domain.Product;
import com.DCBows.shop.repository.ProductRepository;

@SpringBootTest 
@Transactional
public class ProductRepositoryTest {
    
    @Autowired 
    private ProductRepository productRepository;

    private Product newProduct(String name, String type, String price) {
        return new Product(null, name, type, price == null ? null : new BigDecimal(price));
    }

    @Test 
    void saveAssignsGeneratedId() {
        Product saved = productRepository.save(newProduct("Recurve bow", "Recurve", "199.99"));

        assertThat(saved.getId()).isNotNull();
    }

    @Test 
    void findByIdReturnsPersistedProduct() {
        Long id = productRepository.save(newProduct("Recurve bow", "Recurve", "199.99")).getId();

        Product found = productRepository.findById(id).orElseThrow();

        assertThat(found.getName()).isEqualTo("Recurve bow");
        assertThat(found.getType()).isEqualTo("Recurve");
        assertThat(found.getPrice()).isEqualByComparingTo("199.99");
    }
}
