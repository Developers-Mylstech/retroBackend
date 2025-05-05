package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.request.AddressRequest;
import com.mylstech.rentro.dto.response.AddressResponse;

import java.util.List;

public interface AddressService {
    List<AddressResponse> getAllAddresses();
    List<AddressResponse> getCurrentUserAddresses();
    AddressResponse getAddressById(Long id);
    AddressResponse createAddress(AddressRequest request);
    AddressResponse updateAddress(Long id, AddressRequest request);
    void deleteAddress(Long id);
    AddressResponse setDefaultAddress(Long id);
}