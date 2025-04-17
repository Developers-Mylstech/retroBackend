package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.SpecificationFieldRequest;
import com.mylstech.rentro.dto.response.SpecificationFieldResponse;
import com.mylstech.rentro.service.SpecificationFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/specification-fields")
@RequiredArgsConstructor
public class SpecificationFieldController {

    private final SpecificationFieldService specificationFieldService;

    @GetMapping
    public ResponseEntity<List<SpecificationFieldResponse>> getAllSpecificationFields() {
        return ResponseEntity.ok(specificationFieldService.getAllSpecificationFields());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecificationFieldResponse> getSpecificationFieldById(@PathVariable Long id) {
        return ResponseEntity.ok(specificationFieldService.getSpecificationFieldById(id));
    }

    @PostMapping
    public ResponseEntity<SpecificationFieldResponse> createSpecificationField(@RequestBody SpecificationFieldRequest request) {
        return new ResponseEntity<>(specificationFieldService.createSpecificationField(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpecificationFieldResponse> updateSpecificationField(@PathVariable Long id, @RequestBody SpecificationFieldRequest request) {
        return ResponseEntity.ok(specificationFieldService.updateSpecificationField(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecificationField(@PathVariable Long id) {
        specificationFieldService.deleteSpecificationField(id);
        return ResponseEntity.noContent().build();
    }
}
