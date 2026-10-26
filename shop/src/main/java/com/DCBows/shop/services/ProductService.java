package com.DCBows.shop.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.DCBows.shop.domain.Product;
import com.DCBows.shop.repository.ProductRepository;
import com.DCBows.shop.exception.ProductNotFoundException;

@Service 
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional(readOnly=true)
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly=true)
    public Product findProductById(long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public Product updateProduct(Long id, Product updates) {
        Product product = findProductById(id);
        product.setName(updates.getName());
        product.setType(updates.getType());
        product.setPrice(updates.getPrice());
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }
}
