package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.BuyNowRequest;
import com.mylstech.rentro.dto.request.ProductRequest;
import com.mylstech.rentro.dto.response.CheckOutResponse;
import com.mylstech.rentro.dto.response.ProductResponse;
import com.mylstech.rentro.service.ProductService;
import com.mylstech.rentro.util.ProductType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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

//    // We're keeping these methods for backward compatibility and individual image management
//    @PostMapping("/{id}/images")
//    public ResponseEntity<ProductResponse> addImageToProduct(
//            @PathVariable Long id,
//            @RequestBody String imageUrl) {
//        return ResponseEntity.ok(productService.addImageToProduct(id,  imageUrl));
//    }

    @DeleteMapping("/{productId}/images/{imageId}")
    @Operation(summary = "Remove a specific image from a product", 
               description = "Removes the association between a product and an image")
    public ResponseEntity<ProductResponse> removeImageFromProduct(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        try {
            ProductResponse response = productService.removeImageFromProduct(productId, imageId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PostMapping("/{productId}/images/{imageId}")
    @Operation(summary = "Add a specific image to a product", 
               description = "Creates an association between a product and an image")
    public ResponseEntity<ProductResponse> addImageToProduct(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        try {
            ProductResponse response = productService.addImageToProduct(productId, imageId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @Operation(summary = "Get products by type", description = "Retrieve products filtered by type (SELL, RENT, SERVICE)")
    @GetMapping("/by-type/{productType}")
    public ResponseEntity<List<ProductResponse>> getProductsByType(@PathVariable ProductType productType) {
        try {
            logger.debug("Fetching products by type: {}", productType);
            List<ProductResponse> products = productService.getProductsByType(productType);
            logger.debug("Found {} products of type {}", products.size(), productType);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            logger.error("Error fetching products by type: " + productType, e);
            throw e;
        }
    }

    @Operation(
        summary = "Filter products", 
        description = "Filter products by various criteria. Currently supports filtering by type (SELL, RENT, SERVICE)"
    )
    @GetMapping("/filter")
    public ResponseEntity<List<ProductResponse>> filterProducts(
            @RequestParam(required = false) ProductType type) {
        try {
            if (type != null) {
                logger.debug("Filtering products by type: {}", type);
                List<ProductResponse> products = productService.getProductsByType(type);
                logger.debug("Found {} products of type {}", products.size(), type);
                return ResponseEntity.ok(products);
            } else {
                // If no type is specified, return all products
                logger.debug("No filter specified, returning all products");
                return getAllProducts();
            }
        } catch (Exception e) {
            logger.error("Error filtering products", e);
            throw e;
        }
    }

    @Operation(
        summary = "Buy Now", 
        description = "Purchase a product immediately and proceed to checkout"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created checkout"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PostMapping("/{id}/buy-now")
    public ResponseEntity<CheckOutResponse> buyNow(
            @PathVariable Long id,
            @RequestBody BuyNowRequest request) {
        try {
            logger.debug("Buy now request for product ID: {}", id);
            CheckOutResponse response = productService.buyNow(id, request);
            logger.debug("Created checkout with ID: {}", response.getCheckoutId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error processing buy now request for product ID: " + id, e);
            throw e;
        }
    }
}
