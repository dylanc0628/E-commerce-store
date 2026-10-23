package com.DCBows.shop.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.DCBows.shop.domain.Product;
import com.DCBows.shop.repository.ProductRepository;

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
        return (List<Product>) productRepository.findAll();
    }

    @Transactional(readOnly=true)
    public Product findProductById(Long Id) throws Exception {
        return productRepository.findById(Id)
            .orElseThrow(() -> new Exception("Product not found: " + Id));
    }

    public Product updateProduct(Long id, Product updates) throws Exception {
        Product product = findProductById(id);
        product.setName(updates.getName());
        product.setType(updates.getType());
        product.setPrice(updates.getPrice());
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) throws Exception {
        Product product = findProductById(id);
        productRepository.delete(product);
    }
}
