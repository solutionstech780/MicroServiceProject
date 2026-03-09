package com.customer_service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {

    /**
     * Global async executor used by Spring's @Async methods.
     * Backed by Executors.newCachedThreadPool().
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        return Executors.newCachedThreadPool();
    }
}

