package com.DCBows.shop.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.DCBows.shop.domain.Customer;
import com.DCBows.shop.exception.CustomerNotFoundException;
import com.DCBows.shop.repository.CustomerRepository;

@Service 
@Transactional 
public class CustomerService {
    
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer addCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Transactional(readOnly=true)
    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }

    @Transactional(readOnly=true)
    public Customer findCustomerById(long id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    public Customer updateCustomer(long id, Customer updates) {
        Customer customer = findCustomerById(id);
        customer.setFirstName(updates.getFirstName());
        customer.setLastName(updates.getLastName());
        customer.setEmail(updates.getEmail());
        customer.setPassword(updates.getPassword());
        return customerRepository.save(customer);
    }

    public void deleteCustomer(long id) {
        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException(id);
        }
        customerRepository.deleteById(id);
    }
}
