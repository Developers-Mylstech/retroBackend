package com.mylstech.rentro.model;

import com.mylstech.rentro.util.StockStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "inventories")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;
    private Long quantity;
    private String sku;
    @Enumerated
    private StockStatus stockStatus;
}
