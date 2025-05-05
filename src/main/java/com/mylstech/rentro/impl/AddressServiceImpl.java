package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.AddressRequest;
import com.mylstech.rentro.dto.response.AddressResponse;
import com.mylstech.rentro.model.Address;
import com.mylstech.rentro.model.AppUser;
import com.mylstech.rentro.repository.AddressRepository;
import com.mylstech.rentro.service.AddressService;
import com.mylstech.rentro.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private static final Logger logger = LoggerFactory.getLogger(AddressServiceImpl.class);
    
    private final AddressRepository addressRepository;
    private final SecurityUtils securityUtils;
    
    @Override
    public List<AddressResponse> getAllAddresses() {
        return addressRepository.findAll().stream()
                .map(AddressResponse::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AddressResponse> getCurrentUserAddresses() {
        AppUser currentUser = securityUtils.getCurrentUser();
        return addressRepository.findByUserOrderByIsDefaultDesc(currentUser).stream()
                .map(AddressResponse::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public AddressResponse getAddressById(Long id) {
        Address address = findAddressById(id);
        return new AddressResponse(address);
    }
    
    @Override
    @Transactional
    public AddressResponse createAddress(AddressRequest request) {
        AppUser currentUser = securityUtils.getCurrentUser();
        Address address = request.toAddress();
        address.setUser(currentUser);
        
        // If this is the first address or marked as default, ensure it's the only default
        if (request.isDefault() || addressRepository.countByUser(currentUser) == 0) {
            resetDefaultAddresses(currentUser);
            address.setDefault(true);
        }
        
        address = addressRepository.save(address);
        logger.info("Created address with ID: {} for user: {}", address.getAddressId(), currentUser.getUserId());
        
        return new AddressResponse(address);
    }
    
    @Override
    @Transactional
    public AddressResponse updateAddress(Long id, AddressRequest request) {
        Address address = findAddressById(id);
        
        // Update fields
        address.setStreetAddress(request.getStreetAddress());
        address.setBuildingName(request.getBuildingName());
        address.setFlatNo(request.getFlatNo());
        address.setArea(request.getArea());
        address.setEmirate(request.getEmirate());
        address.setCountry(request.getCountry() != null && !request.getCountry().isEmpty() 
            ? request.getCountry() : "United Arab Emirates");
        address.setLandmark(request.getLandmark());
        address.setAddressType(request.getAddressType());
        
        // Handle default status
        if (request.isDefault() && !address.isDefault()) {
            resetDefaultAddresses(address.getUser());
            address.setDefault(true);
        }
        
        address = addressRepository.save(address);
        logger.info("Updated address with ID: {}", address.getAddressId());
        
        return new AddressResponse(address);
    }
    
    @Override
    @Transactional
    public void deleteAddress(Long id) {
        Address address = findAddressById(id);
        AppUser user = address.getUser();
        
        addressRepository.delete(address);
        logger.info("Deleted address with ID: {}", id);
        
        // If we deleted the default address and there are other addresses, make another one default
        if (address.isDefault()) {
            addressRepository.findByUser(user).stream()
                    .findFirst()
                    .ifPresent(newDefault -> {
                        newDefault.setDefault(true);
                        addressRepository.save(newDefault);
                        logger.info("Set address with ID: {} as new default", newDefault.getAddressId());
                    });
        }
    }
    
    @Override
    @Transactional
    public AddressResponse setDefaultAddress(Long id) {
        Address address = findAddressById(id);
        
        if (!address.isDefault()) {
            resetDefaultAddresses(address.getUser());
            address.setDefault(true);
            address = addressRepository.save(address);
            logger.info("Set address with ID: {} as default", id);
        }
        
        return new AddressResponse(address);
    }
    
    /**
     * Helper method to find an address by ID and verify ownership
     */
    private Address findAddressById(Long id) {
        AppUser currentUser = securityUtils.getCurrentUser();
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + id));
        
        // Verify ownership (unless admin)
        if (!address.getUser().getUserId().equals(currentUser.getUserId()) && 
                !securityUtils.isAdmin()) {
            throw new RuntimeException("You don't have permission to access this address");
        }
        
        return address;
    }
    
    /**
     * Helper method to reset all default addresses for a user
     */
    private void resetDefaultAddresses(AppUser user) {
        addressRepository.findByUserAndIsDefaultTrue(user)
                .ifPresent(defaultAddress -> {
                    defaultAddress.setDefault(false);
                    addressRepository.save(defaultAddress);
                });
    }
}