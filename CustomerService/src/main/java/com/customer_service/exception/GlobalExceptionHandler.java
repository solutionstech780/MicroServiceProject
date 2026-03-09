package com.customer_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IdNotFoundException.class)
    public ResponseEntity<APIErrorResponse> CustomerIdNotFoundException(IdNotFoundException ex){
                APIErrorResponse error = new APIErrorResponse(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now());
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomerNameNotFound.class)
    public ResponseEntity<APIErrorResponse> CustomerNameNotFoundException(CustomerNameNotFound ex){
        APIErrorResponse error = new APIErrorResponse(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(CustomerNotFoundByEmail.class)
    public ResponseEntity<APIErrorResponse> CustomerEmailNotFoundException(CustomerNotFoundByEmail ex){
        APIErrorResponse error = new APIErrorResponse(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
