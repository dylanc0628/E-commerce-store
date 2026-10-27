package com.DCBows.shop;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willThrow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import com.DCBows.shop.controller.ProductController;
import com.DCBows.shop.domain.Product;
import com.DCBows.shop.exception.ProductNotFoundException;
import com.DCBows.shop.services.ProductService;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductControllerTest {
    
    @Autowired 
    private MockMvc mockMvc;

    @MockitoBean 
    private ProductService productService;

    private Product bow;
    private Product arrow;

    @BeforeEach
    void setUp() {
        bow = new Product(1L, "Recurve bow", "Recurve", new BigDecimal("199.99"));
        arrow = new Product(2L, "Carbon arrow", "Arrow", new BigDecimal("49.99"));
    }

    static Stream<String> invalidProductBodies() {
        return Stream.of(
            """
            {"name":"","type":"Recurve","price":10.00}""",
            """
            {"type":"Recurve","price":10.00}""",
            """
            {"name":"Bow","price":10.00}""",
            """
            {"name":"Bow","type":"Recurve"}""",
            """
            {"name":"Bow","type":"Recurve","price":0}""",
            """
            {"name":"Bow","type":"Recurve","price":-5.00}""",
            """
            {"name":"Bow","type":"Recurve","price":10.999}""");
    }

    //Get all products
    @Test 
    void getAllProductsReturnsListOfProducts() throws Exception {
        given(productService.findAllProducts()).willReturn(List.of(bow, arrow));

        mockMvc.perform(get("/products"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].name", is("Recurve bow")))
            .andExpect(jsonPath("$[0].type", is("Recurve")))
            .andExpect(jsonPath("$[0].price", is(199.99)))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[1].name", is("Carbon arrow")))
            .andExpect(jsonPath("$[1].type", is("Arrow")))
            .andExpect(jsonPath("$[1].price", is(49.99)));

        verify(productService).findAllProducts();
    }

    @Test 
    void getAllProductsWhenEmptyReturnsEmptyList() throws Exception {
        given(productService.findAllProducts()).willReturn(List.of());

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        
        verify(productService).findAllProducts();
    }

    //Get individual products
    @Test 
    void findProductByIdWhenFoundReturnsProduct() throws Exception {
        given(productService.findProductById(1L)).willReturn(bow);

        mockMvc.perform(get("/products/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("Recurve bow")))
            .andExpect(jsonPath("$.type", is("Recurve")))
            .andExpect(jsonPath("$.price", is(199.99)));
    }

    @Test 
    void findProductByIdWhenNotFoundReturns404() throws Exception {
        given(productService.findProductById(999999L)).willThrow(new ProductNotFoundException(999999L));

        mockMvc.perform(get("/products/999999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.detail", is("Product not found: 999999")));
    }

    @Test 
    void findProductByIdWithNonNumericIdReturns400() throws Exception {
        mockMvc.perform(get("/products/abc"))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    //Post products
    @Test 
    void addProductReturns201WithLocationAndBody() throws Exception {
        Product saved = new Product(3L, "Compound bow", "Compound", new BigDecimal("999.99"));
        given(productService.addProduct(any(Product.class))).willReturn(saved);

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"Compound bow","type":"Compound","price":999.99}"""))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", endsWith("/products/3")))
            .andExpect(jsonPath("$.id", is(3)))
            .andExpect(jsonPath("$.name", is("Compound bow")))
            .andExpect(jsonPath("$.type", is("Compound")))
            .andExpect(jsonPath("$.price", is(999.99)));

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productService).addProduct(captor.capture());
        Product passed = captor.getValue();
        assertEquals("Compound bow", passed.getName());
        assertEquals("Compound", passed.getType());
        assertEquals(0, new BigDecimal("999.99").compareTo(passed.getPrice()));
    }

    @Test 
    void addProductIgnoresClientSuppliedId() throws Exception {
        Product saved = new Product(3L, "Compound bow", "Compound", new BigDecimal("999.99"));
        given(productService.addProduct(any(Product.class))).willReturn(saved);

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"id":42,"name":"Compound bow","type":"Compound","price":999.99}"""))
            .andExpect(status().isCreated());
        
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productService).addProduct(captor.capture());
        assertEquals(null, captor.getValue().getId());
    }

    @Test 
    void addProductWithMalformedJsonReturns400() throws Exception {
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{not valid json}"))
            .andExpect(status().isBadRequest());
        
        verifyNoInteractions(productService);
    }

    @Test
    void addProductWithNonNumericPriceReturns400() throws Exception {
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"Compound bow","type":"Compound","price":"free"}"""))
            .andExpect(status().isBadRequest());
        
        verifyNoInteractions(productService);
    }

    @ParameterizedTest 
    @MethodSource("invalidProductBodies")
    void addProductWithInvalidFieldsReturns400(String body) throws Exception {
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
        
        verifyNoInteractions(productService);
    }

    @Test 
    void addProductWithUnsupportedMediaTypeReturns415() throws Exception {
        mockMvc.perform(post("/products")
                .contentType(MediaType.TEXT_PLAIN)
                .content("Compound bow"))
            .andExpect(status().isUnsupportedMediaType());

        verifyNoInteractions(productService);
    }

    //Put products
    @Test 
    void updateProductWhenFoundReturnsUpdatedProduct() throws Exception {
        Product updated = new Product(1L, "Black Hunter Recurve", "Recurve", new BigDecimal("159.99"));
        given(productService.updateProduct(eq(1L), any(Product.class))).willReturn(updated);

        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"Black Hunter Recurve","type":"Recurve","price":159.99}"""))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("Black Hunter Recurve")))
            .andExpect(jsonPath("$.type", is("Recurve")))
            .andExpect(jsonPath("$.price", is(159.99)));

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productService).updateProduct(eq(1L), captor.capture());
        assertEquals("Black Hunter Recurve", captor.getValue().getName());
        assertEquals(0, new BigDecimal("159.99").compareTo(captor.getValue().getPrice()));
    }

    @Test 
    void updateProductWhenNotFoundReturns404() throws Exception {
        given(productService.updateProduct(eq(999999L), any(Product.class)))
            .willThrow(new ProductNotFoundException(999999L));
        
        mockMvc.perform(put("/products/999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name":"Ghost","type":"Recurve","price":1.00}"""))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.detail", is("Product not found: 999999")));
    }

    @Test 
    void updateProductWithMalformedJsonReturns400() throws Exception {
        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{not valid json}"))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @ParameterizedTest 
    @MethodSource("invalidProductBodies")
    void updateProductWithInvalidFieldsReturns400(String body) throws Exception {
        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
        
        verifyNoInteractions(productService);
    }

    //Delete products
    @Test 
    void deleteProductWhenFoundReturns204() throws Exception {
        mockMvc.perform(delete("/products/1"))
            .andExpect(status().isNoContent());
        
        verify(productService).deleteProduct(1L);
    }

    @Test 
    void deleteProductWhenNotFoundReturns404() throws Exception {
        willThrow(new ProductNotFoundException(999999L))
            .given(productService).deleteProduct(999999L);
        
        mockMvc.perform(delete("/products/999999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.detail", is("Product not found: 999999")));
    }

    @Test 
    void deleteProductWithNonNumericIdReturns400() throws Exception {
        mockMvc.perform(delete("/products/abc"))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }
}
