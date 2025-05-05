package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.AddressRequest;
import com.mylstech.rentro.dto.response.AddressResponse;
import com.mylstech.rentro.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
@Tag(name = "Address", description = "Address management APIs")
public class AddressController {
    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);
    
    private final AddressService addressService;
    
    @GetMapping
    @Operation(summary = "Get current user's addresses", description = "Returns all addresses for the current user")
    public ResponseEntity<List<AddressResponse>> getCurrentUserAddresses() {
        return ResponseEntity.ok(addressService.getCurrentUserAddresses());
    }
    
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all addresses", description = "Returns all addresses in the system (admin only)")
    public ResponseEntity<List<AddressResponse>> getAllAddresses() {
        return ResponseEntity.ok(addressService.getAllAddresses());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get address by ID", description = "Returns a specific address by its ID")
    public ResponseEntity<AddressResponse> getAddressById(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.getAddressById(id));
    }
    
    @PostMapping
    @Operation(summary = "Create address", description = "Creates a new address for the current user")
    public ResponseEntity<AddressResponse> createAddress(@Valid @RequestBody AddressRequest request) {
        return new ResponseEntity<>(addressService.createAddress(request), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update address", description = "Updates an existing address")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable Long id, 
            @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.updateAddress(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete address", description = "Deletes an address")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/default")
    @Operation(summary = "Set default address", description = "Sets an address as the default for the current user")
    public ResponseEntity<AddressResponse> setDefaultAddress(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.setDefaultAddress(id));
    }
}