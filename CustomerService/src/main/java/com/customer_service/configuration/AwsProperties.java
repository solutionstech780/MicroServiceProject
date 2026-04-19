package com.customer_service.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "aws")
public class AwsProperties {

    /**
     * AWS region (example: us-east-1).
     */
    private String region;

    private final S3 s3 = new S3();

    @Data
    public static class S3 {
        /**
         * S3 bucket name.
         */
        private String bucket;

        /**
         * Prefix used for uploaded objects.
         * Keep this aligned with your S3 -> Lambda event notification filter.
         */
        private String keyPrefix = "uploads/";

        /**
         * Expiration (seconds) for returned presigned GET URLs.
         */
        private long urlExpirationSeconds = 86400L;
    }
}

