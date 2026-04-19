package com.customer_service.controller;

import com.customer_service.dto.CustomerResponse;
import com.customer_service.dto.PagedResponse;
import com.customer_service.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerControllerUnitTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCustomers_DefaultParams() {
        // Arrange
        List<CustomerResponse> customers = List.of(
                new CustomerResponse(1L, "John Doe", "john@example.com", "1234567890"),
                new CustomerResponse(2L, "Jane Doe", "jane@example.com", "9876543210")
        );
        PagedResponse<CustomerResponse> mockResponse = new PagedResponse<>(
                customers,   // content
                0,           // pageNumber
                5,           // pageSize
                2L,          // totalElements
                1,           // totalPages
                true         // last
        );

        when(customerService.getAllCustomers(0, 5, "id")).thenReturn(mockResponse);

        // Act
        ResponseEntity<PagedResponse<CustomerResponse>> response =
                customerController.getAllCustomers(0, 5, "id");

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().content().size());
        assertEquals(0, response.getBody().pageNumber());
        assertEquals(5, response.getBody().pageSize());

        // Verify service was called
        verify(customerService, times(1)).getAllCustomers(0, 5, "id");
    }

    @Test
    void testGetAllCustomers_CustomParams() {
        // Arrange
        List<CustomerResponse> customers = List.of(
                new CustomerResponse(3L, "Alice", "alice@example.com", "1112223333")
        );
        PagedResponse<CustomerResponse> mockResponse = new PagedResponse<>(
                customers,   // content
                1,           // pageNumber
                1,           // pageSize
                3L,          // totalElements
                3,           // totalPages
                false        // last
        );

        when(customerService.getAllCustomers(1, 1, "name")).thenReturn(mockResponse);

        // Act
        ResponseEntity<PagedResponse<CustomerResponse>> response =
                customerController.getAllCustomers(1, 1, "name");

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().content().size());
        assertEquals("Alice", response.getBody().content().get(0).name());
        assertEquals(1, response.getBody().pageNumber());
        assertEquals(1, response.getBody().pageSize());

        // Verify service was called
        verify(customerService, times(1)).getAllCustomers(1, 1, "name");
    }
}
