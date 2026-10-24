package com.DCBows.shop;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.BDDMockito.willThrow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.DCBows.shop.controller.ProductController;
import com.DCBows.shop.domain.Product;
import com.DCBows.shop.services.ProductService;

import jakarta.servlet.ServletException;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(ProductController.class) 
@AutoConfigureMockMvc(addFilters=false)
public class ProductControllerTest {
    
    @Autowired 
    private MockMvc mockMvc;

    @MockitoBean 
    private ProductService productService;

    @Autowired 
    private ObjectMapper objectMapper;

    private Product bow;
    private Product arrow;

    @BeforeEach
    void setUp() {
        bow = new Product(1L, "Recurve bow", "Recurve", "199.99");
        arrow = new Product(2L, "Carbon arrow", "Arrow", "49.99");
    }

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

    @Test 
    void findProductByIdWhenFoundReturnsProduct() throws Exception {
        given(productService.findProductById(1L)).willReturn(bow);

        mockMvc.perform(get("/products/productById/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Recurve bow")))
                .andExpect(jsonPath("$.type", is("Recurve")))
                .andExpect(jsonPath("$.price", is(199.99)));
    }

    @Test 
    void findProductByIdWhenNotFoundThrowsException() throws Exception {
        given(productService.findProductById(999_999L))
                .willThrow(new Exception("Product not found: 999999"));

        ServletException thrown = assertThrows(ServletException.class, 
            () -> mockMvc.perform(get("/products/productById/999999")));

        assertEquals("Product not found: 999999", thrown.getCause().getMessage());
    }

    @Test 
    void findProductByIdWithNonNumericIdReturns400() throws Exception {
        mockMvc.perform(get("/products/productById/abc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test 
    void testAddProduct() throws Exception {
        Product newBow = new Product(3L, "Compound bow", "Compound", "999.99");
        given(productService.addProduct(any(Product.class))).willReturn(newBow);

        mockMvc.perform(post("/products/addProduct")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBow)))
                .andExpect(status().isOk())
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
    void addProductWithMalformedJsonReturns400() throws Exception {
        mockMvc.perform(post("/products/addProduct")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{not valid json"))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test 
    void addProductWithNonNumericPriceReturns400() throws Exception {
        String body = "{\"name\":\"Compound bow\",\"type\":\"Compound\",\"price\":\"free\"}";

        mockMvc.perform(post("/products/addProduct")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
        
        verifyNoInteractions(productService);
    }

    @Test 
    void addProductWithUnsupportedMediaTypeReturns415() throws Exception {
        mockMvc.perform(post("/products/addProduct")
                .contentType(MediaType.TEXT_PLAIN)
                .content("Compound bow"))
            .andExpect(status().isUnsupportedMediaType());

        verifyNoInteractions(productService);
    }

    @Test 
    void updateProductWhenFoundReturnsUpdatedProduct() throws Exception {
        Product update = new Product(null, "Black Hunter Recurve", "Recurve", "159.99");
        Product updated = new Product(1L, "Black Hunter Recurve", "Recurve", "159.99");
        given(productService.updateProduct(eq(1L), any(Product.class))).willReturn(updated);

        mockMvc.perform(put("/products/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
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
    void updateProductWhenNotFoundThrowsException() throws Exception {
        Product update = new Product(null, "Ghost", "Recurve", "1.00");

        given(productService.updateProduct(eq(999_999L), any(Product.class)))
                .willThrow(new Exception("Product not found: 999999"));
        
        ServletException thrown = assertThrows(ServletException.class,
            () -> mockMvc.perform(put("/products/update/999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update))));
        
        assertEquals("Product not found: 999999", thrown.getCause().getMessage());
    }

    @Test
    void updateProductWithMalformedJsonReturns400() throws Exception {
        mockMvc.perform(put("/products/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{not valid json}"))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test
    void deleteProductWhenFoundReturns200() throws Exception {
        mockMvc.perform(delete("/products/delete/1"))
                .andExpect(status().isOk());

        verify(productService).deleteProduct(1L);
    }

    @Test
    void deleteProducteWhenNotFoundThrowsException() throws Exception {
        willThrow(new Exception("Product not found: 999999"))
            .given(productService).deleteProduct(999_999L);

        ServletException thrown = assertThrows(ServletException.class, 
            () -> mockMvc.perform(delete("/products/delete/999999")));

        assertEquals("Product not found: 999999", thrown.getCause().getMessage());
    }

    @Test
    void deleteProductWithNonNumericIdReturns400() throws Exception {
        mockMvc.perform(delete("/products/delete/abc"))
            .andExpect(status().isBadRequest());
        

        verifyNoInteractions(productService);
    }
}
