package com.customer_service.service;

import com.customer_service.dto.CustomerRequest;
import com.customer_service.dto.CustomerResponse;
import com.customer_service.dto.PagedResponse;
import com.customer_service.entity.Customer;
import org.hibernate.query.Page;

public interface CustomerService {

    public Customer createCustomer(CustomerRequest customerRequest);
    public CustomerResponse getCustomerById(Long id);
    public CustomerResponse getCustomerByname(String name);
    public CustomerResponse getCustomerByemail(String email);
    PagedResponse<CustomerResponse> getAllCustomers(int page, int size, String sortBy);
    CustomerResponse updateCustomer(Long id, CustomerRequest customerRequest);
    void deleteCustomer(Long id);
    CustomerResponse patchCustomer(Long id, CustomerRequest customerRequest);

}
