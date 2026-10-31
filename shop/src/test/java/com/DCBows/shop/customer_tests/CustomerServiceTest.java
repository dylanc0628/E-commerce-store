package com.DCBows.shop.customer_tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.DCBows.shop.domain.Customer;
import com.DCBows.shop.exception.CustomerNotFoundException;
import com.DCBows.shop.repository.CustomerRepository;
import com.DCBows.shop.services.CustomerService;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
    
    @Mock 
    private CustomerRepository customerRepository;
    
    @InjectMocks 
    private CustomerService customerService;

    private Customer customer() {
        return new Customer(1L, "John", "Smith", "jsmith@yahoo.com", "2345");
    }

    @Test 
    void addCustomerSavesAndReturnsCustomer() {
        Customer input = new Customer(null, "John", "Smith", "jsmith@yahoo.com", "9876");
        given(customerRepository.save(input)).willReturn(customer());

        Customer result = customerService.addCustomer(input);

        assertThat(result.getId()).isEqualTo(1L);
        verify(customerRepository).save(input);
    }

    @Test 
    void findAllCustomersReturnsRepositoryResults() {
        given(customerRepository.findAll()).willReturn(List.of(customer()));

        assertThat(customerService.findAllCustomers()).hasSize(1);
    }

    @Test 
    void findCustomerByIdWhenFoundReturnsCustomer() {
        given(customerRepository.findById(1L)).willReturn(Optional.of(customer()));

        assertThat(customerService.findCustomerById(1L).getFirstName()).isEqualTo("John");
        assertThat(customerService.findCustomerById(1L).getLastName()).isEqualTo("Smith");
        assertThat(customerService.findCustomerById(1L).getEmail()).isEqualTo("jsmith@yahoo.com");
        assertThat(customerService.findCustomerById(1L).getPassword()).isEqualTo("2345");
    }

    @Test 
    void findCustomerByIdWhenMissingThrowsNotFound() {
        given(customerRepository.findById(999999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.findCustomerById(999999L))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage("Customer not found: 999999");
    }

    @Test
    void updateCustomerCopiesFieldsAndKeepsId() {
        given(customerRepository.findById(1L)).willReturn(Optional.of(customer()));
        given(customerRepository.save(any(Customer.class))).willAnswer(invocation -> invocation.getArgument(0));

        Customer updates = new Customer(null, "Bill", "Gates", "bgates@gmail.com", "4578");
        Customer result = customerService.updateCustomer(1L, updates);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("Bill");
        assertThat(result.getLastName()).isEqualTo("Gates");
        assertThat(result.getEmail()).isEqualTo("bgates@gmail.com");
        assertThat(result.getPassword()).isEqualTo("4578");
    }

    @Test 
    void updateCustomerWhenMissingThrowsAndNeverSaves() {
        given(customerRepository.findById(999999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.updateCustomer(999999L, customer()))
                .isInstanceOf(CustomerNotFoundException.class);
        
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test 
    void deleteCustomerWhenFoundDeletesById() {
        given(customerRepository.existsById(1L)).willReturn(true);

        customerService.deleteCustomer(1L);

        verify(customerRepository).deleteById(1L);
    }

    @Test 
    void deleteCustomerWhenMissingThrowsAndNeverDeletes() {
        given(customerRepository.existsById(999999L)).willReturn(false);

        assertThatThrownBy(() -> customerService.deleteCustomer(999999L))
                .isInstanceOf(CustomerNotFoundException.class);
        
        verify(customerRepository, never()).deleteById(any(Long.class));
    }
}
