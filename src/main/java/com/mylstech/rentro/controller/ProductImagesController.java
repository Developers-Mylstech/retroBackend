package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.ProductImagesRequest;
import com.mylstech.rentro.dto.response.FileUploadResponse;
import com.mylstech.rentro.dto.response.ProductImagesResponse;
import com.mylstech.rentro.service.FileStorageService;
import com.mylstech.rentro.service.ProductImagesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/product-images")
@RequiredArgsConstructor
public class ProductImagesController {

    private final ProductImagesService productImagesService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<List<ProductImagesResponse>> getAllProductImages() {
        return ResponseEntity.ok(productImagesService.getAllProductImages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductImagesResponse> getProductImagesById(@PathVariable Long id) {
        return ResponseEntity.ok(productImagesService.getProductImagesById(id));
    }

    @PostMapping
    public ResponseEntity<ProductImagesResponse> createProductImages(@RequestBody ProductImagesRequest request) {
        return new ResponseEntity<>(productImagesService.createProductImages(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductImagesResponse> updateProductImages(@PathVariable Long id, @RequestBody ProductImagesRequest request) {
        return ResponseEntity.ok(productImagesService.updateProductImages(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductImages(@PathVariable Long id) {
        productImagesService.deleteProductImages(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<ProductImagesResponse> addImageUrl(@PathVariable Long id, @RequestBody String imageUrl) {
        return ResponseEntity.ok(productImagesService.addImageUrl(id, imageUrl));
    }

    @DeleteMapping("/{id}/images")
    public ResponseEntity<ProductImagesResponse> removeImageUrl(@PathVariable Long id, @RequestBody String imageUrl) {
        return ResponseEntity.ok(productImagesService.removeImageUrl(id, imageUrl));
    }

    /**
     * Unified image upload endpoint that accepts any image format and converts to WebP
     * @param file The image file to upload
     * @param quality The quality of the WebP image (0-100)
     * @param fallbackToJpeg Whether to fall back to JPEG if WebP conversion fails
     * @return The upload response with the file URL
     */
    @PostMapping("/upload")
    @Operation(summary = "Upload an image")
    public ResponseEntity<FileUploadResponse> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "quality", defaultValue = "80") int quality,
            @RequestParam(value = "fallbackToJpeg", defaultValue = "true") boolean fallbackToJpeg) {
        try {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            String fileUrl = fileStorageService.storeImageAsWebP(file, quality, fallbackToJpeg);

            // Determine content type based on file extension
            String contentType = fileUrl.endsWith(".webp") ? "image/webp" : "image/jpeg";

            FileUploadResponse response = new FileUploadResponse(
                fileName,
                fileUrl,
                contentType,
                file.getSize()
            );

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace(); // Log the error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Upload an image and add it to a ProductImages entity
     * @param id The ID of the ProductImages entity
     * @param file The image file to upload
     * @param quality The quality of the WebP image (0-100)
     * @param fallbackToJpeg Whether to fall back to JPEG if WebP conversion fails
     * @return The updated ProductImages entity
     */
    @PostMapping("/{id}/upload")
    public ResponseEntity<ProductImagesResponse> uploadAndAddImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "quality", defaultValue = "80") int quality,
            @RequestParam(value = "fallbackToJpeg", defaultValue = "true") boolean fallbackToJpeg) {
        try {
            String fileUrl = fileStorageService.storeImageAsWebP(file, quality, fallbackToJpeg);
            return ResponseEntity.ok(productImagesService.addImageUrl(id, fileUrl));
        } catch (IOException e) {
            e.printStackTrace(); // Log the error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Upload multiple images and convert them all to WebP
     * @param files The image files to upload
     * @param quality The quality of the WebP images (0-100)
     * @param fallbackToJpeg Whether to fall back to JPEG if WebP conversion fails
     * @return A list of upload responses with file URLs
     */
    @PostMapping("/batch-upload")
    public ResponseEntity<List<FileUploadResponse>> uploadMultipleImages(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "quality", defaultValue = "80") int quality,
            @RequestParam(value = "fallbackToJpeg", defaultValue = "true") boolean fallbackToJpeg) {
        List<FileUploadResponse> responses = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                String fileUrl = fileStorageService.storeImageAsWebP(file, quality, fallbackToJpeg);

                // Determine content type based on file extension
                String contentType = fileUrl.endsWith(".webp") ? "image/webp" : "image/jpeg";

                FileUploadResponse response = new FileUploadResponse(
                    fileName,
                    fileUrl,
                    contentType,
                    file.getSize()
                );

                responses.add(response);
            } catch (IOException e) {
                // Log the error but continue with other files
                e.printStackTrace();
            }
        }

        return ResponseEntity.ok(responses);
    }
}
