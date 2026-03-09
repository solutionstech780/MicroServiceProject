package com.customer_service.repository;

import com.customer_service.dto.CustomerResponse;
import com.customer_service.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer,Long> {

    Optional<Customer> findByName(String name);

    Optional<Customer> findByEmail(String email);

    boolean existsByMobile(String mobile);

    boolean existsByEmail(String email);
}
