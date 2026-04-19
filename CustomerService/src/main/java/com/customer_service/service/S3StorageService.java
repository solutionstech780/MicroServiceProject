package com.customer_service.service;

import com.customer_service.configuration.AwsProperties;
import com.customer_service.dto.FileUploadResponse;
import com.customer_service.exception.S3UploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.InputStream;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3StorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final AwsProperties awsProperties;

    public FileUploadResponse upload(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new S3UploadException("File is missing or empty.");
        }

        final String bucket = awsProperties.getS3().getBucket();
        final long size = multipartFile.getSize();
        final String key = buildObjectKey(multipartFile.getOriginalFilename());
        final String contentType = Optional.ofNullable(multipartFile.getContentType())
                .orElse("application/octet-stream");

        try (InputStream inputStream = multipartFile.getInputStream()) {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromInputStream(inputStream, size));
            String presignedUrl = presignGetUrl(key);

            log.info("S3 upload success bucket={} key={} size={}", bucket, key, size);
            return new FileUploadResponse(presignedUrl, key, size);
        } catch (Exception e) {
            log.error("S3 upload failed bucket={} key={} size={}", bucket, key, size, e);
            throw new S3UploadException("Failed to upload file to S3.", e);
        }
    }

    private String buildObjectKey(String originalFilename) {
        String safeFilename = Paths.get(originalFilename == null ? "file" : originalFilename)
                .getFileName()
                .toString();

        safeFilename = safeFilename.replaceAll("[^a-zA-Z0-9._-]", "_");

        String datePart = LocalDate.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        String prefix = awsProperties.getS3().getKeyPrefix();
        if (prefix == null || prefix.isBlank()) {
            prefix = "uploads/";
        }
        if (!prefix.endsWith("/")) {
            prefix = prefix + "/";
        }

        return prefix + datePart + "/" + UUID.randomUUID() + "-" + safeFilename;
    }

    private String presignGetUrl(String key) {
        String bucket = awsProperties.getS3().getBucket();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(awsProperties.getS3().getUrlExpirationSeconds()))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }
}

