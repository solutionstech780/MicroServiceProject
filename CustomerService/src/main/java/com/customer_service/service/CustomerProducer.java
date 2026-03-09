package com.customer_service.service;

import com.customer_service.dto.CustomerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerProducer {

    private final KafkaTemplate<String, String> kafkaStringTemplate;
    private final KafkaTemplate<String, CustomerResponse> kafkaTemplate;

    private static final String TOPIC = "customer-topic";

    public void sendCustomerEvent(CustomerResponse response) {
        kafkaTemplate.send(TOPIC, response);
    }
    // ✅ New method for deletion event
    public void sendCustomerDeletedEvent(Long id) {
        kafkaStringTemplate.send(TOPIC, "Customer deleted with ID: " + id);
    }
}