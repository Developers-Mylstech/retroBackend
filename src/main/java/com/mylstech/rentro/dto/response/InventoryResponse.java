package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.Inventory;
import com.mylstech.rentro.util.StockStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {
    private Long inventoryId;
    private Long quantity;
    private String sku;
    private StockStatus stockStatus;

    public InventoryResponse(Inventory inventory) {
        this.inventoryId = inventory.getInventoryId();
        this.quantity = inventory.getQuantity();
        this.sku = inventory.getSku();
        this.stockStatus = inventory.getStockStatus();
    }
}
