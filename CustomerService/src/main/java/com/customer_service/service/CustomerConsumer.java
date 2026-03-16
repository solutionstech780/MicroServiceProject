package com.customer_service.service;

import com.customer_service.dto.CustomerResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomerConsumer {

    private static final Logger log = LoggerFactory.getLogger(CustomerConsumer.class);
    private final EmailService emailService;

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 2000)
    )
    @KafkaListener(
            topics = "customer-topic",
            groupId = "customer-group",
            concurrency = "3"   // 🔥 3 parallel threads
    )
    public void consume(CustomerResponse response) {
        log.info("{} processing: {}", Thread.currentThread().getName(), response);

        try {
            // Send synchronously so failures trigger retry/DLT handling.
            emailService.sendEmail(response.email(), response.name(), response.id());
        } catch (Exception e) {
            log.error("Failed to send welcome email to {} (customerId={})", response.email(), response.id(), e);
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(
            topics = "customer-topic-dlt",
            groupId = "customer-group"
    )
    public void listenDLT(CustomerResponse response) {
        log.error("DLT message: {}", response);
    }
}