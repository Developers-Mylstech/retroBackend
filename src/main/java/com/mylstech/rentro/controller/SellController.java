package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.SellRequest;
import com.mylstech.rentro.dto.response.SellResponse;
import com.mylstech.rentro.service.SellService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sells")
@RequiredArgsConstructor
public class SellController {

    private final SellService sellService;

    @GetMapping
    public ResponseEntity<List<SellResponse>> getAllSells() {
        return ResponseEntity.ok(sellService.getAllSells());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SellResponse> getSellById(@PathVariable Long id) {
        return ResponseEntity.ok(sellService.getSellById(id));
    }

    @PostMapping
    public ResponseEntity<SellResponse> createSell(@RequestBody SellRequest request) {
        return new ResponseEntity<>(sellService.createSell(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SellResponse> updateSell(@PathVariable Long id, @RequestBody SellRequest request) {
        return ResponseEntity.ok(sellService.updateSell(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSell(@PathVariable Long id) {
        sellService.deleteSell(id);
        return ResponseEntity.noContent().build();
    }
}
