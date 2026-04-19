package com.customer_service.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class HttpRequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        long startNs = System.nanoTime();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = (System.nanoTime() - startNs) / 1_000_000;
            String method = request.getMethod();
            String path = request.getRequestURI();
            String query = request.getQueryString();
            int status = response.getStatus();

            // Example: HTTP GET /customer/AllCustomersDetails?page=0&size=5 -> 200 (12ms)

            log.info("CUSTOMER-SERVICE HTTP {} {}{} -> {} ({}ms)",
                    method,
                    path,
                    (query == null || query.isBlank()) ? "" : "?" + query,
                    status,
                    durationMs
            );
            
        }
    }
}

