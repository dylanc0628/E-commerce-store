package com.DCBows.shop;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest 
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class ProductApiIntegrationTest {
    
    private static final String BOW = """
            {"name":"Recurve bow","type":"Recurve","price":199.99}""";

    @Autowired 
    private MockMvc mockMvc;

    private long createProduct(String json) throws Exception {
        String response = mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        Number id = JsonPath.read(response, "$.id");
        return id.longValue();
    }

    @Test 
    void createReturns201AndProductCanBeFetched() throws Exception {
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(BOW))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", containsString("/products/")))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name", is("Recurve bow")))
            .andExpect(jsonPath("$.type", is("Recurve")))
            .andExpect(jsonPath("$.price", is(199.99)));
        
        long id = createProduct(BOW);

        mockMvc.perform(get("/products/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is((int) id)))
            .andExpect(jsonPath("$.name", is("Recurve bow")))
            .andExpect(jsonPath("$.type", is("Recurve")))
            .andExpect(jsonPath("$.price", is(199.99)));
    }

    @Test 
    void createIgnoresClientSuppliedId() throws Exception {
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"id":999999,"name":"Recurve bow","type":"Recurve","price":199.99}"""))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", not(999999)));
    }

    @Test 
    void listReturnsCreatedProducts() throws Exception {
        createProduct(BOW);
        createProduct("""
                {"name":"Carbon arrow","type":"Arrow","price":49.99}""");
        
        mockMvc.perform(get("/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[*].name", hasItems("Recurve bow", "Carbon arrow")));
    }

    @Test 
    void updateChangesPersistedValues() throws Exception {
        long id = createProduct(BOW);

        mockMvc.perform(put("/products/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name":"Black Hunter Recurve","type":"Recurve","price":159.99}"""))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is((int) id)))
            .andExpect(jsonPath("$.name", is("Black Hunter Recurve")))
            .andExpect(jsonPath("$.type", is("Recurve")))
            .andExpect(jsonPath("$.price", is(159.99)));

        mockMvc.perform(get("/products/" + id))
            .andExpect(jsonPath("$.name", is("Black Hunter Recurve")))
            .andExpect(jsonPath("$.type", is("Recurve")))
            .andExpect(jsonPath("$.price", is(159.99)));
    }

    @Test
    void deleteRemovesProduct() throws Exception {
        long id = createProduct(BOW);

        mockMvc.perform(delete("/products/" + id))
            .andExpect(status().isNoContent());
        
        mockMvc.perform(get("/products/" + id))
            .andExpect(status().isNotFound());
    }

    @Test 
    void getMissingProductReturns404() throws Exception {
        mockMvc.perform(get("/products/999999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.detail", is("Product not found: 999999")));
    }

    @Test 
    void updateMissingProductReturns404() throws Exception {
        mockMvc.perform(put("/products/999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(BOW))
            .andExpect(status().isNotFound());
    }

    @Test 
    void deletMissingProductReturns404() throws Exception {
        mockMvc.perform(delete("/products/999999"))
            .andExpect(status().isNotFound());
    }

    @Test 
    void invalidBodyReturns400() throws Exception {
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name":"","type":"Recurve","price":-1}"""))
            .andExpect(status().isBadRequest());
    }
}
