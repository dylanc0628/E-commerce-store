package com.DCBows.shop.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.DCBows.shop.domain.Product;
import com.DCBows.shop.dto.ProductRequest;
import com.DCBows.shop.dto.ProductResponse;
import com.DCBows.shop.services.ProductService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/products")
public class ProductController {
    
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> addProduct(@Valid @RequestBody ProductRequest request) {
        Product saved = productService.addProduct(request.toProduct());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(saved.getId())
            .toUri();
        return ResponseEntity.created(location).body(ProductResponse.from(saved));
    }

    @GetMapping 
    public List<ProductResponse> findAllProducts() {
        return productService.findAllProducts().stream()
            .map(ProductResponse::from)
            .toList();
    }

    @GetMapping("/{id}")
    public ProductResponse findProductById(@PathVariable long id) {
        return ProductResponse.from(productService.findProductById(id));
    }

    @PutMapping("/{id}")
    public ProductResponse updateProduct(@PathVariable long id, @Valid @RequestBody ProductRequest request) {
        return ProductResponse.from(productService.updateProduct(id, request.toProduct()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable long id) {
        productService.deleteProduct(id);
    }
}
