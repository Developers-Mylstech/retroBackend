package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.request.RentRequest;
import com.mylstech.rentro.dto.response.RentResponse;

import java.util.List;

public interface RentService {
    List<RentResponse> getAllRents();
    RentResponse getRentById(Long id);
    RentResponse createRent(RentRequest request);
    RentResponse updateRent(Long id, RentRequest request);
    void deleteRent(Long id);
}
