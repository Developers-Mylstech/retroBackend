package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.RentRequest;
import com.mylstech.rentro.dto.response.RentResponse;
import com.mylstech.rentro.service.RentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rents")
@RequiredArgsConstructor
public class RentController {

    private final RentService rentService;

    @GetMapping
    public ResponseEntity<List<RentResponse>> getAllRents() {
        return ResponseEntity.ok(rentService.getAllRents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentResponse> getRentById(@PathVariable Long id) {
        return ResponseEntity.ok(rentService.getRentById(id));
    }

    @PostMapping
    public ResponseEntity<RentResponse> createRent(@RequestBody RentRequest request) {
        return new ResponseEntity<>(rentService.createRent(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RentResponse> updateRent(@PathVariable Long id, @RequestBody RentRequest request) {
        return ResponseEntity.ok(rentService.updateRent(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRent(@PathVariable Long id) {
        rentService.deleteRent(id);
        return ResponseEntity.noContent().build();
    }
}
