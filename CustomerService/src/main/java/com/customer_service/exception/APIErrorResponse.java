package com.customer_service.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class APIErrorResponse {
    private String message;
    private int status;
    private LocalDateTime timestamp;

}
