package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.util.StockStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object for creating or updating a product.
 * Contains all the necessary fields for a product including nested objects.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    private String name;
    private String description;
    private String longDescription;
    private String manufacturer;
    private Long brandId;
    private List<String> imageUrls;
    private List<SpecificationRequest> specifications;
    private ProductForRequest productFor;
    private Long categoryId;
    private Long subCategoryId;
    private InventoryRequest inventory;
    private List<String> keyFeatures;
}
