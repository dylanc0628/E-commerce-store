package com.DCBows.shop;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

import com.DCBows.shop.domain.Product;
import com.DCBows.shop.exception.ProductNotFoundException;
import com.DCBows.shop.repository.ProductRepository;
import com.DCBows.shop.services.ProductService;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;

    @InjectMocks 
    private ProductService productService;

    private Product bow() {
        return new Product(1L, "Recurve bow", "Recurve", new BigDecimal("199.99"));
    }

    @Test 
    void addProductSavesAndReturnsProduct() {
        Product input = new Product(null, "Recurve bow", "Recurve", new BigDecimal("199.99"));
        given(productRepository.save(input)).willReturn(bow());

        Product result = productService.addProduct(input);

        assertThat(result.getId()).isEqualTo(1L);
        verify(productRepository).save(input);
    }

    @Test 
    void findAllProductsReturnsRepositoryResults() {
        given(productRepository.findAll()).willReturn(List.of(bow()));

        assertThat(productService.findAllProducts()).hasSize(1);
    }

    @Test 
    void findProductByIdWhenFoundReturnsProduct() {
        given(productRepository.findById(1L)).willReturn(Optional.of(bow()));

        assertThat(productService.findProductById(1L).getName()).isEqualTo("Recurve bow");
        assertThat(productService.findProductById(1L).getType()).isEqualTo("Recurve");
        assertThat(productService.findProductById(1L).getPrice()).isEqualByComparingTo("199.99");
    }

    @Test
    void findProductByIdWhenMissingThrowsNotFound() {
        given(productRepository.findById(999999L)).willReturn(Optional.empty());
        
        assertThatThrownBy(() -> productService.findProductById(999999L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product not found: 999999");
    }

    @Test 
    void updateProductCopiesFieldsAndKeepsId() {
        given(productRepository.findById(1L)).willReturn(Optional.of(bow()));
        given(productRepository.save(any(Product.class))).willAnswer(invocation -> invocation.getArgument(0));
        
        Product updates = new Product(null, "Black Hunter Recurve", "Recurve", new BigDecimal("249.99"));
        Product result = productService.updateProduct(1L, updates);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Black Hunter Recurve");
        assertThat(result.getPrice()).isEqualByComparingTo("249.99");
    }

    @Test
    void updateProductWhenMissingThrowsAndNeverSaves() {
        given(productRepository.findById(999999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProduct(999999L, bow()))
                .isInstanceOf(ProductNotFoundException.class);
        
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test 
    void deleteProductWhenFoundDeletesById() {
        given(productRepository.existsById(1L)).willReturn(true);

        productService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test 
    void deleteProductWhenMissingThrowsAndNeverDeletes() {
        given(productRepository.existsById(999999L)).willReturn(false);

        assertThatThrownBy(() -> productService.deleteProduct(999999L))
                .isInstanceOf(ProductNotFoundException.class);
        
        verify(productRepository, never()).deleteById(any(Long.class));
    }
}
