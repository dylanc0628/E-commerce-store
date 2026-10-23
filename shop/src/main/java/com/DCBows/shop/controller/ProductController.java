package com.DCBows.shop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.DCBows.shop.domain.Product;
import com.DCBows.shop.services.ProductService;

@RestController 
@RequestMapping("/products")
public class ProductController {
    
    @Autowired 
    private ProductService productService;

    @PostMapping("/addProduct")
    public Product addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    @GetMapping 
    public List<Product> findAllProducts() {
        return productService.findAllProducts();
    }

    @GetMapping("/productById/{id}")
    public Product findProductById(@PathVariable long id) throws Exception {
        return productService.findProductById(id);
    }

    @PutMapping("/update/{id}")
    public Product updateProduct(@PathVariable long id, @RequestBody Product product) throws Exception {
        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteProduct(@PathVariable long id) throws Exception {
        productService.deleteProduct(id);
    }
}
