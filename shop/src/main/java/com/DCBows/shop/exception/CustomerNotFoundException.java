package com.DCBows.shop.exception;

public class CustomerNotFoundException extends RuntimeException{
    
    public CustomerNotFoundException(long id) {
        super("Customer not found: " + id);
    }
}
