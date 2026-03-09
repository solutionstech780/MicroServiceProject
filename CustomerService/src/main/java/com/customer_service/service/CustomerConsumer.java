package com.customer_service.service;

import com.customer_service.dto.CustomerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomerConsumer {

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
    public void consume(CustomerResponse response) throws Exception {

        System.out.println(
                Thread.currentThread().getName() +
                        " processing: " + response
        );

        emailService.sendEmail(
                response.email(),
                response.name(),
                response.id()
        );
    }

    @KafkaListener(
            topics = "customer-topic-dlt",
            groupId = "customer-group"
    )
    public void listenDLT(CustomerResponse response) {

        System.out.println("DLT message: " + response);
    }
}