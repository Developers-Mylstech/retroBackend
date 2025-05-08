package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.response.FileUploadResponse;
import com.mylstech.rentro.model.Image;
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
@RequestMapping("/api/v1/images-new")
@RequiredArgsConstructor
public class ImageEntityController {

    private final ImageEntityService imageEntityService;

    @Operation(summary = "Get all images")
    @GetMapping
    public ResponseEntity<List<Image>> getAllImages() {
        return ResponseEntity.ok(imageEntityService.getAllImages());
    }

    @Operation(summary = "Get image by ID")
    @GetMapping("/{imageId}")
    public ResponseEntity<Image> getImageById(@PathVariable Long imageId) {
        return ResponseEntity.ok(imageEntityService.getImageById(imageId));
    }

    @Operation(summary = "Upload image and get URL with image object")
    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "quality", defaultValue = "80") int quality,
            @RequestParam(value = "fallbackToJpeg", defaultValue = "true") boolean fallbackToJpeg) {
        try {
            FileUploadResponse response = imageEntityService.uploadImage(file, quality, fallbackToJpeg);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Upload multiple images and get URLs with image objects")
    @PostMapping("/batch-upload")
    public ResponseEntity<List<FileUploadResponse>> uploadMultipleImages(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "quality", defaultValue = "80") int quality,
            @RequestParam(value = "fallbackToJpeg", defaultValue = "true") boolean fallbackToJpeg) {
        try {
            List<FileUploadResponse> responses = imageEntityService.uploadMultipleImages(files, quality, fallbackToJpeg);
            return ResponseEntity.ok(responses);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Save image URL")
    @PostMapping
    public ResponseEntity<Image> saveImage(@RequestParam String imageUrl) {
        Image savedImage = imageEntityService.saveImage(imageUrl);
        return new ResponseEntity<>(savedImage, HttpStatus.CREATED);
    }

    @Operation(summary = "Delete image")
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        try {
            imageEntityService.deleteImage(imageId);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}