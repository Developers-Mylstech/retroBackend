package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.SpecificationRequest;
import com.mylstech.rentro.dto.response.SpecificationResponse;
import com.mylstech.rentro.service.SpecificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/specifications")
@RequiredArgsConstructor
public class SpecificationController {

    private final SpecificationService specificationService;

    @GetMapping
    public ResponseEntity<List<SpecificationResponse>> getAllSpecifications() {
        return ResponseEntity.ok(specificationService.getAllSpecifications());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecificationResponse> getSpecificationById(@PathVariable Long id) {
        return ResponseEntity.ok(specificationService.getSpecificationById(id));
    }

    @PostMapping
    public ResponseEntity<SpecificationResponse> createSpecification(@RequestBody SpecificationRequest request) {
        return new ResponseEntity<>(specificationService.createSpecification(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpecificationResponse> updateSpecification(@PathVariable Long id, @RequestBody SpecificationRequest request) {
        return ResponseEntity.ok(specificationService.updateSpecification(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecification(@PathVariable Long id) {
        specificationService.deleteSpecification(id);
        return ResponseEntity.noContent().build();
    }
}
