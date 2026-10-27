package com.DCBows.shop;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
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

    @Test
    void findByIdWhenMissingReturnsEmpty() {
        assertThat(productRepository.findById(999999L)).isEmpty();
    }

    @Test 
    void findAllReturnsSavedProducts() {
        productRepository.save(newProduct("Recurve bow", "Recurve", "199.99"));
        productRepository.save(newProduct("Carbon arrow", "Arrow", "49.99"));

        assertThat(productRepository.findAll())
            .extracting(Product::getName)
            .contains("Recurve bow", "Carbon arrow");
    }

    @Test 
    void saveExistingProductUpdatesRow() {
        Product saved = productRepository.save(newProduct("Recurve bow", "Recurve", "199.99"));

        saved.setPrice(new BigDecimal("159.99"));
        productRepository.save(saved);

        Product reloaded = productRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getPrice()).isEqualByComparingTo("159.99");
    }

    @Test 
    void deleteRemovesRow() {
        Long id = productRepository.save(newProduct("Recurve bow", "Recurve", "199.99")).getId();
        
        productRepository.deleteById(id);

        assertThat(productRepository.existsById(id)).isFalse();
    }

    @Test 
    void nullPriceVioldatesNotNullConstraint() {
        assertThatThrownBy(() -> productRepository.save(newProduct("Bow", "Recurve", null)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test 
    void nonPositivePriceViolatesCheckConstraint() {
        assertThatThrownBy(() -> productRepository.save(newProduct("Bow", "Recurve", "0.00")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
