package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.ServiceRequest;
import com.mylstech.rentro.dto.response.ServiceResponse;
import com.mylstech.rentro.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @GetMapping
    public ResponseEntity<List<ServiceResponse>> getAllServices() {
        return ResponseEntity.ok(serviceService.getAllServices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> getServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceService.getServiceById(id));
    }

    @PostMapping
    public ResponseEntity<ServiceResponse> createService(@RequestBody ServiceRequest request) {
        return new ResponseEntity<>(serviceService.createService(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> updateService(@PathVariable Long id, @RequestBody ServiceRequest request) {
        return ResponseEntity.ok(serviceService.updateService(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}
