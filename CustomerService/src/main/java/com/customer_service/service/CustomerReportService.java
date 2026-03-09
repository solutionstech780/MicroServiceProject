package com.customer_service.service;

import com.customer_service.entity.Customer;
import com.customer_service.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Executor;

@Service
@Slf4j
public class CustomerReportService {

    private final CustomerRepository customerRepository;

    private final Executor taskExecutor;

    public CustomerReportService(CustomerRepository customerRepository,
                                 @Qualifier("taskExecutor") Executor taskExecutor) {
        this.customerRepository = customerRepository;
        this.taskExecutor = taskExecutor;
    }

    public void generateCustomerReportAsync() {

        taskExecutor.execute(() -> {
            try {

                log.info("Customer report generation started on thread {}",
                        Thread.currentThread().getName());

                long totalCustomers = customerRepository.count();

                Thread.sleep(3000);

                log.info("Customer report generated successfully. totalCustomers={}",
                        totalCustomers);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                log.error("Customer report generation interrupted", e);

            } catch (Exception e) {

                log.error("Unexpected error during customer report generation", e);
            }
        });
    }

    public byte[] generateCustomerReportCsv() {

        List<Customer> customers = customerRepository.findAll();

        StringBuilder sb = new StringBuilder();
        sb.append("Id,Name,Email,Mobile\n");

        for (Customer customer : customers) {

            sb.append(customer.getId()).append(",")
                    .append(escapeCsv(customer.getName())).append(",")
                    .append(escapeCsv(customer.getEmail())).append(",")
                    .append(escapeCsv(customer.getMobile()))
                    .append("\n");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escapeCsv(String value) {

        if (value == null) {
            return "";
        }

        String escaped = value.replace("\"", "\"\"");

        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")) {
            return "\"" + escaped + "\"";
        }

        return escaped;
    }
}