package com.customer_service.exception;

public class CustomerNotFoundByEmail extends RuntimeException{
    public CustomerNotFoundByEmail(String message){
        super(message);
    }
}
