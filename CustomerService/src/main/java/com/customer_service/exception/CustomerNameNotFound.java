package com.customer_service.exception;

public class CustomerNameNotFound extends RuntimeException {
    public CustomerNameNotFound(String message){
        super(message);
    }
}
