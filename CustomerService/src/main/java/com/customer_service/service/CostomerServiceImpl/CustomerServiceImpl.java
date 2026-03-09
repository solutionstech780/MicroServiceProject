package com.customer_service.service.CostomerServiceImpl;

import com.customer_service.dto.CustomerRequest;
import com.customer_service.dto.CustomerResponse;
import com.customer_service.dto.PagedResponse;
import com.customer_service.entity.Customer;
import com.customer_service.exception.CustomerNameNotFound;
import com.customer_service.exception.CustomerNotFoundByEmail;
import com.customer_service.exception.IdNotFoundException;
import com.customer_service.repository.CustomerRepository;
import com.customer_service.service.CustomerProducer;
import com.customer_service.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerProducer customerProducer;

    // ================= CREATE =================
    @CacheEvict(value = {"customers", "customerById"}, allEntries = true)
    @Override
    public Customer createCustomer(CustomerRequest customerRequest) {

        if (customerRepository.existsByEmail(customerRequest.email())) {
            throw new RuntimeException("Customer already exists with email: " + customerRequest.email());
        }

        if (customerRepository.existsByMobile(customerRequest.mobile())) {
            throw new RuntimeException("Customer already exists with mobile: " + customerRequest.mobile());
        }

        Customer customer = new Customer();
        customer.setName(customerRequest.name());
        customer.setEmail(customerRequest.email());
        customer.setMobile(customerRequest.mobile());

        Customer savedCustomer = customerRepository.save(customer);

        customerProducer.sendCustomerEvent(mapToResponse(savedCustomer));

        return savedCustomer;
    }

    // ================= READ =================
    @Cacheable(value = "customerById", key = "#id")
    @Override
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Customer not found with id: " + id));
        return mapToResponse(customer);
    }

    @Override
    public CustomerResponse getCustomerByname(String name) {
        Customer customer = customerRepository.findByName(name)
                .orElseThrow(() -> new CustomerNameNotFound("Customer not found with name: " + name));
        return mapToResponse(customer);
    }

    @Override
    public CustomerResponse getCustomerByemail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomerNotFoundByEmail("Customer not found with email: " + email));
        return mapToResponse(customer);
    }

    @Cacheable(value = "customers", key = "#page + '-' + #size + '-' + #sortBy")
    @Override
    public PagedResponse<CustomerResponse> getAllCustomers(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<Customer> customerPage = customerRepository.findAll(pageable);

        List<CustomerResponse> content = customerPage.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return new PagedResponse<>(
                content,
                customerPage.getNumber(),
                customerPage.getSize(),
                customerPage.getTotalElements(),
                customerPage.getTotalPages(),
                customerPage.isLast()
        );
    }

    // ================= UPDATE =================
    @CacheEvict(value = {"customers", "customerById"}, allEntries = true)
    @Override
    public CustomerResponse updateCustomer(Long id, CustomerRequest customerRequest) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Customer not found with id: " + id));

        if (!customer.getEmail().equals(customerRequest.email())
                && customerRepository.existsByEmail(customerRequest.email())) {
            throw new RuntimeException("Email already exists: " + customerRequest.email());
        }

        if (!customer.getMobile().equals(customerRequest.mobile())
                && customerRepository.existsByMobile(customerRequest.mobile())) {
            throw new RuntimeException("Mobile already exists: " + customerRequest.mobile());
        }

        customer.setName(customerRequest.name());
        customer.setEmail(customerRequest.email());
        customer.setMobile(customerRequest.mobile());

        Customer updatedCustomer = customerRepository.save(customer);

        customerProducer.sendCustomerEvent(mapToResponse(updatedCustomer));

        return mapToResponse(updatedCustomer);
    }

    // ================= PATCH (Partial Update) =================
    @CacheEvict(value = {"customers", "customerById"}, allEntries = true)
    @Override
    public CustomerResponse patchCustomer(Long id, CustomerRequest customerRequest) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Customer not found with id: " + id));

        if (customerRequest.name() != null && !customerRequest.name().isBlank()) {
            customer.setName(customerRequest.name());
        }

        if (customerRequest.email() != null && !customerRequest.email().isBlank()) {
            if (!customer.getEmail().equals(customerRequest.email())
                    && customerRepository.existsByEmail(customerRequest.email())) {
                throw new RuntimeException("Email already exists: " + customerRequest.email());
            }
            customer.setEmail(customerRequest.email());
        }

        if (customerRequest.mobile() != null && !customerRequest.mobile().isBlank()) {
            if (!customer.getMobile().equals(customerRequest.mobile())
                    && customerRepository.existsByMobile(customerRequest.mobile())) {
                throw new RuntimeException("Mobile already exists: " + customerRequest.mobile());
            }
            customer.setMobile(customerRequest.mobile());
        }

        Customer updatedCustomer = customerRepository.save(customer);
        customerProducer.sendCustomerEvent(mapToResponse(updatedCustomer));

        return mapToResponse(updatedCustomer);
    }

    // ================= DELETE =================
    @CacheEvict(value = {"customers", "customerById"}, allEntries = true)
    @Override
    public void deleteCustomer(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Customer not found with id: " + id));

        customerRepository.delete(customer);

        customerProducer.sendCustomerDeletedEvent(id);
    }
    //added logic
    //update code
    // ================= COMMON MAPPER =================
    private CustomerResponse mapToResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getMobile()
        );
    }
}