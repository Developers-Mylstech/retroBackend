package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.ProductForRequest;
import com.mylstech.rentro.dto.response.ProductForResponse;
import com.mylstech.rentro.service.ProductForService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product-fors")
@RequiredArgsConstructor
public class ProductForController {

    private final ProductForService productForService;

    @GetMapping
    public ResponseEntity<List<ProductForResponse>> getAllProductFors() {
        return ResponseEntity.ok(productForService.getAllProductFors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductForResponse> getProductForById(@PathVariable Long id) {
        return ResponseEntity.ok(productForService.getProductForById(id));
    }

    @PostMapping
    public ResponseEntity<ProductForResponse> createProductFor(@RequestBody ProductForRequest request) {
        return new ResponseEntity<>(productForService.createProductFor(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductForResponse> updateProductFor(@PathVariable Long id, @RequestBody ProductForRequest request) {
        return ResponseEntity.ok(productForService.updateProductFor(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductFor(@PathVariable Long id) {
        productForService.deleteProductFor(id);
        return ResponseEntity.noContent().build();
    }
}
