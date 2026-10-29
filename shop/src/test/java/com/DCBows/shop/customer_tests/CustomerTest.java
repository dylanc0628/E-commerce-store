package com.DCBows.shop.customer_tests;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.DCBows.shop.domain.Customer;

@SpringBootTest
public class CustomerTest {
    
    @Test 
    void constructorSetsAllFields() {
        Customer customer = new Customer(1L, "John", "Smith", "jsmith@hotmail.com", "1234");

        assertThat(customer.getId()).isEqualTo(1L);
        assertThat(customer.getFirstName()).isEqualTo("John");
        assertThat(customer.getLastName()).isEqualTo("Smith");
        assertThat(customer.getEmail()).isEqualTo("jsmith@hotmail.com");
        assertThat(customer.getPassword()).isEqualTo("1234");
    }

    @Test 
    void settersUpdateFields() {
        Customer customer = new Customer();

        customer.setId(2L);
        customer.setFirstName("James");
        customer.setLastName("Doe");
        customer.setEmail("jdoe@gmail.com");
        customer.setPassword("9876");

        assertThat(customer.getId()).isEqualTo(2L);
        assertThat(customer.getFirstName()).isEqualTo("James");
        assertThat(customer.getLastName()).isEqualTo("Doe");
        assertThat(customer.getEmail()).isEqualTo("jdoe@gmail.com");
        assertThat(customer.getPassword()).isEqualTo("9876");
    }
}
