package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.ProductRequest;
import com.mylstech.rentro.dto.response.ProductResponse;
import com.mylstech.rentro.service.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        return new ResponseEntity<>(productService.createProduct(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }

    // We're keeping these methods for backward compatibility and individual image management
    @PostMapping("/{id}/images")
    public ResponseEntity<ProductResponse> addImageToProduct(
            @PathVariable Long id,
            @RequestBody String imageUrl) {
        return ResponseEntity.ok(productService.addImageToProduct(id, imageUrl));
    }

    @DeleteMapping("/{id}/images")
    public ResponseEntity<ProductResponse> removeImageFromProduct(
            @PathVariable Long id,
            @RequestBody String imageUrl) {
        return ResponseEntity.ok(productService.removeImageFromProduct(id, imageUrl));
    }
}
