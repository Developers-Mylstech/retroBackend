package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.model.Inventory;
import com.mylstech.rentro.util.StockStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequest {
    private Long quantity;
    private String sku;
    private StockStatus stockStatus;
    
    public Inventory requestToInventory() {
        Inventory inventory = new Inventory();
        inventory.setQuantity(quantity);
        inventory.setSku(sku);
        inventory.setStockStatus(stockStatus);
        return inventory;
    }
}
