package com.customer_service.exception;

public class IdNotFoundException extends RuntimeException{

    public IdNotFoundException(String message){
        super(message);
    }

}
