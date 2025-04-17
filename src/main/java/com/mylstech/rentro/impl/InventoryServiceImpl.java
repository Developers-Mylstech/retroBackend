package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.InventoryRequest;
import com.mylstech.rentro.dto.response.InventoryResponse;
import com.mylstech.rentro.model.Inventory;
import com.mylstech.rentro.repository.InventoryRepository;
import com.mylstech.rentro.service.InventoryService;
import com.mylstech.rentro.util.StockStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    public List<InventoryResponse> getAllInventories() {
        return inventoryRepository.findAll().stream().map(InventoryResponse::new).toList();
    }

    @Override
    public InventoryResponse getInventoryById(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found with id: " + id));
        return new InventoryResponse(inventory);
    }

    @Override
    public InventoryResponse createInventory(InventoryRequest request) {
        Inventory inventory = request.requestToInventory();
        
        // Set default stock status based on quantity if not provided
        if (inventory.getStockStatus() == null) {
            inventory.setStockStatus(inventory.getQuantity() > 0 ? StockStatus.IN_STOCK : StockStatus.OUT_OF_STOCK);
        }
        
        return new InventoryResponse(inventoryRepository.save(inventory));
    }

    @Override
    public InventoryResponse updateInventory(Long id, InventoryRequest request) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found with id: " + id));
        
        if (request.getQuantity() != null) {
            inventory.setQuantity(request.getQuantity());
        }
        
        if (request.getSku() != null) {
            inventory.setSku(request.getSku());
        }
        
        // Update stock status if provided, otherwise update based on quantity
        if (request.getStockStatus() != null) {
            inventory.setStockStatus(request.getStockStatus());
        } else if (request.getQuantity() != null) {
            inventory.setStockStatus(request.getQuantity() > 0 ? StockStatus.IN_STOCK : StockStatus.OUT_OF_STOCK);
        }
        
        return new InventoryResponse(inventoryRepository.save(inventory));
    }

    @Override
    public void deleteInventory(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found with id: " + id));
        inventoryRepository.delete(inventory);
    }
}
