package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.response.FileUploadResponse;
import com.mylstech.rentro.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload-pdf")
    @Operation(summary = "Upload a PDF file")
    public ResponseEntity<FileUploadResponse> uploadPdf(
            @Parameter(description = "PDF file to upload", required = true)
            @RequestParam("file") MultipartFile file) {
        try {

            return ResponseEntity.ok(fileStorageService.storePdf(file));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new FileUploadResponse(
                        null,
                        null,
                        null,
                        0,
                        e.getMessage()
                    ));
        }
    }
}