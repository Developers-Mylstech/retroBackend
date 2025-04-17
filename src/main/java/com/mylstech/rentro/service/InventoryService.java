package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.request.InventoryRequest;
import com.mylstech.rentro.dto.response.InventoryResponse;

import java.util.List;

public interface InventoryService {
    List<InventoryResponse> getAllInventories();
    InventoryResponse getInventoryById(Long id);
    InventoryResponse createInventory(InventoryRequest request);
    InventoryResponse updateInventory(Long id, InventoryRequest request);
    void deleteInventory(Long id);
}
