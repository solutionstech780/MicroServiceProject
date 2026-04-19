package com.customer_service.controller;

import com.customer_service.dto.FileUploadResponse;
import com.customer_service.service.S3StorageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class S3UploadController {

    private final S3StorageService s3StorageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a file to S3")
    public ResponseEntity<FileUploadResponse> upload(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(s3StorageService.upload(file));
    }
}

