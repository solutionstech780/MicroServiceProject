package com.customer_service.dto;

import java.io.Serializable;

public record CustomerResponse(Long id, String name, String email,String mobile)implements Serializable {
}
