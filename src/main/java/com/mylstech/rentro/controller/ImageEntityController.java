package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.response.EntityImagesResponse;
import com.mylstech.rentro.dto.response.FileUploadResponse;
import com.mylstech.rentro.service.ImageEntityService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/image-entities")
@RequiredArgsConstructor
public class ImageEntityController {
    private final ImageEntityService imageEntityService;

    @Operation(summary = "Upload image and get image entity with ID")
    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "quality", defaultValue = "80") int quality,
            @RequestParam(value = "fallbackToJpeg", defaultValue = "true") boolean fallbackToJpeg) {
        try {
            FileUploadResponse response = imageEntityService.uploadImage ( file, quality, fallbackToJpeg );
            return ResponseEntity.ok ( response );
        }
        catch ( IOException e ) {
            return ResponseEntity.internalServerError ( ).build ( );
        }
    }

    @Operation(summary = "Upload multiple images and get image entities with IDs")
    @PostMapping("/batch-upload")
    public ResponseEntity<List<FileUploadResponse>> uploadMultipleImages(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "quality", defaultValue = "80") int quality,
            @RequestParam(value = "fallbackToJpeg", defaultValue = "true") boolean fallbackToJpeg) {
        try {
            List<FileUploadResponse> responses = imageEntityService.uploadMultipleImages ( files, quality, fallbackToJpeg );
            return ResponseEntity.ok ( responses );
        }
        catch ( IOException e ) {
            return ResponseEntity.internalServerError ( ).build ( );
        }
    }

    @Operation(summary = "Get all images")
    @GetMapping
    public ResponseEntity<List<EntityImagesResponse>> getAllImages() {
        List<EntityImagesResponse> images = imageEntityService.getAllImages ( );
        return ResponseEntity.ok ( images );
    }

    @Operation(summary = "Get image by ID")
    @GetMapping("/{imageId}")
    public ResponseEntity<EntityImagesResponse> getImageById(@PathVariable Long imageId) {
        EntityImagesResponse image = imageEntityService.getImageById ( imageId );
        return ResponseEntity.ok ( image );
    }

    @Operation(summary = "Delete image by ID")
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        try {
            imageEntityService.deleteImage ( imageId );
            return ResponseEntity.noContent ( ).build ( );
        }
        catch ( IOException e ) {
            return ResponseEntity.status ( HttpStatus.INTERNAL_SERVER_ERROR ).build ( );
        }
    }
}