package com.customer_service.dto;

import java.io.Serializable;

public record CustomerRequest(String name, String email, String mobile)implements Serializable {
}
