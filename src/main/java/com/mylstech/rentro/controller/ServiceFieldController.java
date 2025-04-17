package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.ServiceFieldRequest;
import com.mylstech.rentro.dto.response.ServiceFieldResponse;
import com.mylstech.rentro.service.ServiceFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service-fields")
@RequiredArgsConstructor
public class ServiceFieldController {

    private final ServiceFieldService serviceFieldService;

    @GetMapping
    public ResponseEntity<List<ServiceFieldResponse>> getAllServiceFields() {
        return ResponseEntity.ok(serviceFieldService.getAllServiceFields());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceFieldResponse> getServiceFieldById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceFieldService.getServiceFieldById(id));
    }

    @PostMapping
    public ResponseEntity<ServiceFieldResponse> createServiceField(@RequestBody ServiceFieldRequest request) {
        return new ResponseEntity<>(serviceFieldService.createServiceField(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceFieldResponse> updateServiceField(@PathVariable Long id, @RequestBody ServiceFieldRequest request) {
        return ResponseEntity.ok(serviceFieldService.updateServiceField(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServiceField(@PathVariable Long id) {
        serviceFieldService.deleteServiceField(id);
        return ResponseEntity.noContent().build();
    }
}
