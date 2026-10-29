package com.DCBows.shop.customer_tests;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.DCBows.shop.domain.Customer;
import com.DCBows.shop.repository.CustomerRepository;

@SpringBootTest 
@Transactional
public class CustomerRepositoryTest {
    
    @Autowired 
    private CustomerRepository customerRepository;

    private Customer newCustomer(String firstName, String lastName, String email, String password) {
        return new Customer(null, firstName, lastName, email, password);
    }

    @Test 
    void saveAssignsGeneratedId() {
        Customer saved = customerRepository.save(newCustomer("John", "Doe", "jdoe@yahoo.com", "2345"));

        assertThat(saved.getId()).isNotNull();
    }

    @Test 
    void findByIdReturnsPersistedProduct() {
        Long id = customerRepository.save(newCustomer("John", "Doe", "jdoe@yahoo.com", "2345")).getId();

        Customer found = customerRepository.findById(id).orElseThrow();

        assertThat(found.getFirstName()).isEqualTo("John");
        assertThat(found.getLastName()).isEqualTo("Doe");
        assertThat(found.getEmail()).isEqualTo("jdoe@yahoo.com");
        assertThat(found.getPassword()).isEqualTo("2345");
    }

    @Test 
    void findByIdWhenMissingReturnsEmpty() {
        assertThat(customerRepository.findById(999999L)).isEmpty();
    }

    @Test 
    void findAllReturnsSavedProducts() {
        customerRepository.save(newCustomer("John", "Doe", "jdoe@yahoo.com", "2345"));
        customerRepository.save(newCustomer("Jane", "Doe", "jdoe@gmail.com", "9876"));

        assertThat(customerRepository.findAll())
            .extracting(Customer::getFirstName)
            .contains("John", "Jane");
    }

    @Test 
    void saveExistingProductUpdatesRow() {
        Customer saved = customerRepository.save(newCustomer("John", "Doe", "jdoe@yahoo.com", "2345"));

        saved.setPassword("123456");
        customerRepository.save(saved);

        Customer reloaded = customerRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getPassword()).isEqualTo("123456");
    }

    @Test 
    void deleteRemovesRow() {
        Long id = customerRepository.save(newCustomer("John", "Doe", "jdoe@yahoo.com", "2345")).getId();

        customerRepository.deleteById(id);

        assertThat(customerRepository.existsById(id)).isFalse();
    }
}
