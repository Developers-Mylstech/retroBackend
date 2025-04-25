package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.response.EntityImagesResponse;
import com.mylstech.rentro.dto.response.FileUploadResponse;
import com.mylstech.rentro.service.FileStorageService;
import com.mylstech.rentro.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {

    private final FileStorageService fileStorageService;
    private final ImageService imageService;

    @Operation(summary = "Upload image and get URL")
    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "quality", defaultValue = "80") int quality,
            @RequestParam(value = "fallbackToJpeg", defaultValue = "true") boolean fallbackToJpeg) {
        try {
            FileUploadResponse response = imageService.uploadImage(file, quality, fallbackToJpeg);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Upload multiple images and get URLs")
    @PostMapping("/batch-upload")
    public ResponseEntity<List<FileUploadResponse>> uploadMultipleImages(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "quality", defaultValue = "80") int quality,
            @RequestParam(value = "fallbackToJpeg", defaultValue = "true") boolean fallbackToJpeg) {
        try {
            List<FileUploadResponse> responses = imageService.uploadMultipleImages(files, quality, fallbackToJpeg);
            return ResponseEntity.ok(responses);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Get all images for an entity entity type={'product', 'brand', 'category', 'jobpost'} ")
    @GetMapping("/{entityType}/{entityId}")
    public ResponseEntity<EntityImagesResponse> getEntityImages(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        EntityImagesResponse response = imageService.getEntityImages(entityType, entityId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all images for an entity type")
    @GetMapping("/{entityType}")
    public ResponseEntity<List<EntityImagesResponse>> getAllImagesByEntityType(
            @PathVariable String entityType) {
        List<EntityImagesResponse> responses = imageService.getAllImagesByEntityType(entityType);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Upload image for any entity")
    @PostMapping("/upload/{entityType}/{entityId}")
    public ResponseEntity<FileUploadResponse> uploadImage(
            @PathVariable String entityType,
            @PathVariable Long entityId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "quality", defaultValue = "80") int quality,
            @RequestParam(value = "fallbackToJpeg", defaultValue = "true") boolean fallbackToJpeg) {
        try {
            FileUploadResponse response = imageService.uploadAndAssociateImage(
                entityType, 
                entityId, 
                file, 
                quality, 
                fallbackToJpeg
            );
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Update image for any entity")
    @PutMapping("/update/{entityType}/{entityId}")
    public ResponseEntity<FileUploadResponse> updateImage(
            @PathVariable String entityType,
            @PathVariable Long entityId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "quality", defaultValue = "80") int quality,
            @RequestParam(value = "fallbackToJpeg", defaultValue = "true") boolean fallbackToJpeg) {
        try {
            // Delete old image first
            imageService.deleteEntityImage(entityType, entityId);
            
            // Upload new image
            FileUploadResponse response = imageService.uploadAndAssociateImage(
                entityType, 
                entityId, 
                file, 
                quality, 
                fallbackToJpeg
            );
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Delete image for any entity")
    @DeleteMapping("/delete/{entityType}/{entityId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        imageService.deleteEntityImage(entityType, entityId);
        return ResponseEntity.noContent().build();
    }
}
