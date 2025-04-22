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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProductController {

    private final ProductService productService;
    private final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        try {
            logger.debug("Fetching all products");
            List<ProductResponse> products = productService.getAllProducts();
            logger.debug("Found {} products", products.size());
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            logger.error("Error fetching all products", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        try {
            logger.debug("Fetching product with id: {}", id);
            ProductResponse product = productService.getProductById(id);
            logger.debug("Found product: {}", product);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            logger.error("Error fetching product with id: " + id, e);
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        try {
            logger.debug("Creating new product: {}", request);
            ProductResponse product = productService.createProduct(request);
            logger.debug("Created product: {}", product);
            return new ResponseEntity<>(product, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating product", e);
            throw e;
        }
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
