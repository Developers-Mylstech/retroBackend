package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.Brand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandResponse {
    private Long brandId;
    private String name;
//    private List<String> imageUrls; // For backward compatibility
    private ImageDTO image; // New field for single image

    public BrandResponse(Brand brand) {
        this.brandId = brand.getBrandId();
        this.name = brand.getName();
        
        // Initialize empty list for backward compatibility
//        this.imageUrls = new ArrayList<>();
        
        // Handle Image entity (new approach)
        if (brand.getImage() != null) {
            this.image = new ImageDTO(brand.getImage());
            
            // For backward compatibility, add the image URL to imageUrls
//            if (brand.getImage().getImageUrl() != null) {
//                this.imageUrls.add(brand.getImage().getImageUrl());
//            }
        }
        
        // If we still have no URLs but have deprecated imageUrls collection, use those
//        if (this.imageUrls.isEmpty() && brand.getImageUrls() != null && !brand.getImageUrls().isEmpty()) {
//            this.imageUrls.addAll(brand.getImageUrls());
//        }
    }
}
