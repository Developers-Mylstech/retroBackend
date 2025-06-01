package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.BuyNowRequest;
import com.mylstech.rentro.dto.request.ProductRequest;
import com.mylstech.rentro.dto.response.CheckOutResponse;
import com.mylstech.rentro.dto.response.ProductResponse;
import com.mylstech.rentro.service.ProductService;
import com.mylstech.rentro.util.ProductType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger = LoggerFactory.getLogger ( ProductController.class );

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {

        logger.debug ( "Fetching all products" );
        List<ProductResponse> products = productService.getAllProducts ( );
        logger.debug ( "Found {} products", products.size ( ) );
        return ResponseEntity.ok ( products );

    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {

        logger.debug ( "Fetching product with id: {}", id );
        ProductResponse product = productService.getProductById ( id );
        logger.debug ( "Found product: {}", product );
        return ResponseEntity.ok ( product );

    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {

        logger.debug ( "Creating new product: {}", request );
        ProductResponse product = productService.createProduct ( request );
        logger.debug ( "Created product: {}", product );
        return new ResponseEntity<> ( product, HttpStatus.CREATED );

    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        return ResponseEntity.ok ( productService.updateProduct ( id, request ) );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct ( id );
        return ResponseEntity.ok ( "Product deleted successfully" );
    }


    @DeleteMapping("/{productId}/images/{imageId}")
    @Operation(summary = "Remove a specific image from a product",
            description = "Removes the association between a product and an image")
    public ResponseEntity<ProductResponse> removeImageFromProduct(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        try {
            ProductResponse response = productService.removeImageFromProduct ( productId, imageId );
            return ResponseEntity.ok ( response );
        }
        catch ( Exception e ) {
            return ResponseEntity.status ( HttpStatus.INTERNAL_SERVER_ERROR )
                    .body ( null );
        }
    }

    @PostMapping("/{productId}/images/{imageId}")
    @Operation(summary = "Add a specific image to a product",
            description = "Creates an association between a product and an image")
    public ResponseEntity<ProductResponse> addImageToProduct(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        try {
            ProductResponse response = productService.addImageToProduct ( productId, imageId );
            return ResponseEntity.ok ( response );
        }
        catch ( Exception e ) {
            return ResponseEntity.status ( HttpStatus.INTERNAL_SERVER_ERROR )
                    .body ( null );
        }
    }

    @Operation(summary = "Get products by type", description = "Retrieve products filtered by type (SELL, RENT, SERVICE)")
    @GetMapping("/by-type/{productType}")
    public ResponseEntity<List<ProductResponse>> getProductsByType(@PathVariable ProductType productType) {

        logger.debug ( "Fetching products by type: {}", productType );
        List<ProductResponse> products = productService.getProductsByType ( productType );
        logger.debug ( "Found {} products of type {}", products.size ( ), productType );
        return ResponseEntity.ok ( products );
    }

    @Operation(
            summary = "Filter products",
            description = "Filter products by various criteria. Currently supports filtering by type (SELL, RENT, SERVICE)"
    )
    @GetMapping("/filter")
    public ResponseEntity<List<ProductResponse>> filterProducts(
            @RequestParam(required = false) ProductType type) {

        if ( type != null ) {
            logger.debug ( "Filtering products by type: {}", type );
            List<ProductResponse> products = productService.getProductsByType ( type );
            logger.debug ( "Found {} products of type {}", products.size ( ), type );
            return ResponseEntity.ok ( products );
        } else {
            // If no type is specified, return all products
            logger.debug ( "No filter specified, returning all products" );
            return getAllProducts ( );
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

        logger.debug ( "Buy now request for product ID: {}", id );
        CheckOutResponse response = productService.buyNow ( id, request );
        logger.debug ( "Created checkout with ID: {}", response.getCheckoutId ( ) );
        return ResponseEntity.ok ( response );

    }

    @PostMapping("/{productId}/services/{ourServiceId}")
    @Operation(summary = "Add a service to a product by ID",
            description = "Adds an existing service to a product by ID")
    public ResponseEntity<ProductResponse> addServiceToProduct(
            @PathVariable Long productId,
            @PathVariable Long ourServiceId) {

        ProductResponse response = productService.addServiceToProduct ( productId, ourServiceId );
        return ResponseEntity.ok ( response );

    }

    @DeleteMapping("/{productId}/services/{ourServiceId}")
    @Operation(summary = "Remove a service from a product",
            description = "Removes a service from a product")
    public ResponseEntity<ProductResponse> removeServiceFromProduct(
            @PathVariable Long productId,
            @PathVariable Long ourServiceId) {

        ProductResponse response = productService.removeServiceFromProduct ( productId, ourServiceId );
        return ResponseEntity.ok ( response );

    }

    @GetMapping("/search/{query}")
    public ResponseEntity<List<ProductResponse>> searchByProductName(@PathVariable String query) {

        List<ProductResponse> products = productService.searchByProductName ( query );
        return ResponseEntity.ok ( products );

    }


}
