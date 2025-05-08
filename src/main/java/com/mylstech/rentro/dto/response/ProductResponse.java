package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Transfer Object for product responses.
 * Contains all the product information including nested objects.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long productId;
    private String name;
    private String description;
    private String longDescription;
    private String manufacturer;
    private String supplierName;
    private String supplierCode;
    private String modelNo;
    private CategoryResponse category;
    private CategoryResponse subCategory;
    private BrandResponse brand;
    private InventoryResponse inventory;
    private ProductForResponse productFor;
    private List<SpecificationResponse> specifications;
    private List<String> imageUrls;
    private List<String> keyFeatures;
    private List<String> tagNKeywords;
    private List<ImageDTO> images;

    public ProductResponse(Product product) {
        this.productId = product.getProductId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.longDescription = product.getLongDescription();
        this.manufacturer=product.getManufacturer ();
        if (product.getCategory() != null) {
            this.category = new CategoryResponse(product.getCategory());
        }

        if (product.getSubCategory() != null) {
            this.subCategory = new CategoryResponse(product.getSubCategory());
        }

        if (product.getBrand() != null) {
            this.brand = new BrandResponse(product.getBrand());
        }

        if (product.getInventory() != null) {
            this.inventory = new InventoryResponse(product.getInventory());
        }

        if (product.getProductFor() != null) {
            this.productFor = new ProductForResponse(product.getProductFor());
        }

        if (product.getSpecification () != null && !product.getSpecification ().isEmpty()) {
            this.specifications = product.getSpecification ().stream()
                .map(SpecificationResponse::new)
                .toList();
        }


        // For backward compatibility
        this.imageUrls = product.getImageUrls();
        
        // New image entity integration
        if (product.getImages() != null) {
            this.images = product.getImages().stream()
                    .map(image -> new ImageDTO(image.getImageId(), image.getImageUrl()))
                    .collect(Collectors.toList());
        } else {
            this.images = new ArrayList<>();
        }
        if (product.getKeyFeatures () != null) {
            this.keyFeatures = product.getKeyFeatures ();
        }
        if (product.getTagNKeywords () != null) {
            this.tagNKeywords = product.getTagNKeywords ();
        }
        this.supplierName = product.getSupplierName ();
        this.supplierCode = product.getSupplierCode ();
        this.modelNo = product.getModelNo ();
    }
}
