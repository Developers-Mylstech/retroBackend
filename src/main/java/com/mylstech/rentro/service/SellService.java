package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.request.SellRequest;
import com.mylstech.rentro.dto.response.SellResponse;

import java.util.List;

public interface SellService {
    List<SellResponse> getAllSells();
    SellResponse getSellById(Long id);
    SellResponse createSell(SellRequest request);
    SellResponse updateSell(Long id, SellRequest request);
    void deleteSell(Long id);
}
